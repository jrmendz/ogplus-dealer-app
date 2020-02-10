package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.dealerapp.game.model.GameResult;

import java.util.List;

public interface GameResultRecordService {
    void saveGameResults(GameIdentity gameIdentity, List<GameResult> gameResults) throws Exception;

    List<GameResult> loadGameResults(GameIdentity gameIdentity);
}
