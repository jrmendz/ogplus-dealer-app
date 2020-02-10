package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.*;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.exception.UnexpectedScannerSettingException;
import com.og.ogplus.dealerapp.game.model.DefaultDealerAI;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.ScannerService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "NIUNIU")
@Component
public class BullbullGame extends CardGame {

    private static Card[] firstPlayerCards = new Card[5];
    private static Card[] secondPlayerCards = new Card[5];
    private static Card[] thirdPlayerCards = new Card[5];
    private static Card[] bankerCards = new Card[5];

    private DefaultDealerAI DefaultDealerAI;

    private Card firstCard;

    private List<Listener> listeners;

    private AtomicBoolean changeResultActionDetected = new AtomicBoolean(false);

    private Future<Card> scanFirstCardTask;

    public BullbullGame(Table table) {
        super(table);
    }

    @Autowired
    public void setDealerAI(DefaultDealerAI DefaultDealerAI) {
        this.DefaultDealerAI = DefaultDealerAI;
    }

    @Override
    public void init() {
        super.init();
        DefaultDealerAI.setDesks(1);
        DefaultDealerAI.setShuffleCard(false);
        DefaultDealerAI.shuffle();
    }

    @Override
    protected void configureScanner() {
        final Map<String, ScannerService> scannerServiceMap;
        try {
            //TODO Only support one-to-one(position-scanner), have to support one-to-many(position-scanner)
            scannerServiceMap = getScannerSettingHelper().getScannerServices(Collections.singletonList("SCANNER"));
            scannerServicesMap.putAll(Arrays.stream(BullbullPosition.values())
                    .collect(Collectors.toMap(position -> position, position -> {
                        Set<ScannerService> set = new HashSet<>();
                        set.add(scannerServiceMap.get("SCANNER"));
                        return set;
                    })));
        } catch (UnexpectedScannerSettingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public static CardsType calculateCardsType(Card[] cards) {
        Assert.isTrue(cards.length == 5);
        if (Arrays.stream(cards).allMatch(card -> card.getRank() == Rank.JACK || card.getRank() == Rank.QUEEN || card.getRank() == Rank.KING)) {
            return CardsType.FNIUNIU;
        }

        int[] cardValue = Arrays.stream(cards).mapToInt(card -> card.getRank().getValue() >= 10 ? 10 : card.getRank().getValue()).toArray();
        int cardSum = Arrays.stream(cardValue).sum();

        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 5; j++) {
                int twoCardSum = cardValue[i] + cardValue[j];
                if ((cardSum - twoCardSum) % 10 == 0) {
                    return CardsType.parseResult(twoCardSum % 10 == 0 ? 10 : twoCardSum % 10);
                }
            }
        }

        return CardsType.NO_NIU;
    }

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

    @Override
    protected boolean validateScannerSetting() {
        return getScannerCount() == 1;
    }

    @Override
    protected void reset() {
        super.reset();
        for (int i = 0; i < 5; ++i) {
            firstPlayerCards[i] = null;
            secondPlayerCards[i] = null;
            thirdPlayerCards[i] = null;
            bankerCards[i] = null;
            firstCard = null;
        }
    }


    @Override
    protected void deal() throws InterruptedException {
        if (!scanFirstCardTask.isDone()) {
            scanFirstCardTask.cancel(true);
            firstCard = scanFirstCard();
        } else {
            try {
                firstCard = scanFirstCardTask.get();
            } catch (ExecutionException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                firstCard = scanFirstCard();
            }
        }

        switch (firstCard.getRank()) {
            case ACE:
            case FIVE:
            case NINE:
            case KING:
                showCard(1);
                showCard(2);
                showCard(3);
                showCard(4);
                break;
            case TWO:
            case SIX:
            case TEN:
                showCard(2);
                showCard(3);
                showCard(4);
                showCard(1);
                break;
            case THREE:
            case SEVEN:
            case JACK:
                showCard(3);
                showCard(4);
                showCard(1);
                showCard(2);
                break;
            case FOUR:
            case EIGHT:
            case QUEEN:
                showCard(4);
                showCard(1);
                showCard(2);
                showCard(3);
                break;
        }
    }

