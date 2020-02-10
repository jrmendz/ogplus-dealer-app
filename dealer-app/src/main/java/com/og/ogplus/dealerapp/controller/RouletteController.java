package com.og.ogplus.dealerapp.controller;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.roulette.BroadcastMessage;
import com.og.ogplus.common.message.roulette.ProcessMessage;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.RouletteGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.game.model.RoulettePacket;
import com.og.ogplus.dealerapp.service.CameraService;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import com.og.ogplus.dealerapp.view.roulette.GamePanel;
import com.og.ogplus.dealerapp.view.roulette.RouletteConfirmDialog;
import com.og.ogplus.dealerapp.view.roulette.RouletteView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(RouletteGame.class)
@Component
public class RouletteController extends AbstractGameController implements RouletteGame.Listener {
    private  RouletteView layout;
   
    private ScheduledFuture cameraSwitchFuture;
    
    private RouletteConfirmDialog confirmDialog;
    
    @Value("${roulette.camera-delay.result:10}")
    private int switchResultCameraDelayTime;

    @Override
    public void init() {
        if (checkGui()) {
            super.init();
            Arrays.stream(RouletteSlot.values()).forEach(slot -> {
                layout.getSlotButton(slot).addActionListener(e -> {
                    confirmDialog.showDialog(slot);
                    if (confirmDialog.isConfirm()) {
                        ((RouletteGame) getGame()).setSlot(slot);
                    }
                });
            });
            layout.disableSlotButtons();
            }
    
        
    }

 
    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        transitCamera(CameraService.Mode.DEFAULT);
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        RouletteGame.RouletteGameResult result = (RouletteGame.RouletteGameResult) gameResult;
        return new ProcessMessage(result.getStage().getDate(), result.getSlot());

    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        RouletteGame.RouletteGameResult result = (RouletteGame.RouletteGameResult) gameResult;
        return new BroadcastMessage(result.getSlot(), result.getSlot());
    }

    @Override
    public void onSpin() {
        if (checkGui()) {
            SwingUtilities.invokeLater(() -> layout.enableSlotButtons());  
            Arrays.stream(RouletteSlot.values()).forEach(slot -> layout.getSlotButton(slot).setEnabled(true));
        }
        layout.setStatus("Spin Wheel(" + (getGame().getStage().getRound() % 2 == 0 ? "EVEN" : "ODD") + ") And Ball.");
        transitCamera(CameraService.Mode.ZOOMED);
        cameraSwitchFuture = getTaskScheduler().schedule(() -> transitCamera(CameraService.Mode.RESULT), Instant.now().plusSeconds(switchResultCameraDelayTime));
    }

    @Override
    public void onWheelStop(RouletteSlot rouletteSlot) {
        layout.setStatus("SPIN RESULT: " + rouletteSlot.getReadableFormat());
        layout.showResult(rouletteSlot.getReadableFormat(), 156, GamePanel.getSlotColor(rouletteSlot), Color.WHITE);
        if (checkGui()) {
            SwingUtilities.invokeLater(() -> layout.disableSlotButtons()); 
        }
        sendGameMessageOut(new BroadcastMessage(null, rouletteSlot));

        if (cameraSwitchFuture == null) {
            transitCamera(CameraService.Mode.RESULT);
        } else if (!cameraSwitchFuture.isDone()) {
            cameraSwitchFuture.cancel(true);
            transitCamera(CameraService.Mode.RESULT);
        }
    }

    @Override
    public void onRotorStateChange(RoulettePacket.GameState gameState) {
        switch (gameState) {
            case START_GAME:
                layout.showAlert("Rotor Ready", 36, Color.GREEN, Color.BLACK, Duration.ofSeconds(3));
                break;
            case PLACE_BETS:
                layout.showAlert("Wheel Rotating", 36, Color.GREEN, Color.BLACK, null);
                break;
            case BALL_IN_RIM:
                layout.showAlert("Ball In Race Track", 36, Color.GREEN, Color.BLACK, null);
                break;
            case NO_MORE_BETS:
                layout.showAlert("Wait for Result", 36, Color.GREEN, Color.BLACK, null);
                break;
            case WINNING_NUMBER:
                break;
            default:
                layout.showAlert("Rotor in Idle Mode", 36, Color.RED, Color.BLACK, null);
                break;
        }
    }

    @Override
    public void onRotorDisconnect() {
        onAlert("Disconnect from Rotor, Reconnecting ...");
    }
    
   
    @Override
    public void onRotorConnect() {
        layout.showAlert("Connect to Rotor", 36, Color.GREEN, Color.BLACK, Duration.ofSeconds(3));
    }

    @Autowired
    public void setLayout(RouletteView layout) {
        this.layout = layout;
    }
    
    @Autowired(required = false)
    public void setConfirmDialog(RouletteConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    @Autowired(required = false)
    @Qualifier("gameInfoPublisher")
    @Override
    public void setGameServerClientService(GameServerClientService gameServerClientService) {
        super.setGameServerClientService(gameServerClientService);
    }

}
