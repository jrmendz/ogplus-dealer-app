package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.config.CardProperty;
import com.og.ogplus.dealerapp.game.model.DealerAI;
import com.og.ogplus.dealerapp.service.ScannerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ShuffleGame extends CardGame {
    private boolean shuffle = false;

    private List<Listener> listenerList;

    private ScannerService.Listener shuffleCardScannedListener = data -> {
        if (StringUtils.isNotBlank(data) && isShuffleCard(data)) {
            enableShuffle();
        }
    };

    public ShuffleGame(Table table) {
        super(table);
    }

    @Override
    public void init() {
        super.init();

        scannerServicesMap.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                .forEach(ScannerService::startScanner);
    }

    @Override
    protected void reset() {
        super.reset();
        this.shuffle = false;
    }


    @Override
    protected void doBeforeRoundStart() throws InterruptedException {
        checkShuffle();
        reset();
        toNextRound();
    }

    @Override
    protected void doAfterRoundStart() {
        super.doAfterRoundStart();
        scannerServicesMap.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                .forEach(scannerService -> scannerService.addListener(shuffleCardScannedListener));
    }

    @Override
    protected void doBeforeRoundEnd() throws InterruptedException {
        super.doBeforeRoundEnd();
        scannerServicesMap.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                .forEach(scannerService -> scannerService.removeListener(shuffleCardScannedListener));
    }

    @Override
    protected void doAfterRoundEnd() throws InterruptedException {
        super.doAfterRoundEnd();
        checkShuffle();
    }

    private void checkShuffle() throws InterruptedException {
        if (shuffle) {
            toNextShoe();
            if (listenerList != null) {
                listenerList.forEach(listener -> getScheduler().execute(listener::onShuffle));
            }
            pause("SHUFFLE (Press ENTER To Continue)", Duration.of(1, ChronoUnit.SECONDS));
            getDealerAI().shuffle();
            shuffle = false;
        }
    }

    @Override
    protected void doCancel() {
        super.doCancel();
        shuffle = true;
    }

    private void toNextShoe() {
        LocalDate today = LocalDate.now();
        if (!today.isEqual(getStage().getDate())) { //change date => reset the shoe
            getStage().setShoe(1);
            getStage().setDate(today);
        } else {
            getStage().setShoe(getStage().getShoe() + 1);
        }
        getStage().setRound(0);
    }

    @Override
    Card getCardFromDealerAI() {
        Card card = null;
        do {
            String cardCode = getDealerAI().drawCard();
            if (DealerAI.SHUFFLE_CARD.equals(cardCode)) {
                enableShuffle();
            } else {
                card = convertToCard(cardCode);
            }
        } while (card == null);

        return card;
    }

    @Override
    protected boolean validateStage(Stage stage) {
        if (stage == null || stage.getShoe() == null) {
            return false;
        }

        Stage currentStage = getStage();
        if (stage.getShoe() < currentStage.getShoe()) {
            return false;
        } else if (stage.getShoe().equals(currentStage.getShoe()) && stage.getRound() <= currentStage.getRound()) {
            return false;
        } else {
            return true;
        }
    }

    public void enableShuffle() {
        shuffle = true;
        listenerList.forEach(gameListener -> getScheduler().execute(gameListener::onShuffleCardScanned));
    }

    private boolean isShuffleCard(String scannedData) {
        String cardCode = getCardProperty().getCardCode(scannedData).orElse(null);

        return CardProperty.SHUFFLE_CODE.equals(cardCode);
    }

    @Lazy
    @Autowired(required = false)
    public void setShuffleListeners(List<Listener> listeners) {
        this.listenerList = listeners;
    }

    public interface Listener extends CardGame.Listener {
        default void onShuffle() {
        }

        default void onShuffleCardScanned() {
        }
    }
}
