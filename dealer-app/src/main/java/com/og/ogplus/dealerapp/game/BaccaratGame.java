package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.BaccaratPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.*;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.ScannerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.og.ogplus.common.enums.BaccaratPosition.*;

@Slf4j

//@ConditionalOnProperty(name = "app.game-category", havingValue = "BACCARAT")
@ConditionalOnExpression("'${app.game-category}'.equals('BACCARAT') && '${app.isvip}'.equals('false')")
@Component
public class BaccaratGame extends ShuffleGame {
    private static final boolean[][] BANKER_DRAW = {
            //              Player's      Third       Card
            //0     1      2     3     4     5     6     7     8     9
            {true, true, true, true, true, true, true, true, true, true,},              //  0
            {true, true, true, true, true, true, true, true, true, true,},              //  1
            {true, true, true, true, true, true, true, true, true, true,},              //  2   Banker's
            {true, true, true, true, true, true, true, true, false, true,},             //  3
            {false, false, true, true, true, true, true, true, false, false,},          //  4   Total
            {false, false, false, false, true, true, true, true, false, false,},        //  5
            {false, false, false, false, false, false, true, true, false, false,},      //  6
            {false, false, false, false, false, false, false, false, false, false,},    //  7
    };

    private Card[] playerCards = new Card[3];
    private Card[] bankerCards = new Card[3];

    private boolean singleScanner;

    private List<Listener> listeners;

    private AtomicBoolean changeResultActionDetected = new AtomicBoolean(false);

    public BaccaratGame(Table table) {
        super(table);
    }

    private static List<Result> calculateGameResult(Card[] playerCards, Card[] bankerCards) throws UnsatisfiedBaccaratRuleException {
        if (playerCards[0] == null || playerCards[1] == null || bankerCards[0] == null || bankerCards[1] == null) {
            throw new UnsatisfiedBaccaratRuleException();
        }

        boolean isPlayer3rdCardNeeded = isPlayer3rdCardNeeded(playerCards, bankerCards);
        if (isPlayer3rdCardNeeded && playerCards[2] == null) {
            throw new UnsatisfiedBaccaratRuleException("Need Player 3rd Card");
        } else if (!isPlayer3rdCardNeeded && playerCards[2] != null) {
            throw new UnsatisfiedBaccaratRuleException("Player 3rd Card No Need");
        }

        boolean isBanker3rdCardNeeded = isBanker3rdCardNeeded(playerCards, bankerCards);
        if (isBanker3rdCardNeeded && bankerCards[2] == null) {
            throw new UnsatisfiedBaccaratRuleException("Need Banker 3rd Card");
        } else if (!isBanker3rdCardNeeded && bankerCards[2] != null) {
            throw new UnsatisfiedBaccaratRuleException("Banker 3rd Card No Need");
        }

        int playerTotal = Arrays.stream(playerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10;
        int bankerTotal = Arrays.stream(bankerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10;

        List<Result> results = new ArrayList<>();
        results.add(playerTotal > bankerTotal ? Result.PLAYER : playerTotal < bankerTotal ? Result.BANKER : Result.TIE);

        if (bankerCards[0].getRank().getValue() == bankerCards[1].getRank().getValue()) {
            results.add(Result.BANKER_PAIR);
        }

        if (playerCards[0].getRank().getValue() == playerCards[1].getRank().getValue()) {
            results.add(Result.PLAYER_PAIR);
        }

        if (bankerTotal > playerTotal && bankerTotal == 6) {
            results.add(Result.SUPER_SIX);
        }

        return results;
    }

    @Override
    protected boolean validateScannerSetting() {
        int scannerCount = getScannerCount();
        switch (scannerCount) {
            case 1:
                singleScanner = true;
                return true;
            case 6:
                singleScanner = false;
                return true;
            default:
                log.warn("Baccarat only support single or 6 scanner devices.");
                showErrorMessage("Baccarat only support single or 6 scanner devices.");
                return false;
        }
    }

    @Override
    protected void reset() {
        super.reset();
        for (int i = 0; i < 3; ++i) {
            playerCards[i] = null;
            bankerCards[i] = null;
        }
    }

    @Override
    protected void deal() throws InterruptedException {
        if (singleScanner) {
            playerCards[0] = dealCard(PLAYER1);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();

            bankerCards[0] = dealCard(BaccaratPosition.BANKER1);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();

            playerCards[1] = dealCard(PLAYER2);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();

            bankerCards[1] = dealCard(BaccaratPosition.BANKER2);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();
        } else {
            Card[] cards = dealCard(PLAYER1, PLAYER2);
            playerCards[0] = cards[0];
            playerCards[1] = cards[1];
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();

            cards = dealCard(BaccaratPosition.BANKER1, BaccaratPosition.BANKER2);
            bankerCards[0] = cards[0];
            bankerCards[1] = cards[1];
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();
        }

        squeeze(getSqueezeTime1(), PLAYER1, PLAYER2, BaccaratPosition.BANKER1, BaccaratPosition.BANKER2);

        do {
            if (!isPlayer3rdCardNeeded(playerCards, bankerCards)) {
                break;
            }

            if (playerCards[2] == null) {
                playerCards[2] = dealCard(PLAYER3);    //  get null if any card changing operation occur
            }

            if (playerCards[2] != null) {
                int bankerTotal = (bankerCards[0].getValue() + bankerCards[1].getValue()) % 10;
                if (bankerTotal <= 2) {
                    bankerCards[2] = dealCard(BaccaratPosition.BANKER3);
                    if (bankerCards[2] != null) {
                        Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                        updateGameInfo();
                        squeeze(getSqueezeTime2(), PLAYER3, BaccaratPosition.BANKER3);
                        break;
                    }
                } else {
                    Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                    updateGameInfo();
                    squeeze(getSqueezeTime2(), PLAYER3);
                    break;
                }
            }
        } while (true);

        do {
            if (isBanker3rdCardNeeded(playerCards, bankerCards) && bankerCards[2] == null) {
                bankerCards[2] = dealCard(BaccaratPosition.BANKER3);
                if (bankerCards[2] != null) {
                    Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                    updateGameInfo();
                    squeeze(getSqueezeTime2(), BaccaratPosition.BANKER3);
                    break;
                }
            } else {
                break;
            }
        } while (true);
    }

    @Override
    protected Card dealCard(Position position) throws InterruptedException {
        changeResultActionDetected.set(false);
        Future<Card> future = getScheduler().submit(() -> super.dealCard(position));
        Future<Card> detectChangeCardActionFuture = getScheduler().submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (changeResultActionDetected.get()) {
                    return getCard((BaccaratPosition) position);
                }

                Thread.sleep(100);
            }
            throw new InterruptedException();
        });

