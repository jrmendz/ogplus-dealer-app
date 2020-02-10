package com.og.ogplus.dealerapp.controller.api.handler;

import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.ShuffleGame;

public abstract class GameRequestHandler {

    private Game game;

    public GameRequestHandler(Game game) {
        this.game = game;
    }

    public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
        throw new UnsupportedOperationException();
    }

    public boolean handleCancelGameRequest(Stage stage) {
        if (((AbstractGame) game).getStage().equals(stage)) {
            return game.cancel();
        } else {
            return false;
        }
    }

    public boolean handleChangeStageRequest(Stage stage) {
        return game.setTempStage(stage);
    }

    public void handleRefreshStageRequest() {
        if (game instanceof ShuffleGame) {
            ((ShuffleGame)game).enableShuffle();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