    private void showCard(int order) throws InterruptedException {
        switch (order) {
            case 1:
                bankerCards[0] = dealCard(BullbullPosition.BANKER1ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                bankerCards[1] = dealCard(BullbullPosition.BANKER2ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                bankerCards[2] = dealCard(BullbullPosition.BANKER3ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                bankerCards[3] = dealCard(BullbullPosition.BANKER4ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                bankerCards[4] = dealCard(BullbullPosition.BANKER5ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();
                break;

            case 2:
                firstPlayerCards[0] = dealCard(BullbullPosition.FIRSTPLAYER1ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                firstPlayerCards[1] = dealCard(BullbullPosition.FIRSTPLAYER2ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                firstPlayerCards[2] = dealCard(BullbullPosition.FIRSTPLAYER3ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                firstPlayerCards[3] = dealCard(BullbullPosition.FIRSTPLAYER4ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                firstPlayerCards[4] = dealCard(BullbullPosition.FIRSTPLAYER5ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                break;
            case 3:
                secondPlayerCards[0] = dealCard(BullbullPosition.SECONDPLAYER1ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                secondPlayerCards[1] = dealCard(BullbullPosition.SECONDPLAYER2ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                secondPlayerCards[2] = dealCard(BullbullPosition.SECONDPLAYER3ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                secondPlayerCards[3] = dealCard(BullbullPosition.SECONDPLAYER4ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                secondPlayerCards[4] = dealCard(BullbullPosition.SECONDPLAYER5ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                break;
            case 4:
                thirdPlayerCards[0] = dealCard(BullbullPosition.THIRDPLAYER1ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                thirdPlayerCards[1] = dealCard(BullbullPosition.THIRDPLAYER2ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                thirdPlayerCards[2] = dealCard(BullbullPosition.THIRDPLAYER3ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                thirdPlayerCards[3] = dealCard(BullbullPosition.THIRDPLAYER4ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                thirdPlayerCards[4] = dealCard(BullbullPosition.THIRDPLAYER5ST);
                Thread.sleep(DATA_PROCESS_PROVE_DELAY);
                updateGameInfo();

                break;
        }
    }

    @Override
    protected void doBeforeRoundEnd() throws InterruptedException {
        showTempResult();

        super.doBeforeRoundEnd();
    }


    @Override
    protected void doAfterRoundStart() {
        super.doAfterRoundStart();
        DefaultDealerAI.shuffle();
        scanFirstCardTask = getScheduler().submit(this::scanFirstCard);
    }

    private Card scanFirstCard() throws InterruptedException {
        Card card = dealCard(BullbullPosition.FIRSTCARD);
        Thread.sleep(DATA_PROCESS_PROVE_DELAY);
        updateGameInfo();
        return card;
    }

    @Override
    protected boolean validateStage(Stage stage) {
        if (stage == null || stage.getShoe() != null) {
            return false;
        }

        return stage.getRound() > getStage().getRound();
    }

    @Override
    protected GameResult calculateResult() {
        return new BullbullGameResult(getStage().clone(), Arrays.copyOf(firstPlayerCards, firstPlayerCards.length),
                Arrays.copyOf(secondPlayerCards, secondPlayerCards.length),
                Arrays.copyOf(thirdPlayerCards, thirdPlayerCards.length),
                Arrays.copyOf(bankerCards, bankerCards.length));
    }

    private void showTempResult() {
        CardsType bankerCardsType = calculateCardsType(bankerCards);
        CardsType player1CardsType = calculateCardsType(firstPlayerCards);
        CardsType player2CardsType = calculateCardsType(secondPlayerCards);
        CardsType player3CardsType = calculateCardsType(thirdPlayerCards);

        Result[] results = new Result[3];
        results[0] = bankerCardsType.value > player1CardsType.value ? Result.BANKER :
                (bankerCardsType.value < player1CardsType.value ? Result.PLAYER1 :
                        (compareMaxCard(bankerCards, firstPlayerCards) > 0 ? Result.BANKER : Result.PLAYER1));
        results[1] = bankerCardsType.value > player2CardsType.value ? Result.BANKER :
                (bankerCardsType.value < player2CardsType.value ? Result.PLAYER2 :
                        (compareMaxCard(bankerCards, secondPlayerCards) > 0 ? Result.BANKER : Result.PLAYER2));
        results[2] = bankerCardsType.value > player3CardsType.value ? Result.BANKER :
                (bankerCardsType.value < player3CardsType.value ? Result.PLAYER3 :
                        (compareMaxCard(bankerCards, thirdPlayerCards) > 0 ? Result.BANKER : Result.PLAYER3));

        listeners.forEach(listener -> getScheduler().execute(() -> listener.onTempGameResult(results)));
    }


    @Override
    protected Card generateCard(Suit suit, Rank rank) {
        return new BaccaratCard(suit, rank);
    }

    @Override
    protected List<Position> getPositions() {
        return Arrays.asList(BullbullPosition.values());
    }

    public void setCard(Stage stage, BullbullPosition position, BullbullCard card) throws FailedChangeGameResultException {
        if (isAllowChangeGameResult() && getStage().equals(stage)) {
            getChangeLock().lock();
            try {
                if (isAllowChangeGameResult() && getStage().equals(stage)) {
                    switch (position) {
                        case BANKER1ST:
                            bankerCards[0] = card;
                            break;
                        case BANKER2ST:
                            bankerCards[1] = card;
                            break;
                        case BANKER3ST:
                            bankerCards[2] = card;
                            break;
                        case BANKER4ST:
                            bankerCards[3] = card;
                            break;
                        case BANKER5ST:
                            bankerCards[4] = card;
                            break;

                        case FIRSTPLAYER1ST:
                            firstPlayerCards[0] = card;
                            break;
                        case FIRSTPLAYER2ST:
                            firstPlayerCards[1] = card;
                            break;
                        case FIRSTPLAYER3ST:
                            firstPlayerCards[2] = card;
                            break;
                        case FIRSTPLAYER4ST:
                            firstPlayerCards[3] = card;
                            break;
                        case FIRSTPLAYER5ST:
                            firstPlayerCards[4] = card;
                            break;

                        case SECONDPLAYER1ST:
                            secondPlayerCards[0] = card;
                            break;
                        case SECONDPLAYER2ST:
                            secondPlayerCards[1] = card;
                            break;
                        case SECONDPLAYER3ST:
                            secondPlayerCards[2] = card;
                            break;
                        case SECONDPLAYER4ST:
                            secondPlayerCards[3] = card;
                            break;
                        case SECONDPLAYER5ST:
                            secondPlayerCards[4] = card;
                            break;


                        case THIRDPLAYER1ST:
                            thirdPlayerCards[0] = card;
                            break;
                        case THIRDPLAYER2ST:
                            thirdPlayerCards[1] = card;
                            break;
                        case THIRDPLAYER3ST:
                            thirdPlayerCards[2] = card;
                            break;
                        case THIRDPLAYER4ST:
                            thirdPlayerCards[3] = card;
                            break;
                        case THIRDPLAYER5ST:
                            thirdPlayerCards[4] = card;
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

    @Override
    public Card getCard(Position position) {
        if(position instanceof  BullbullPosition) {
            switch ((BullbullPosition) position) {
                case FIRSTCARD:
                    return firstCard;
                case BANKER1ST:
                    return bankerCards[0];
                case BANKER2ST:
                    return bankerCards[1];
                case BANKER3ST:
                    return bankerCards[2];
                case BANKER4ST:
                    return bankerCards[3];
                case BANKER5ST:
                    return bankerCards[4];

                case FIRSTPLAYER1ST:
                    return firstPlayerCards[0];
                case FIRSTPLAYER2ST:
                    return firstPlayerCards[1];
                case FIRSTPLAYER3ST:
                    return firstPlayerCards[2];
                case FIRSTPLAYER4ST:
                    return firstPlayerCards[3];
                case FIRSTPLAYER5ST:
                    return firstPlayerCards[4];

                case SECONDPLAYER1ST:
                    return secondPlayerCards[0];
                case SECONDPLAYER2ST:
                    return secondPlayerCards[1];
                case SECONDPLAYER3ST:
                    return secondPlayerCards[2];
                case SECONDPLAYER4ST:
                    return secondPlayerCards[3];
                case SECONDPLAYER5ST:
                    return secondPlayerCards[4];

                case THIRDPLAYER1ST:
                    return thirdPlayerCards[0];
                case THIRDPLAYER2ST:
                    return thirdPlayerCards[1];
                case THIRDPLAYER3ST:
                    return thirdPlayerCards[2];
                case THIRDPLAYER4ST:
                    return thirdPlayerCards[3];
                case THIRDPLAYER5ST:
                    return thirdPlayerCards[4];
            }
        }
        return null;
    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public enum CardsType {
        NO_NIU(0), NIU1(1), NIU2(2), NIU3(3), NIU4(4), NIU5(5), NIU6(6), NIU7(7), NIU8(8), NIU9(9), NIUNIU(10), FNIUNIU(11),
        ;

        int value;

        CardsType(int value) {
            this.value = value;
        }

        public static CardsType parseResult(int i) {
            switch (i) {
                case 0:
                    return NO_NIU;
                case 1:
                    return NIU1;
                case 2:
                    return NIU2;
                case 3:
                    return NIU3;
                case 4:
                    return NIU4;
                case 5:
                    return NIU5;
                case 6:
                    return NIU6;
                case 7:
                    return NIU7;
                case 8:
                    return NIU8;
                case 9:
                    return NIU9;
                case 10:
                    return NIUNIU;
                case 11:
                    return FNIUNIU;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public enum Result {
        BANKER, PLAYER1, PLAYER2, PLAYER3
    }


    public interface Listener extends ShuffleGame.Listener {
        default void onTempGameResult(Result[] results) {
        }

    }

    @Getter
    public static class BullbullGameResult implements GameResult {
        private static final long serialVersionUID = 1406782741595931290L;

        private Stage stage;

        private Card[] firstPlayerCards;
        private Card[] secondPlayerCards;
        private Card[] thirdPlayerCards;

        private Card[] bankerCards;

        private CardsType bankerCardsType;
        private CardsType player1CardsType;
        private CardsType player2CardsType;
        private CardsType player3CardsType;

        private Result[] results;

        private BullbullGameResult(Stage stage, Card[] firstPlayerCards, Card[] secondPlayerCards,
                                   Card[] thirdPlayerCards, Card[] bankerCards) {
            this.stage = stage;

            this.firstPlayerCards = firstPlayerCards;
            this.secondPlayerCards = secondPlayerCards;
            this.thirdPlayerCards = thirdPlayerCards;
            this.bankerCards = bankerCards;

            bankerCardsType = calculateCardsType(bankerCards);
            player1CardsType = calculateCardsType(firstPlayerCards);
            player2CardsType = calculateCardsType(secondPlayerCards);
            player3CardsType = calculateCardsType(thirdPlayerCards);

            results = new Result[3];
            results[0] = bankerCardsType.value > player1CardsType.value ? Result.BANKER :
                    (bankerCardsType.value < player1CardsType.value ? Result.PLAYER1 :
                            (compareMaxCard(bankerCards, firstPlayerCards) > 0 ? Result.BANKER : Result.PLAYER1));
            results[1] = bankerCardsType.value > player2CardsType.value ? Result.BANKER :
                    (bankerCardsType.value < player2CardsType.value ? Result.PLAYER2 :
                            (compareMaxCard(bankerCards, secondPlayerCards) > 0 ? Result.BANKER : Result.PLAYER2));
            results[2] = bankerCardsType.value > player3CardsType.value ? Result.BANKER :
                    (bankerCardsType.value < player3CardsType.value ? Result.PLAYER3 :
                            (compareMaxCard(bankerCards, thirdPlayerCards) > 0 ? Result.BANKER : Result.PLAYER3));
        }
    }

    public static int compareMaxCard(Card[] cards1, Card[] cards2) {
        Card cards1MaxCard = cards1[0];
        Card cards2MaxCard = cards2[0];

        for (int i = 1; i < cards1.length; ++i) {
            if (compareCard(cards1MaxCard, cards1[i]) < 0) {
                cards1MaxCard = cards1[i];
            }

            if (compareCard(cards2MaxCard, cards2[i]) < 0) {
                cards2MaxCard = cards2[i];
            }
        }

        return compareCard(cards1MaxCard, cards2MaxCard);
    }

    private static int compareCard(Card card1, Card card2) {
        if (card1.getRank().getValue() != card2.getRank().getValue()) {
            return card1.getRank().getValue() - card2.getRank().getValue();
        }

        if (card1.getSuit().getValue() != card2.getSuit().getValue()) {
            return card1.getSuit().getValue() - card2.getSuit().getValue();
        }

        return 0;
    }
}
