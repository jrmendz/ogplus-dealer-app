package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;

public interface Game {
    void initialize();

    default void destroy() {
    }

    void start();

    default void pause() {
    }

    default void resume() {
    }

    default void stop() {
    }

    default boolean cancel() {
        throw new UnsupportedOperationException();
    }

    void setDealer(Dealer dealer);

    Stage getStage();

    void setStage(Stage stage);

    boolean setTempStage(Stage stage);

    void setAutoStart(boolean isEnabled);

    boolean isAutoDeal();

    void setAutoDeal(boolean isEnabled);

    boolean isBiddingMode();

    void extendBettingTime();

    Table getTable();

    GameIdentity getGameIdentity();
}