        try {
            while (true) {
                try {
                    if (future.isDone()) {
                        return future.get();
                    } else if (detectChangeCardActionFuture.isDone()) {
                        return detectChangeCardActionFuture.get();
                    }
                } catch (ExecutionException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
                Thread.sleep(100);
            }
        } finally {
            future.cancel(true);
            detectChangeCardActionFuture.cancel(true);
        }
    }

    @Override
    public Card getCard(Position position) {
        if (position instanceof BaccaratPosition) {
            switch ((BaccaratPosition)position) {
                case PLAYER1:
                    return playerCards[0];
                case PLAYER2:
                    return playerCards[1];
                case PLAYER3:
                    return playerCards[2];
                case BANKER1:
                    return bankerCards[0];
                case BANKER2:
                    return bankerCards[1];
                case BANKER3:
                    return bankerCards[2];
            }
        }
        return null;
    }

    @Override
    protected Optional<Set<ScannerService>> getScannerService(Position position) {
        if (position == BaccaratPosition.BANKER3 && playerCards[2] == null) {
            return Optional.ofNullable(scannerServicesMap.get(PLAYER3));
        } else {
            return super.getScannerService(position);
        }
    }

    private static boolean isNatural(Card card1, Card card2) {
        return (card1.getValue() + card2.getValue()) % 10 >= 8;
    }

    private static boolean isPlayer3rdCardNeeded(Card[] playerCards, Card[] bankerCards) {
        int playerTotal = (playerCards[0].getValue() + playerCards[1].getValue()) % 10;
        return !isNatural(playerCards[0], playerCards[1]) && !isNatural(bankerCards[0], bankerCards[1]) && playerTotal < 6;
    }

