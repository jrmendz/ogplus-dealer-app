package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "FANTAN")
@Component
public class FanTanGame extends AbstractGame {
    private List<Listener> listeners;

    private AtomicReference<FanTanSymbol> currentSymbol = new AtomicReference<>();

    public FanTanGame(Table table) {
        super(table);
    }

    @Override
    protected void init() {
       
    }

    @Override
    protected void reset() {
        currentSymbol.set(null);
    }

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

    @Override
    protected void deal() throws InterruptedException {
        listeners.forEach(listener -> getScheduler().execute(listener::onStart));
        while (currentSymbol.get() == null) {
            Thread.sleep(500);
            if (isAutoDeal()) {
                autoSetResult();
            }
        }

        FanTanSymbol symbol = currentSymbol.get();
        listeners.forEach(listener -> getScheduler().execute(() -> listener.onStop(symbol)));
        Thread.sleep(DATA_PROCESS_PROVE_DELAY);
        updateGameInfo();
        Thread.sleep(2000);
    }

    @Override
    protected GameResult calculateResult() {
        return new FanTanGameResult(getStage().clone(), currentSymbol.get());
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

    public void setCurrentSymbol(FanTanSymbol symbol) {
        this.currentSymbol.set(symbol);
    }

    private void autoSetResult() {
        FanTanSymbol[] symbols = FanTanSymbol.values();
        setCurrentSymbol(symbols[RandomUtils.nextInt(0, symbols.length)]);
    }

    @Lazy
    @Autowired
    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public interface Listener extends GameListener {
		/**
		 * 开始
         * @param
         * @return void
		 */
		default void onStart() {
        }

        /**
         * 停止
         * @param symbol
         * @return void
         */
		default void onStop(FanTanSymbol symbol) {
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FanTanGameResult implements GameResult {
        private static final long serialVersionUID = -4450618620399037188L;

        private Stage stage;

        private FanTanSymbol symbol;

    }
}
