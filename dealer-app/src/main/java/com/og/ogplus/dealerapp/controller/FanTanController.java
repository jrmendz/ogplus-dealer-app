package com.og.ogplus.dealerapp.controller;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.fantan.BroadcastMessage;
import com.og.ogplus.common.message.fantan.ProcessMessage;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.FanTanGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import com.og.ogplus.dealerapp.view.fantan.FanTanConfirmDialog;
import com.og.ogplus.dealerapp.view.fantan.FanTanView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(FanTanGame.class)
@Component
public class FanTanController extends AbstractGameController implements FanTanGame.Listener {
    private FanTanView layout;
    private final String ACTION_FANTAN_1 = "fantan 1";
    private final String ACTION_FANTAN_2 = "fantan 2";
    private final String ACTION_FANTAN_3 = "fantan 3";
    private final String ACTION_FANTAN_4 = "fantan 4";
    private FanTanConfirmDialog confirmDialog;
    @Override
    public void init() {
        if (checkGui()) {
        super.init();
        Arrays.stream(FanTanSymbol.values()).forEach(symbol -> {
            layout.getSymbolButton(symbol).setEnabled(false);
            layout.getSymbolButton(symbol).addActionListener(e -> {
                confirmDialog.showDialog(symbol);
                if (confirmDialog.isConfirm()) {
                    ((FanTanGame) getGame()).setCurrentSymbol(symbol);
                }
            });
        });

        InputMap inputMap = layout.getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), ACTION_FANTAN_1);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), ACTION_FANTAN_2);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), ACTION_FANTAN_3);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), ACTION_FANTAN_4);

        ActionMap actionMap = layout.getRootPane().getActionMap();
        actionMap.put(ACTION_FANTAN_1, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout.getSymbolButton(FanTanSymbol.FT_1).doClick();
            }
        });

        actionMap.put(ACTION_FANTAN_2, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout.getSymbolButton(FanTanSymbol.FT_2).doClick();
            }
        });

        actionMap.put(ACTION_FANTAN_3, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout.getSymbolButton(FanTanSymbol.FT_3).doClick();
            }
        });

        actionMap.put(ACTION_FANTAN_4, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                layout.getSymbolButton(FanTanSymbol.FT_4).doClick();
            }
        });
        }
    }

    @Override
    public void onGameInitialized(Table table) {
        super.onGameInitialized(table);
        getGameResultModel().iterator()
            .forEachRemaining(gameResult -> {
                FanTanSymbol symbol = ((FanTanGame.FanTanGameResult) gameResult).getSymbol();
                layout.addRoadMap(symbol);
            });
    }

    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        FanTanGame.FanTanGameResult result = (FanTanGame.FanTanGameResult) gameResult;

        FanTanSymbol fanTanSymbol = result.getSymbol();

        return new ProcessMessage(gameResult.getStage().getDate(), fanTanSymbol);
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        FanTanGame.FanTanGameResult result = (FanTanGame.FanTanGameResult) gameResult;

        FanTanSymbol fanTanSymbol = result.getSymbol();

        return new BroadcastMessage(fanTanSymbol, fanTanSymbol);
    }

    @Override
    public void onUpdateGameInfo() {
    }

    @Override
    public void onStart() {
        Arrays.stream(FanTanSymbol.values()).forEach(symbol -> {
            if(layout.getSymbolButton(symbol)!=null) {
                layout.getSymbolButton(symbol).setEnabled(true);  
            }
            }
        );
       
        layout.setStatus("Start Split.");
    }

    @Override
    public void onStop(FanTanSymbol fanTanSymbol) {
        Arrays.stream(FanTanSymbol.values()).forEach(symbol -> {
            if(layout.getSymbolButton(symbol)!=null) {
                layout.getSymbolButton(symbol).setEnabled(true);  
            }
            }
        );
        layout.setStatus("SPLIT RESULT: " + fanTanSymbol.getReadableFormat());

        sendGameMessageOut(new BroadcastMessage(null, fanTanSymbol));

        layout.addRoadMap(fanTanSymbol);
        layout.showSymbol(fanTanSymbol);
    }

    @Autowired
    public void setLayout(FanTanView layout) {
        this.layout = layout;
    }

    @Autowired(required = false)
    public void setConfirmDialog(FanTanConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    @Autowired(required = false)
    @Qualifier("gameInfoPublisher")
    @Override
    public void setGameServerClientService(GameServerClientService gameServerClientService) {
        super.setGameServerClientService(gameServerClientService);
    }
   
}