    private static boolean isBanker3rdCardNeeded(Card[] playerCards, Card[] bankerCards) {
        if (isNatural(playerCards[0], playerCards[1]) || isNatural(bankerCards[0], bankerCards[1])) {
            return false;
        }
        int bankerTotal = (bankerCards[0].getValue() + bankerCards[1].getValue()) % 10;
        if (bankerTotal == 7) {
            return false;
        }

        if (playerCards[2] == null) {   //player 1st + player 2nd = 6 or 7
            return bankerTotal != 6;
        } else {
            return BANKER_DRAW[bankerTotal][playerCards[2].getValue()];
        }
    }

    @Override
    protected void doBeforeRoundEnd() throws InterruptedException {
        showTempResult();

        super.doBeforeRoundEnd();
    }

    @Override
    protected GameResult calculateResult() {
        return new BaccaratGameResult(getStage().clone(), Arrays.copyOf(playerCards, playerCards.length),
                Arrays.copyOf(bankerCards, bankerCards.length));
    }

    private void showTempResult() {
        try {
            List<Result> results = calculateGameResult(playerCards, bankerCards);
            listeners.forEach(listener -> getScheduler().execute(() -> listener.onTempGameResult(results)));
        } catch (UnsatisfiedBaccaratRuleException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alert(e.getMessage());
        }
    }


    @Override
    protected Card generateCard(Suit suit, Rank rank) {
        return new BaccaratCard(suit, rank);
    }

    @Override
    protected List<Position> getPositions() {
        return Arrays.asList(BaccaratPosition.values());
    }

    public void setCard(Stage stage, BaccaratPosition position, BaccaratCard card) throws FailedChangeGameResultException {
        if (isAllowChangeGameResult() && getStage().equals(stage)) {
            getChangeLock().lock();
            try {
                if (isAllowChangeGameResult() && getStage().equals(stage)) {
                    switch (position) {
                        case PLAYER1:
                            playerCards[0] = card;
                            break;
                        case PLAYER2:
                            playerCards[1] = card;
                            break;
                        case PLAYER3:
                            playerCards[2] = card;
                            break;
                        case BANKER1:
                            bankerCards[0] = card;
                            break;
                        case BANKER2:
                            bankerCards[1] = card;
                            break;
                        case BANKER3:
                            bankerCards[2] = card;
                            break;
                    }
                    changeResultActionDetected.set(true);
                    onCardScanned(position, card, true);
                    Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                    updateGameInfo();
                    showTempResult();
                } else {
                    throw new FailedChangeGameResultException(String.format("Not allowed to change game result at stage(%s, %s-%s)",
                            stage.getDate(), stage.getShoe(), stage.getRound()));
                }
            } catch (InterruptedException e) {
                throw new FailedChangeGameResultException(e);
            } finally {
                getChangeLock().unlock();
            }
        } else {
            throw new FailedChangeGameResultException(String.format("Not allowed to change game result at stage(%s, %s-%s)",
                    stage.getDate(), stage.getShoe(), stage.getRound()));
        }

    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public enum Result {
        PLAYER, BANKER, TIE, PLAYER_PAIR, BANKER_PAIR, SUPER_SIX
    }

    public interface Listener extends ShuffleGame.Listener {
        default void onTempGameResult(List<BaccaratGame.Result> results) {
        }
    }

    @Getter
    public static class BaccaratGameResult implements GameResult {
        private static final long serialVersionUID = 1406782741595931290L;

        private Stage stage;

        private Card[] playerCards;
        private Card[] bankerCards;

        private int playerTotal;
        private int bankerTotal;

        private List<Result> result;

        private BaccaratGameResult(Stage stage, Card[] playerCards, Card[] bankerCards) {
            this.stage = stage;

            this.playerCards = playerCards;
            this.bankerCards = bankerCards;

            playerTotal = Arrays.stream(playerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10;
            bankerTotal = Arrays.stream(bankerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10;

            try {
                this.result = calculateGameResult(playerCards, bankerCards);
            } catch (UnsatisfiedBaccaratRuleException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private static class UnsatisfiedBaccaratRuleException extends Exception {
        private UnsatisfiedBaccaratRuleException() {
        }

        private UnsatisfiedBaccaratRuleException(String message) {
            super(message);
        }
    }

}
