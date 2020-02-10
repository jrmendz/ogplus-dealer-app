package com.og.ogplus.dealerapp.component;

import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.ShuffleGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.game.model.GameResultModel;
import com.og.ogplus.dealerapp.service.GameResultRecordService;
import com.og.ogplus.dealerapp.service.StageRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RecordComponent implements AbstractGame.GameListener, ShuffleGame.Listener {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");

    private AppProperty appProperty;

    private StageRecordService stageRecordService;

    private GameResultRecordService gameResultRecordService;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private GameResultModel gameResultModel;

    private Game game;

    @PostConstruct
    public void init() {
        List<GameResult> gameResults = gameResultRecordService.loadGameResults(appProperty.getGameIdentity());
        gameResultModel.setGameResults(gameResults);
    }

    @Override
    public void onRoundStart(Stage stage) {
        try {
            // To prove when dealer app opened, won't overwrite old game data. => Record stage at game start.
            stageRecordService.saveStage(appProperty.getGameIdentity(), stage);
        } catch (Exception e) {
            log.error("Save stage failed.\n{}", ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void onGameResult(GameResult gameResult) {
        gameResultModel.addGameResult(gameResult);
        List<GameResult> gameResults = new ArrayList<>();
        gameResultModel.iterator().forEachRemaining(gameResults::add);

        try {
            gameResultRecordService.saveGameResults(appProperty.getGameIdentity(), gameResults);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void onShuffle() {
        try {
            stageRecordService.saveStage(appProperty.getGameIdentity(), game.getStage());
        } catch (Exception e) {
            log.error("Save stage failed.\n{}", ExceptionUtils.getStackTrace(e));
        }

        gameResultModel.clear();
        try {
            gameResultRecordService.saveGameResults(appProperty.getGameIdentity(), Collections.emptyList());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    @Autowired
    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    @Autowired
    public void setStageRecordService(StageRecordService stageRecordService) {
        this.stageRecordService = stageRecordService;
    }

    @Autowired
    public void setGameResultModel(GameResultModel gameResultModel) {
        this.gameResultModel = gameResultModel;
    }

    @Autowired
    public void setGameResultRecordService(GameResultRecordService gameResultRecordService) {
        this.gameResultRecordService = gameResultRecordService;
    }

    @Lazy
    @Autowired
    public void setGame(Game game) {
        this.game = game;
    }

}
