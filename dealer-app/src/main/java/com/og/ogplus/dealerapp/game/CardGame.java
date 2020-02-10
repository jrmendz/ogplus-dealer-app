package com.og.ogplus.dealerapp.game;


import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Suit;
import com.og.ogplus.dealerapp.component.ScannerSettingHelper;
import com.og.ogplus.dealerapp.config.CardProperty;
import com.og.ogplus.dealerapp.exception.UnexpectedScannerSettingException;
import com.og.ogplus.dealerapp.game.model.DealerAI;
import com.og.ogplus.dealerapp.service.CardValidationService;
import com.og.ogplus.dealerapp.service.ScannerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public abstract class CardGame extends AbstractGame {
    protected Map<Position, Set<ScannerService>> scannerServicesMap = new HashMap<>();
    @Getter(AccessLevel.PROTECTED)
    private CardProperty cardProperty;
    private List<Listener> listenerList;
    @Getter(AccessLevel.PROTECTED)
    private ScannerSettingHelper scannerSettingHelper;
    @Getter(AccessLevel.PROTECTED)
    private DealerAI dealerAI;

    private CardValidationService cardValidationService;

    public CardGame(Table table) {
        super(table);
    }

    @Override
    public void init() {
        if (scannerSettingHelper.getScannerCount() == 0) {
            log.warn("No scanner available");
            setAutoDeal(true);
            setAutoDealChangeable(false);
        } else {
            do {
                configureScanner();
            } while (!validateScannerSetting());
        }
    }

    protected void configureScanner() {
        final Map<Position, ScannerService> scannerServiceMap;
        try {
            //TODO Only support one-to-one(position-scanner), have to support one-to-many(position-scanner)
            scannerServiceMap = scannerSettingHelper.getScannerServices(getPositions());
            scannerServicesMap.putAll(scannerServiceMap.keySet().stream()
                    .collect(Collectors.toMap(position -> position, position -> {
                        Set<ScannerService> set = new HashSet<>();
                        set.add(scannerServiceMap.get(position));
                        return set;
                    })));
        } catch (UnexpectedScannerSettingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    protected abstract boolean validateScannerSetting();

    public int getScannerCount() {
        if (scannerServicesMap == null) {
            return 0;
        } else {
            return scannerServicesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()).size();
        }
    }

    public boolean isSqueezeMode() {
        return getAppProperty().isSqueezeMode();
    }

    public Duration getSqueezeTime1() {
        if (getTable().getMeta().getSqueezeTime1() == null) {
            return getAppProperty().getSqueezeTime1();
        } else {
            return Duration.ofSeconds(getTable().getMeta().getSqueezeTime1());
        }
    }

    public Duration getSqueezeTime2() {
        if (getTable().getMeta().getSqueezeTime2() == null) {
            return getAppProperty().getSqueezeTime2();
        } else {
            return Duration.ofSeconds(getTable().getMeta().getSqueezeTime1());
        }
    }

    protected void squeeze(Duration squeezeTime, Position... positions) throws InterruptedException {
        if (isSqueezeMode()) {
            waitSqueeze(squeezeTime, positions);
            doShowCards(positions);
        }
    }

    protected void waitSqueeze(Duration squeezeDuration, Position... positions) throws InterruptedException {
        listenerList.forEach(gameListener -> getScheduler().execute(() -> gameListener.onStartSqueeze(LocalDateTime.now().plus(squeezeDuration), positions)));
        Thread.sleep(squeezeDuration.toMillis());
        listenerList.forEach(gameListener -> getScheduler().execute(() -> gameListener.onEndSqueeze(positions)));
        Thread.sleep(1000);
    }

    protected Card dealCard(Position position) throws InterruptedException {
        return dealCard(new Position[]{position})[0];
    }

//    protected Card dealCard(boolean isCardShown, Position position) {
//        return dealCard(isCardShown, new Position[]{position})[0];
//    }

    protected Card[] dealCard(Position... positions) throws InterruptedException {
        return dealCard(!isSqueezeMode(), positions);
    }

    protected Card[] dealCard(boolean isCardShown, Position... positions) throws InterruptedException {
        List<Future<Card>> getCardFutures = new ArrayList<>();

        for (Position position : positions) {
            getCardFutures.add(getScheduler().submit(() -> {
                onStartScanning(position);
                Card card;
                do {
                    card = fetchCard(position);
                } while (cardValidationService != null && !cardValidationService.isValid(card));
                onCardScanned(position, card, isCardShown);
                return card;
            }));
        }

        Card[] cards = new Card[positions.length];
        try {
            for (int i = 0; i < getCardFutures.size(); i++) {
                cards[i] = getCardFutures.get(i).get();
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage());
        } finally {
            getCardFutures.forEach(f -> f.cancel(true));
        }

        return cards;
    }

    private Card fetchCard(Position position) throws ExecutionException, InterruptedException {
        Collection<Future<Card>> fetchCardTask = new ArrayList<>();
        getScannerService(position).ifPresent(scannerServices ->
                fetchCardTask.addAll(scannerServices.stream()
                        .map(scannerService -> getScheduler().submit(new FetchCardFromScannerTask(scannerService)))
                        .collect(Collectors.toList())));

        fetchCardTask.add(getScheduler().submit(new FetchCardFromAITask()));
        fetchCardTask.add(getScheduler().submit(new FetchCardFromInMemoryTask(position)));

        try {
            while (true) {
                for (Future<Card> f : fetchCardTask) {
                    if (f.isDone()) {
                        return f.get();
                    }
                }
                Thread.sleep(100);
            }
        } finally {
            fetchCardTask.forEach(task -> task.cancel(true));
        }
    }

    Card getCardFromDealerAI() {
        Card card;
        do {
            card = convertToCard(dealerAI.drawCard());
        } while (card == null);

        return card;
    }

    public abstract Card getCard(Position position);

    Card convertToCard(String cardCode) {
        if (StringUtils.isNoneBlank(cardCode)) {
            try {
                Suit suit = Suit.of(cardCode.substring(cardCode.length() - 1));
                Rank rank = Rank.of(cardCode.substring(0, cardCode.length() - 1));
                return generateCard(suit, rank);
            } catch (Exception e) {
                log.warn("Failed to parse ({}) to card: {}", cardCode, e.getMessage());
            }
        }

        return null;
    }

    protected Card generateCard(Suit suit, Rank rank) {
        return new Card(suit, rank);
    }

    private boolean isShuffleCard(String scannedData) {
        String cardCode = cardProperty.getCardCode(scannedData).orElse(null);

        return CardProperty.SHUFFLE_CODE.equals(cardCode);
    }


    private void onStartScanning(Position position) {
        listenerList.forEach(listener -> getScheduler().execute(() -> listener.onStartScanning(position)));
    }

    protected void onCardScanned(Position baccaratPosition, Card card, boolean isCardShown) {
        listenerList.forEach(listener -> getScheduler().execute(() -> listener.onCardScanned(baccaratPosition, card, isCardShown)));
    }

    protected void doShowCards(Position... positions) {
        listenerList.forEach(listener -> getScheduler().execute(() -> listener.showCards(positions)));
    }

    protected Optional<Set<ScannerService>> getScannerService(Position position) {
        return Optional.ofNullable(scannerServicesMap.get(position));
    }

    protected abstract List<Position> getPositions();

    @Autowired
    public void setCardProperty(CardProperty cardProperty) {
        this.cardProperty = cardProperty;
    }

    @Lazy
    @Autowired(required = false)
    public void setListenerList(List<Listener> listenerList) {
        this.listenerList = listenerList;
    }

    @Autowired
    public void setScannerSettingHelper(ScannerSettingHelper scannerSettingHelper) {
        this.scannerSettingHelper = scannerSettingHelper;
    }

    @Autowired
    public void setDealerAI(DealerAI dealerAI) {
        this.dealerAI = dealerAI;
    }

    @Autowired(required = false)
    public void setCardValidationService(CardValidationService cardValidationService) {
        this.cardValidationService = cardValidationService;
    }

    public interface Listener extends AbstractGame.GameListener {
        default void onStartScanning(Position position) {
        }

        default void onCardScanned(Position position, Card card, boolean isCardShown) {
        }

        default void showCards(Position... positions) {
        }

        default void onStartSqueeze(LocalDateTime squeezeEndTime, Position... positions) {
        }

        default void onEndSqueeze(Position... positions) {
        }
    }


    private class FetchCardFromScannerTask implements Callable<Card> {
        private final ScannerService scannerService;

        public FetchCardFromScannerTask(ScannerService scannerService) {
            this.scannerService = scannerService;
        }

        @Override
        public Card call() throws Exception {
            AtomicReference<String> cardCode = new AtomicReference<>(null);
            ScannerService.Listener listener = null;
            try {
                scannerService.addListener(listener = data -> {
                    if (StringUtils.isNotBlank(data) && !isShuffleCard(data)) {
                        cardProperty.getCardCode(data).ifPresent(cardCode::set);
                    }
                });

                String s;
                do {
                    if ((s = cardCode.get()) != null) {
                        return convertToCard(s);
                    }
                    Thread.sleep(100);
                } while (!Thread.currentThread().isInterrupted());
                throw new InterruptedException();
            } finally {
                scannerService.removeListener(listener);
            }
        }
    }

    private class FetchCardFromAITask implements Callable<Card> {
        @Override
        public Card call() throws Exception {
            while (!Thread.currentThread().isInterrupted()) {
                if (isAutoDeal()) {
                    return getCardFromDealerAI();
                }
                Thread.sleep(100);
            }
            throw new InterruptedException();
        }
    }

    private class FetchCardFromInMemoryTask implements Callable<Card> {
        private final Position position;

        public FetchCardFromInMemoryTask(Position position) {
            this.position = position;
        }

        @Override
        public Card call() throws Exception {
            Card card;
            while (!Thread.currentThread().isInterrupted()) {
                if ((card = getCard(position)) != null) {
                    return card;
                }
                Thread.sleep(100);
            }
            throw new InterruptedException();
        }
    }
}
