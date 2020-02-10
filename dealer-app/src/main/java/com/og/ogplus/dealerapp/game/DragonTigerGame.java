package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.common.enums.DragonTigerType;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.*;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "DRAGON_TIGER")
@Component
public class DragonTigerGame extends ShuffleGame {

    private List<Listener> listeners;

    private Card dragonCard;

    private Card tigerCard;

    private boolean singleScanner;

    @Getter
    @Value("${dragon-tiger.type}")
    private DragonTigerType type = DragonTigerType.NEW;

    public DragonTigerGame(Table table) {
        super(table);
    }

    private static Result calculateGameResult(Card dragonCard, Card tigerCard) throws UnsatisfiedDragonTigerRuleException {
        if (dragonCard == null || tigerCard == null) {
            throw new UnsatisfiedDragonTigerRuleException();
        }
        return dragonCard.getValue() > tigerCard.getValue() ? Result.DRAGON :
                dragonCard.getValue() < tigerCard.getValue() ? Result.TIGER : Result.TIE;
    }

    @Override
    protected boolean validateScannerSetting() {
        int scannerCount = getScannerCount();
        switch (scannerCount) {
            case 1:
                singleScanner = true;
                return true;
            case 2:
                singleScanner = false;
                return true;
            default:
                log.warn("Dragon&Tiger only support single or 2 scanner devices.");
                showErrorMessage("Dragon&Tiger only support single or 2 scanner devices.");
                return false;
        }
    }

    @Override
    protected void reset() {
        super.reset();
        dragonCard = tigerCard = null;
    }

    @Override
    protected void deal() throws InterruptedException {
        if (singleScanner) {
            dragonCard = dealCard(DragonTigerPosition.DRAGON);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();

            tigerCard = dealCard(DragonTigerPosition.TIGER);
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();
        } else {
            Card[] cards = dealCard(DragonTigerPosition.DRAGON, DragonTigerPosition.TIGER);
            dragonCard = cards[0];
            tigerCard = cards[1];
            Thread.sleep(DATA_PROCESS_PROVE_DELAY);
            updateGameInfo();
        }

        squeeze(getSqueezeTime1(), DragonTigerPosition.DRAGON, DragonTigerPosition.TIGER);

    }

    @Override
    protected void doBeforeRoundEnd() throws InterruptedException {
        showTempResult();

        super.doBeforeRoundEnd();
    }

    @Override
    protected GameResult calculateResult() {
        return new DragonTigerGameResult(getStage().clone(), dragonCard, tigerCard);
    }

    private void showTempResult() {
        try {
            Result results = calculateGameResult(dragonCard, tigerCard);
            listeners.forEach(listener -> getScheduler().execute(() -> listener.onTempGameResult(results)));
        } catch (UnsatisfiedDragonTigerRuleException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            alert(e.getMessage());
        }
    }

    @Override
    protected List<Position> getPositions() {
        return Arrays.asList(DragonTigerPosition.values());
    }

    @Override
    protected Card generateCard(Suit suit, Rank rank) {
        if (type == DragonTigerType.CLASSIC) {
            return new ClassicDTCard(suit, rank);
        } else {  // Type.NEW
            return new Card(suit, rank);
        }
    }

    public void setCard(Stage stage, DragonTigerPosition position, Card card) throws FailedChangeGameResultException {
        if (isAllowChangeGameResult()) {
            getChangeLock().lock();
            try {
                if (isAllowChangeGameResult() && getStage().equals(stage)) {
                    switch (position) {
                        case DRAGON:
                            dragonCard = card;
                            break;
                        case TIGER:
                            tigerCard = card;
                            break;
                        default:
                    }

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
        if (position instanceof DragonTigerPosition) {
            switch ((DragonTigerPosition)position) {
                case DRAGON:
                    return dragonCard;
                case TIGER:
                    return tigerCard;
            }
        }
        return null;
    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public enum Result {
        DRAGON, TIGER, TIE,
        ;
    }

    public interface Listener extends ShuffleGame.Listener {
        default void onTempGameResult(Result results) {
        }
    }

    @Getter
    public static class DragonTigerGameResult implements GameResult {
        private static final long serialVersionUID = 4503111233922622860L;

        private Stage stage;

        private Card dragonCard;
        private Card tigerCard;

        private List<Result> result;

        private DragonTigerGameResult(Stage stage, Card dragonCard, Card tigerCard) {
            this.stage = stage;

            this.dragonCard = dragonCard;
            this.tigerCard = tigerCard;

            this.result = new ArrayList<>();
            try {
                result.add(calculateGameResult(dragonCard, tigerCard));
            } catch (UnsatisfiedDragonTigerRuleException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private static class UnsatisfiedDragonTigerRuleException extends Exception {
        private UnsatisfiedDragonTigerRuleException() {
        }

        private UnsatisfiedDragonTigerRuleException(String message) {
            super(message);
        }
    }

}
