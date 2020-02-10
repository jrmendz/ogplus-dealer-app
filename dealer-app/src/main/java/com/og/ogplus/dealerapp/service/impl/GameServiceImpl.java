package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.db.dao.GameCodeDao;
import com.og.ogplus.common.db.dao.TableDao;
import com.og.ogplus.common.db.entity.GameCode;
import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.service.ClientInteractService;
import com.og.ogplus.dealerapp.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    private TableDao tableDao;

    private GameCodeDao gameCodeDao;

    private ClientInteractService clientInteractService;

    @Override
    public Optional<Table> getTable(GameCategory gameCategory, String tableNumber) {
        Optional<GameCode> gameCodeOptional = gameCodeDao.findByCode(gameCategory);
        if (gameCodeOptional.isPresent()) {
            return tableDao.findByGameCodeAndNumber(gameCodeOptional.get(), tableNumber);
        } else {
            log.warn("Can't find corresponding table: ({}, {}).", gameCategory.name(), tableNumber);
            return Optional.empty();
        }
    }

    @Override
    public Stage getStage(GameCategory gameCategory) {
        Stage stage = null;
        do {
            try {
                stage = clientInteractService.getStage(gameCategory);
                if (!validate(gameCategory, stage)) {
                    clientInteractService.showErrorMessage("Invalid stage.");
                    stage = null;
                }
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
                clientInteractService.showErrorMessage("Invalid stage.");
            }
        } while (stage == null);
        stage.setRound(stage.getRound() - 1);
        return stage;
    }

    private boolean validate(GameCategory gameCategory, Stage stage) {
        switch (gameCategory) {
            case BACCARAT:
            case DRAGON_TIGER:
            case MONEY_WHEEL:
                if (stage.getShoe() == null || stage.getShoe() < 1 || stage.getRound() < 1) {
                    return false;
                }
                break;
            case ROULETTE:
            case SIC_BO:
            case FANTAN:
                if (stage.getShoe() != null || stage.getRound() < 1) {
                    return false;
                }
                break;
            default:
                return true;
        }

        return true;
    }

    @Autowired
    public void setTableDao(TableDao tableDao) {
        this.tableDao = tableDao;
    }

    @Autowired
    public void setGameCodeDao(GameCodeDao gameCodeDao) {
        this.gameCodeDao = gameCodeDao;
    }

    @Autowired
    public void setClientInteractService(ClientInteractService clientInteractService) {
        this.clientInteractService = clientInteractService;
    }
}
