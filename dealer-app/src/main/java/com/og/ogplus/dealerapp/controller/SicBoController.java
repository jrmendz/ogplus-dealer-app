package com.og.ogplus.dealerapp.controller;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.sicbo.BroadcastMessage;
import com.og.ogplus.common.message.sicbo.ProcessMessage;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.SicBoGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import com.og.ogplus.dealerapp.view.sicbo.SicBoConfirmDialog;
import com.og.ogplus.dealerapp.view.sicbo.SicBoView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(SicBoGame.class)
@Component
public class SicBoController extends AbstractGameController implements SicBoGame.Listener {
    private SicBoView layout;
    private static final String ACTION_board01 = "keyboard01";
    private static final String ACTION_board02 = "keyboard02";
    private static final String ACTION_board03 = "keyboard03";
    private static final String ACTION_board04 = "keyboard04";
    private static final String ACTION_board05 = "keyboard05";
    private static final String ACTION_board06 = "keyboard06";
    private static final String ACTION_CANCEL = "CANCEL";
    private SicBoConfirmDialog confirmDialog;
    @Override
    public void init() {
        if (checkGui()) {
        InputMap inputMap = getLayout().getRootPane().getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getLayout().getRootPane().getActionMap();
        super.init();
        SwingUtilities.invokeLater(() -> {
            layout.setAllButtonsEnabled(false);
        });
        IntStream.range(0, 3).mapToObj(index -> layout.getButtonGroup(index))
                .flatMap(buttonGroup -> Collections.list(buttonGroup.getElements()).stream())
                .forEach(button -> button.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (layout.isSelectedThreeDice()) {
                            List<SicBoPoint> points = layout.getSelectedDicePoint();
                            confirmDialog.showDialog(points);
                            if (confirmDialog.isConfirm()) {
                                ((SicBoGame) getGame()).setPoints(points.get(0), points.get(1), points.get(2));
                            } else {
                                layout.clear();
                            }
                        }
                    }
                }));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), ACTION_board01);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), ACTION_board02);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), ACTION_board03);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), ACTION_board04);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), ACTION_board05);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), ACTION_board06);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), ACTION_CANCEL);

        //sam
        actionMap.put(ACTION_board01, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_1);
            }
        });

        actionMap.put(ACTION_board02, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_2);
            }
        });

        actionMap.put(ACTION_board03, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_3);
            }
        });

        actionMap.put(ACTION_board04, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_4);
            }
        });

        actionMap.put(ACTION_board05, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_5);
            }
        });

        actionMap.put(ACTION_board06, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButtonSelected(SicBoPoint.POINT_6);
            }
        });

        actionMap.put(ACTION_CANCEL, new AbstractAction() {
            private long lastPressTimeMillis;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - lastPressTimeMillis < 200) {
                    for (int i=0; i<layout.getButtonGroup().size(); i++){
                        layout.clear();
                    }
                }
                lastPressTimeMillis = System.currentTimeMillis();
            }
        });
        }
    }

    public void setButtonSelected(SicBoPoint selectedDice) {
        List<ButtonGroup> buttonGroups = layout.getButtonGroup();

        for (ButtonGroup buttonGroup : buttonGroups) {
            if (buttonGroup.getSelection() == null) {
                Enumeration<AbstractButton> buttons = buttonGroup.getElements();
                int buttonIndex = getButtonIndex(selectedDice);
                int index = 0;
                AbstractButton button;
                do {
                    button = buttons.nextElement();
                } while (buttonIndex != index++);
                button.setSelected(true);
                break;
            }
        }
    }

    private int getButtonIndex(SicBoPoint sicBoPoint) {
        switch (sicBoPoint) {
            case POINT_1:
                return 0;
            case POINT_2:
                return 1;
            case POINT_3:
                return 2;
            case POINT_4:
                return 3;
            case POINT_5:
                return 4;
            case POINT_6:
                return 5;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        SicBoGame.SicBoGameResult result = (SicBoGame.SicBoGameResult) gameResult;

        return new ProcessMessage(gameResult.getStage().getDate(), result.getPoints());
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        SicBoGame.SicBoGameResult result = (SicBoGame.SicBoGameResult) gameResult;

        return new BroadcastMessage(result.getPoints(), result.getPoints());
    }

    @Override
    public void onDice() {
        if (checkGui()) {
        SwingUtilities.invokeLater(() -> {
            layout.setAllButtonsEnabled(true);
            layout.setStatus("Please Dice And Input Result.");
        });
        }
    }

    @Override
    public void onGameResult(GameResult gameResult) {
        super.onGameResult(gameResult);

        SicBoGame.SicBoGameResult result = (SicBoGame.SicBoGameResult) gameResult;
        if (checkGui()) {
        SwingUtilities.invokeLater(() -> {
            layout.setAllButtonsEnabled(false);
            layout.showAlert(Arrays.stream(result.getPoints()).map(Position::getReadableFormat).collect(Collectors.joining(", ")), 156, Color.WHITE, Color.BLACK, null);
        });
        }
    }

    @Autowired
    public void setLayout(SicBoView layout) {
        this.layout = layout;
    }

    @Autowired(required = false)
    public void setConfirmDialog(SicBoConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }
    
    @Autowired(required = false)
    @Qualifier("gameInfoPublisher")
    @Override
    public void setGameServerClientService(GameServerClientService gameServerClientService) {
        super.setGameServerClientService(gameServerClientService);
    }
}

