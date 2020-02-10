package com.og.ogplus.dealerapp.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.MoneyWheelSymbol;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.moneywheel.BroadcastMessage;
import com.og.ogplus.common.message.moneywheel.ProcessMessage;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.MoneyWheelGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.CameraService;
import com.og.ogplus.dealerapp.view.moneywheel.MoneyWheelConfirmDialog;
import com.og.ogplus.dealerapp.view.moneywheel.MoneyWheelView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(MoneyWheelGame.class)
@Component
public class MoneyWheelController extends AbstractGameController implements MoneyWheelGame.Listener {
    private MoneyWheelView layout;
    
    private ScheduledFuture cameraSwitchFuture;
    
    private ScheduledFuture cameraSwitchZoomedFuture;

    @Value("${money-wheel.camera-delay.result:10}")
    private int switchResultCameraDelayTime;
    
    @Value("${money-wheel.camera-delay.zoomed:10}")
    private int switchZoomedCameraDelayTime;
    
    private MoneyWheelConfirmDialog confirmDialog;
    
    @Override
    public void init() {
        if (checkGui()) {
        super.init();
        Arrays.stream(MoneyWheelSymbol.values()).forEach(symbol -> {
            layout.getSymbolButton(symbol).setEnabled(false);
            layout.getSymbolButton(symbol).addActionListener(e -> {
                confirmDialog.showDialog(symbol);
                if (confirmDialog.isConfirm()) {
                    ((MoneyWheelGame) getGame()).setCurrentSymbol(symbol);
                }
            });
        });
        }
    }

    @Override
    public void onGameInitialized(Table table) {
        super.onGameInitialized(table);
        getGameResultModel().iterator()
                .forEachRemaining(gameResult -> {
                    MoneyWheelSymbol[] symbols = ((MoneyWheelGame.MoneyWheelGameResult) gameResult).getSymbols();
                    for (MoneyWheelSymbol symbol : symbols) {
                        layout.addRoadMap(symbol);
                    }
                });
    }

    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        transitCamera(CameraService.Mode.DEFAULT);
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        MoneyWheelGame.MoneyWheelGameResult result = (MoneyWheelGame.MoneyWheelGameResult) gameResult;

        MoneyWheelSymbol[] moneyWheelSymbols = result.getSymbols();

        return new ProcessMessage(gameResult.getStage().getDate(), moneyWheelSymbols);
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        MoneyWheelGame.MoneyWheelGameResult result = (MoneyWheelGame.MoneyWheelGameResult) gameResult;

        MoneyWheelSymbol[] moneyWheelSymbols = result.getSymbols();

        return new BroadcastMessage(moneyWheelSymbols[moneyWheelSymbols.length - 1], moneyWheelSymbols);
    }

    @Override
    public void onUpdateGameInfo() {
    }

    @Override
    public void onSpin() {
        Arrays.stream(MoneyWheelSymbol.values()).forEach(symbol -> layout.getSymbolButton(symbol).setEnabled(true));
        layout.setStatus("Spin Wheel(" + (getGame().getStage().getRound() % 2 == 0 ? "EVEN" : "ODD") + ").");
        cameraSwitchZoomedFuture = getTaskScheduler().schedule(() -> transitCamera(CameraService.Mode.ZOOMED), Instant.now().plusSeconds(switchZoomedCameraDelayTime));
        cameraSwitchFuture = getTaskScheduler().schedule(() -> transitCamera(CameraService.Mode.RESULT), Instant.now().plusSeconds(switchResultCameraDelayTime));
    }

    @Override
    public void onWheelStop(MoneyWheelSymbol moneyWheelSymbol) {
        Arrays.stream(MoneyWheelSymbol.values()).forEach(symbol -> layout.getSymbolButton(symbol).setEnabled(false));
        layout.addSpinResult(moneyWheelSymbol);
        layout.setStatus("SPIN RESULT: " + moneyWheelSymbol.getReadableFormat());


        sendGameMessageOut(new BroadcastMessage(null, ((MoneyWheelGame) getGame()).getSymbols().toArray(new MoneyWheelSymbol[0])));
       
        if (cameraSwitchZoomedFuture == null) {
            transitCamera(CameraService.Mode.ZOOMED);
        } else if (!cameraSwitchZoomedFuture.isDone()) {
            cameraSwitchZoomedFuture.cancel(true);
            transitCamera(CameraService.Mode.ZOOMED);
        }
        
        if (cameraSwitchFuture == null) {
            transitCamera(CameraService.Mode.RESULT);
        } else if (!cameraSwitchFuture.isDone()) {
            cameraSwitchFuture.cancel(true);
            transitCamera(CameraService.Mode.RESULT);
        }

        layout.addRoadMap(moneyWheelSymbol);
        layout.showTotalOdds(((MoneyWheelGame) getGame()).getSymbols());
    }
    
    @Autowired(required = false)
    public void setConfirmDialog(MoneyWheelConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }
    
    @Autowired
    public void setLayout(MoneyWheelView layout) {
        this.layout = layout;
    }

}
