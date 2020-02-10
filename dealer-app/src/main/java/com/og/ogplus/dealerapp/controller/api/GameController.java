package com.og.ogplus.dealerapp.controller.api;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.ErrorCode;
import com.og.ogplus.common.model.Response;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.controller.api.handler.GameRequestHandler;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(path = "/dealer-app")
@RestController
public class GameController {
    private final GameCategory gameCategory;

    private final String tableNumber;

    private GameRequestHandler gameRequestHandler;

    public GameController(AppProperty appProperty) {
        this.gameCategory = appProperty.getGameCategory();
        this.tableNumber = appProperty.getTableNumber();
    }

    @PutMapping(path = "/game/result")
    public Response changeGameResult(@RequestBody ChangeGameResultRequest changeResultRequest) {
        if (gameRequestHandler == null) {
            return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
        }

        if (gameCategory != changeResultRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(changeResultRequest.getGameIdentity().getTableNumber())) {
            return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
        }

        try {
            if (gameRequestHandler.handleChangeResultRequest(changeResultRequest.getStage(), changeResultRequest.getResult())) {
                return new Response();
            }else{
                log.error("Change game result failed.");
                return new Response(ErrorCode.INTERNAL_ERROR);
            }
        } catch (FailedChangeGameResultException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return new Response(ErrorCode.FAILED_CHANGE_RESULT, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return new Response(ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return new Response(ErrorCode.INTERNAL_ERROR);
        }
    }

    @PutMapping(path = "/game/stage")
    public Response changeGameStage(@RequestBody ChangeGameStageRequest changeGameStageRequest) {
        if (gameRequestHandler == null) {
            return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
        }

        if (gameCategory != changeGameStageRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(changeGameStageRequest.getGameIdentity().getTableNumber())) {
            return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
        }

        if (gameRequestHandler.handleChangeStageRequest(changeGameStageRequest.getStage())) {
            return new Response();
        }else{
            return new Response(ErrorCode.FAILED_OPERATION);
        }
    }

    @PostMapping(path = "/game/stage")
    public Response shuffle(@RequestBody ShuffleRequest shuffleRequest) {
        if (gameRequestHandler == null) {
            return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
        }

        if (gameCategory != shuffleRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(shuffleRequest.getGameIdentity().getTableNumber())) {
            return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
        }

        try {
            gameRequestHandler.handleRefreshStageRequest();
            return new Response();
        } catch (UnsupportedOperationException e) {
            return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
        }
    }


    @DeleteMapping(path = "/game")
    public Response cancelGame(@RequestBody DeleteGameRequest deleteGameRequest) {
        if (gameRequestHandler == null) {
            return new Response(ErrorCode.INTERNAL_ERROR, "Unsupported Operation");
        }

        if (gameCategory != deleteGameRequest.getGameIdentity().getGameCategory() || !tableNumber.equals(deleteGameRequest.getGameIdentity().getTableNumber())) {
            return new Response(ErrorCode.INVALID_ARGUMENT, "can't find corresponding table");
        }

        if (gameRequestHandler.handleCancelGameRequest(deleteGameRequest.getStage())) {
            return new Response();
        }else{
            return new Response(ErrorCode.FAILED_CANCEL_GAME);
        }
    }

    @Autowired(required = false)
    public void setGameRequestHandler(GameRequestHandler gameRequestHandler) {
        this.gameRequestHandler = gameRequestHandler;
    }
}
