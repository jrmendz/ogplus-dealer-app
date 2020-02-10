package com.og.ogplus.dealerapp.view;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;

import com.og.ogplus.dealerapp.game.model.GameResult;

public interface DealerAppView {

    default void addMenu(String string, ActionListener actionListener) {
    };

    default void showMenu() {
    };

    default void setVisible(boolean b) {
    };

    default void setStatus(String string) {
    };

    default void setTableName(String number) {
    };

    default void setDealerName(String nickName) {
    };

    default void showResult(GameResult gameResult) {
    };

    default void clearRoadMap() {
    };

    default void reset() {
    };

    default void setStage(String simpleFormat) {
    };

    default void startCountDown(LocalDateTime bettingEndTime) {
    };

    default void stopCountDown() {
    };

    default void hideAlert() {
    };

    default void hideResult() {
    };

    default void showAlert(String string, int i, Color green, Color black, Duration ofSeconds) {
    };

    default void showResult(String string, int i, Color blue, Color white) {
    };

    default void addShuffleButtonAction(ActionListener actionListener) {
    };

    default void showAlert(String string, Color orange, Color black) {
    };

    public DetailsPanel getDetailsPanel();

    public JToggleButton getAutoStartButton();

    public JButton getStartButton();

    public String getStage();

    public JLabel getStatusLabel();

    public JRootPane getRootPane();

    public JButton getChangeDealerButton();

}
