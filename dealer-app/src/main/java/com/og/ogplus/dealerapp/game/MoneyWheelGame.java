package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.MoneyWheelSymbol;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "MONEY_WHEEL")
@Component
public class MoneyWheelGame extends AbstractGame {

    private List<MoneyWheelSymbol> moneyWheelSymbols = new CopyOnWriteArrayList<>();

    private List<Listener> listeners;

    private AtomicReference<MoneyWheelSymbol> currentSymbol = new AtomicReference<>();

    public MoneyWheelGame(Table table) {
        super(table);
    }

    @Override
    protected void init() {

    }

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

    @Override
    protected void reset() {
        moneyWheelSymbols.clear();
        currentSymbol.set(null);
    }

    @Override
    protected void deal() throws InterruptedException {
        do {
            listeners.forEach(listener -> getScheduler().execute(listener::onSpin));
            while (currentSymbol.get() == null) {
                Thread.sleep(500);
                if (isAutoDeal()) {
                    autoSetResult();
                }
            }

            MoneyWheelSymbol symbol = currentSymbol.get();
            moneyWheelSymbols.add(symbol);
            listeners.forEach(listener -> getScheduler().execute(() -> listener.onWheelStop(symbol)));
            Thread.sleep(500);
            updateGameInfo();
            currentSymbol.set(null);
            Thread.sleep(2000);
            if (symbol != MoneyWheelSymbol.MULTIPLIER_3) {
                break;
            }
        } while (true);
    }

    @Override
    protected GameResult calculateResult() {
        return new MoneyWheelGameResult(getStage().clone(), moneyWheelSymbols.toArray(new MoneyWheelSymbol[0]));
    }

    @Override
    protected boolean isCheckResultEnabled() {
        return false;
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

    public void setCurrentSymbol(MoneyWheelSymbol symbol) {
        this.currentSymbol.set(symbol);
    }

    public List<MoneyWheelSymbol> getSymbols() {
        return new ArrayList<>(moneyWheelSymbols);
    }

    private void autoSetResult() {
        MoneyWheelSymbol[] symbols = MoneyWheelSymbol.values();
        setCurrentSymbol(symbols[RandomUtils.nextInt(0, symbols.length)]);
    }


    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }


    public interface Listener extends AbstractGame.GameListener {
        default void onSpin() {
        }

        default void onWheelStop(MoneyWheelSymbol symbol) {
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoneyWheelGameResult implements GameResult{
        private static final long serialVersionUID = -4450618620399037184L;

        private Stage stage;

        private MoneyWheelSymbol[] symbols;

    }

}
