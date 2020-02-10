package com.og.ogplus.dealerapp;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.AuthenticateException;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.service.ClientInteractService;
import com.og.ogplus.dealerapp.service.SecurityService;
import com.og.ogplus.dealerapp.service.StageRecordService;
import com.og.ogplus.dealerapp.view.DealerAppSettingDialog;
import com.og.ogplus.dealerapp.view.ErrorDetailsDialog;
import com.og.ogplus.dealerapp.view.LoadingDialog;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

@EnableSwagger2
@Slf4j
@EnableJpaRepositories("com.og.ogplus.common.db.dao")
@EntityScan("com.og.ogplus.common.db.entity")
@SpringBootApplication
public class DealerApp implements CommandLineRunner {
    private static LoadingDialog loadingDialog;
    private static boolean gui = true;

    static {
        String guiStr = System.getProperty("gui");
        if (!StringUtils.isEmpty(guiStr)) {
            gui = Boolean.parseBoolean(guiStr);
        }
        System.setProperty("app.gui", String.valueOf(gui));
        System.setProperty("java.awt.headless", "false");
    }

    private StageRecordService stageRecordService;

    private ClientInteractService clientInteractService;

    private SecurityService securityService;

    private Game game;


    public static void main(String[] args) {
        GameIdentity gameIdentity = getGameIdentity();

        System.setProperty("spring.application.name", String.format("dealer-app-%s-%s", gameIdentity.getGameCategory(), gameIdentity.getTableNumber()));
        System.setProperty("app.game-category", gameIdentity.getGameCategory().name());
        System.setProperty("app.table-number", gameIdentity.getTableNumber());

        setLoadingDialogVisible(true);

        try {
            SpringApplication.run(DealerApp.class, args);
        } catch (Exception e) {
            setLoadingDialogVisible(false);
            try {
                if (gui) {
                    SwingUtilities.invokeAndWait(() -> new ErrorDetailsDialog().show("Something Error ...", ExceptionUtils.getStackTrace(e)));
                }
            } catch (InterruptedException | InvocationTargetException ex) {
                log.error(ExceptionUtils.getStackTrace(ex));
            } finally {
                System.exit(1);
            }
        }
    }

    private static GameIdentity getGameIdentity() {
        GameIdentity gameIdentity = DealerAppSettingDialog.loadSettings();
        if (gameIdentity == null) {
            if (gui) {
                gameIdentity = DealerAppSettingDialog.showDialog();
            } else {
                String tableNumber = System.getProperty("tableNumber");
                String gameCategory = System.getProperty("gameCategory");
                gameIdentity = new GameIdentity(GameCategory.valueOf(gameCategory), tableNumber);
            }
        }

        if (gameIdentity.getGameCategory() == null || StringUtils.isBlank(gameIdentity.getTableNumber())) {
            log.error("gameCategory and tableNumber must not be empty");
            System.exit(1);
        }

        return gameIdentity;
    }

    private static void setLoadingDialogVisible(boolean isVisible) {
        if (!gui) {
            return;
        }

        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog();
        }

        if (EventQueue.isDispatchThread()) {
            loadingDialog.setVisible(isVisible);
        } else {
            EventQueue.invokeLater(() -> loadingDialog.setVisible(isVisible));
        }
    }

    @Override
    public void run(String... args) throws Exception {

        game.setStage(getStage());
        game.initialize();
        setLoadingDialogVisible(false);
        game.setDealer(getDealer());    //login dialog should pop after dealer app opened
        game.start();
    }

    private Stage getStage() {
        Stage stage = null;
        try {
            stage = stageRecordService.loadStage(game.getGameIdentity());   //try to load last finished stage
        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
        }

        if (stage != null) {
            LocalDate today = LocalDate.now();
            if (stage.getDate().equals(today)) {
                // if today is same date with history
                return stage;
            } else if (stage.getDate().equals(today.minusDays(1)) && stage.getShoe() != null) {
                // if history is yesterday and stage have shoe
                return stage;
            }
        }
        return null;
    }

    private Dealer getDealer() {
        Dealer dealer = null;
        do {
            try {
                String dealerCode = clientInteractService.getDealerCode(false);
                dealer = securityService.authDealer(dealerCode);
            } catch (AuthenticateException e) {
                clientInteractService.showErrorMessage(e.getMessage());
            } catch (ClientInteractInterruptedException e) {
                System.exit(1);
            }
        } while (dealer == null);

        return dealer;
    }

    @Autowired
    public void setGame(Game game) {
        this.game = game;
    }

    @Autowired
    public void setStageRecordService(StageRecordService stageRecordService) {
        this.stageRecordService = stageRecordService;
    }

    @Autowired
    public void setClientInteractService(ClientInteractService clientInteractService) {
        this.clientInteractService = clientInteractService;
    }

    @Autowired
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
