package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.game.model.RoulettePacket;
import com.og.ogplus.dealerapp.game.model.RoulettePacketParser;
import com.og.ogplus.dealerapp.service.ScannerService;
import com.og.ogplus.dealerapp.service.ScannerServiceManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "ROULETTE")
@Component
public class RouletteGame extends AbstractGame {
    @Value("${roulette.enable-00:false}")
    private boolean pocket00Enabled;

    private List<Listener> listeners;

    private AtomicReference<RouletteSlot> rouletteSlot = new AtomicReference<>();

    private ScannerServiceManager scannerServiceManager;

    private ScannerService scannerService;

    private RotorListener rotorListener;

    public RouletteGame(Table table) {
        super(table);
    }

    @Override
    protected void init() {
        autoConfigScanner();

        if (scannerService != null) {
            scannerService.startScanner();
            scannerService.writeString("*m2 2 2\r");  //turn on Extended Protocol
            scannerService.writeString("*R\r");  //reset the current game back to game start status, 1.
            scannerService.addListener(data -> {
                RoulettePacket packet;
                try {
                    packet = RoulettePacketParser.parse(data);
                    if (packet.getWarningFlag() != 0 && packet.getWarningFlag() != 1) {//ignore speed error
                        alert(packet.getWarningMessage());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Parse {} failed. ({})", data, e.getMessage());
                }
            });
            scannerService.addListener(new RotorHealthyListener());
            rotorListener = new RotorListener();
        } else {
            showWarmMessage("Can't find roulette device.");
        }
    }

    private void autoConfigScanner() {
        Map<ScannerService, ScannerService.Listener> listeners = new HashMap<>();
        listeners.putAll(scannerServiceManager.getAllScannerServices().stream()
                .collect(Collectors.toMap(s -> s, s -> (ScannerService.Listener) data -> {
                    try {
                        RoulettePacket packet = RoulettePacketParser.parse(data);
                        if (packet != null) {
                            scannerService = s;
                            listeners.forEach(ScannerService::removeListener);
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn(ExceptionUtils.getStackTrace(e));
                    }
                })));
        listeners.keySet().forEach(scannerService -> scannerService.startScanner(0));
        listeners.forEach(ScannerService::addListener);

        LocalDateTime startConfigDate = LocalDateTime.now();
        while (scannerService == null && startConfigDate.plusSeconds(5).isAfter(LocalDateTime.now())) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        listeners.forEach(ScannerService::removeListener);
        listeners.keySet().stream().filter(s -> !s.equals(scannerService)).forEach(ScannerService::stopScanner);
    }

    @Override
    protected void reset() {
        rouletteSlot.set(null);
    }

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

    @Override
    protected void deal() throws InterruptedException {
        listeners.forEach(listener -> getScheduler().execute(listener::onSpin));

        if (scannerService != null) {
            rotorListener.reset();
            scannerService.addListener(rotorListener);
        }

        do {
            Thread.sleep(500);
            if (isAutoDeal()) {
                autoSetResult();
            }
        } while (rouletteSlot.get() == null);

        if (scannerService != null) {
            scannerService.removeListener(rotorListener);
        }

        listeners.forEach(listener -> getScheduler().execute(() -> listener.onWheelStop(rouletteSlot.get())));

    }

    @Override
    protected void doAfterRoundStart() {
        super.doAfterRoundStart();
    }

    @Override
    protected GameResult calculateResult() {
        return new RouletteGameResult(getStage().clone(), rouletteSlot.get());
    }

    @Override
    protected boolean isCheckResultEnabled() {
        return false;
    }

    @Override
    protected boolean validateStage(Stage stage) {
        if (stage == null || stage.getShoe() != null) {
            return false;
        }

        return stage.getRound() > getStage().getRound();
    }

    public void setSlot(RouletteSlot slot) {
        this.rouletteSlot.set(slot);
    }

    private void autoSetResult() {
        RouletteSlot[] slots = RouletteSlot.values();
        RouletteSlot result;
        if (pocket00Enabled) {
            result = slots[RandomUtils.nextInt(0, slots.length)];
        } else {
            do {
                result = slots[RandomUtils.nextInt(0, slots.length)];
            } while (result == RouletteSlot.SLOT_00);
        }

        setSlot(result);
    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    @Autowired
    public void setScannerServiceManager(ScannerServiceManager scannerServiceManager) {
        this.scannerServiceManager = scannerServiceManager;
    }

    public interface Listener extends GameListener {
        default void onSpin() {
        }

        default void onWheelStop(RouletteSlot slot) {
        }

        default void onRotorStateChange(RoulettePacket.GameState gameState) {
        }

        default void onRotorDisconnect() {
        }

        default void onRotorConnect() {
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouletteGameResult implements GameResult {
        private static final long serialVersionUID = 1167212512037249711L;

        private Stage stage;

        private RouletteSlot slot;
    }

    private class RotorListener implements ScannerService.Listener {
        private RoulettePacket.GameState currentRotorState;

        private int ballRevolutionNum;

        private void reset() {
            currentRotorState = null;
            ballRevolutionNum = 0;
        }

        @Override
        public synchronized void onReceiveData(String data) {
            RoulettePacket packet = null;
            try {
                packet = RoulettePacketParser.parse(data);
            } catch (IllegalArgumentException e) {
                log.warn("Parse ({}) failed. {}", data, e.getMessage());
            }

            if (packet != null) {
                if (currentRotorState != packet.getGameState()) {
                    currentRotorState = packet.getGameState();
                    listeners.forEach(listener -> getScheduler().execute(() -> listener.onRotorStateChange(currentRotorState)));
                }

                switch (packet.getGameState()) {
                    case START_GAME:
                        ballRevolutionNum = 0;
                        break;
                    case BALL_IN_RIM:
                        //the error message will be sent from rotor but to slow
                        if (packet.getBallDirection() != null && packet.getBallDirection() == packet.getWheelDirection()) {
                            alert("Ball Launch in same direction as Wheel");
                        }
                        break;
                    case NO_MORE_BETS:
                        ballRevolutionNum = Math.max(ballRevolutionNum, packet.getRevolutionNum());
                        break;
                    case WINNING_NUMBER:
                        if (ballRevolutionNum < 3) {
                            alert("Failed to Spin. Revolutions of Ball less than 3");
                        } else {
                            setSlot(RouletteSlot.parse(String.valueOf(packet.getLastResult())));
                            scannerService.removeListener(this);
                        }
                        break;
                    case TABLE_CLOSED:
                    case DEALER_LOCK:
                        alert("Unexpected State");
                        break;
                    default:
                }
            }
        }
    }

    private class RotorHealthyListener implements ScannerService.Listener {
        private LocalDateTime lastReceivePacketTime;

        private boolean connecting = true;

        private RotorHealthyListener() {
            lastReceivePacketTime = LocalDateTime.now();

            getScheduler().scheduleWithFixedDelay(() -> {
                if (LocalDateTime.now().isAfter(lastReceivePacketTime.plusSeconds(5))) {
                    log.warn("Can't receive packet from roulette device.");
                    connecting = false;
                    listeners.forEach(listener -> getScheduler().execute(listener::onRotorDisconnect));
                    scannerService.stopScanner();
                    scannerService.startScanner();
                }
            }, 1000);
        }

        @Override
        public void onReceiveData(String data) {
            lastReceivePacketTime = LocalDateTime.now();

            if (!connecting) {
                log.warn("Re-receive date from roulette device.");
                connecting = true;
                listeners.forEach(listener -> getScheduler().execute(listener::onRotorConnect));
            }
        }
    }


}
