package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.service.ClientInteractService;
import com.og.ogplus.dealerapp.service.StageRecordService;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.LoginDialog;
import com.og.ogplus.dealerapp.view.StageSettingDialog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class GUIInteractService implements ClientInteractService, AbstractGame.GameListener {

    private JFrame mainFrame;

    private LoginDialog loginDialog;

    private StageSettingDialog stageSettingDialog;

    private StageRecordService stageRecordService;

    private AppProperty appProperty;

    @PostConstruct
    public void init() {
//        stageSettingDialog.getLoadBtn().addActionListener(event -> {
//            try {
//                Stage stage = stageRecordService.loadStage(appProperty.getGameIdentity());
//                stageSettingDialog.getShoeTextField().setText(stage.getShoe() == null ? "" : String.valueOf(stage.getShoe()));
//                stageSettingDialog.getRoundTextField().setText(String.valueOf(stage.getRound() + 1)); //Record last game round but should display next round number
//            } catch (Exception e) {
//                showErrorMessage(stageSettingDialog, "Load stage failed.");
//            }
//        });
    }

    @Override
    public String getDealerCode() throws ClientInteractInterruptedException {
        return getDealerCode(true);
    }

    @Override
    public String getDealerCode(boolean allowSkip) throws ClientInteractInterruptedException {
        AtomicReference<String> data = new AtomicReference<>();

        try {
            SwingUtilities.invokeAndWait(() -> data.set(loginDialog.showDialog(allowSkip)));
            if (CANCEL.equals(data.get())) {
                throw new ClientInteractInterruptedException();
            }
        } catch (InterruptedException | InvocationTargetException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new ClientInteractInterruptedException();
        }

        return data.get();
    }

    @Override
    public String getPitBossCode() throws ClientInteractInterruptedException {
        AtomicReference<String> data = new AtomicReference<>();

        try {
            SwingUtilities.invokeAndWait(() -> data.set(loginDialog.showDialog("Pit Boss Authentication")));
            if (CANCEL.equals(data.get())) {
                throw new ClientInteractInterruptedException();
            }
        } catch (InterruptedException | InvocationTargetException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new ClientInteractInterruptedException();
        }

        return data.get();

    }

    @Override
    public Stage showStage(GameCategory gameCategory) {
        if (gameCategory == GameCategory.ROULETTE || gameCategory == GameCategory.SIC_BO || gameCategory == GameCategory.FANTAN || gameCategory == GameCategory.NIUNIU) {
            stageSettingDialog.getShoeTextField().setText("");
            stageSettingDialog.getShoeTextField().setEnabled(false);
        } else {
            stageSettingDialog.getShoeTextField().setText("1");
            stageSettingDialog.getShoeTextField().setEnabled(true);
        }

        stageSettingDialog.getRoundTextField().setText("1");
        return stageSettingDialog.getDialog();
    }

    @Override
    public Stage getStage(GameCategory gameCategory) {
        if (gameCategory == GameCategory.ROULETTE || gameCategory == GameCategory.SIC_BO || gameCategory == GameCategory.FANTAN || gameCategory == GameCategory.NIUNIU) {
            stageSettingDialog.getShoeTextField().setText("");
            stageSettingDialog.getShoeTextField().setEnabled(false);
        } else {
            stageSettingDialog.getShoeTextField().setText("1");
            stageSettingDialog.getShoeTextField().setEnabled(true);
        }

        stageSettingDialog.getRoundTextField().setText("1");
        stageSettingDialog.showDialog();

        String shoe = stageSettingDialog.getShoeTextField().getText().trim();
        String round = stageSettingDialog.getRoundTextField().getText().trim();

        if (StringUtils.isBlank(round)) {
            throw new RuntimeException("Invalid stage");
        }

        return Stage.builder().shoe(StringUtils.isBlank(shoe) ? null : Integer.parseInt(shoe)).round(Integer.parseInt(round)).build();
    }

    private void showErrorMessage(java.awt.Component parentComponent, String message) {
        try {
            SwingUtilities.invokeAndWait(() -> JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE));
        } catch (InterruptedException | InvocationTargetException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    @Override
    public void showWarningMessage(String message) {
        try {
            SwingUtilities.invokeAndWait(() -> JOptionPane.showMessageDialog(mainFrame, message, "Warning", JOptionPane.WARNING_MESSAGE));
        } catch (InterruptedException | InvocationTargetException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void showErrorMessage(String message) {
        showErrorMessage(mainFrame, message);
    }

    @Autowired
    public void setMainFrame(DealerAppWindowsLayout mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Autowired
    public void setLoginDialog(LoginDialog loginDialog) {
        this.loginDialog = loginDialog;
    }

    @Autowired
    public void setStageSettingDialog(StageSettingDialog stageSettingDialog) {
        this.stageSettingDialog = stageSettingDialog;
    }

    @Autowired
    public void setStageRecordService(StageRecordService stageRecordService) {
        this.stageRecordService = stageRecordService;
    }

    @Autowired
    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }
}
