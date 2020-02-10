package com.og.ogplus.dealerapp.controller;

import com.og.ogplus.common.message.OperationMessage;
import com.og.ogplus.common.message.UpdateStatusMessage;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.AuthenticateException;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.game.ShuffleGame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public abstract class ShuffleGameController extends CardGameController implements ShuffleGame.Listener {
    @Override
    public void init() {
        if(checkGui()) {
        super.init();
        getLayout().addShuffleButtonAction(e ->
                getTaskScheduler().execute(() -> {
                    try {
                        String pitBossCode = getClientInteractService().getPitBossCode();
                        getSecurityService().authPitBoss(pitBossCode);
                        ((ShuffleGame) getGame()).enableShuffle();
                    } catch (ClientInteractInterruptedException ex) {
                        log.warn("Client Interact Interrupted");
                    } catch (AuthenticateException ex) {
                        getClientInteractService().showErrorMessage(ex.getMessage());
                    }
                }));
        }
    }

    @Override
    public void onShuffle() {
        if(checkGui()) {
        SwingUtilities.invokeLater(() -> {
            getLayout().reset();
            getLayout().getAutoStartButton().setSelected(false);
        });
        }
        sendGameMessageOut(new UpdateStatusMessage("SHUFFLE"));
    }

    @Override
    public void onGameCancel(Stage stage) {
        super.onGameCancel(stage);
        getLayout().clearRoadMap();
        getLayout().setStage("N/A");
    }

    @Override
    public void onShuffleCardScanned() {
        if(checkGui()) {
            SwingUtilities.invokeLater(() -> getLayout().getAutoStartButton().setSelected(false));  
        }
        addShuffleDecorate();
        getLayout().showAlert("Shuffle Next Round", Color.ORANGE, Color.BLACK);
        sendGameMessageOut(new OperationMessage("SHUFFLE"));
    }


    private void addShuffleDecorate() {
        String originStage = getLayout().getStage();

        if (!originStage.contains("shuffle")) {
            getLayout().setStage(String.format("<html>%s&nbsp;<font color='yellow'>(shuffle)</font></html>", originStage));
        }
    }
    
  
    
}
