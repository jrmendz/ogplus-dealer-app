package com.og.ogplus.dealerapp.view.sicbo;

import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.dealerapp.game.SicBoGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.RoadMapPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ConditionalOnBean(SicBoGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class SicBoLayout extends DealerAppWindowsLayout implements SicBoView{
    private GamePanel gamePanel;

    @Override
    public void reset() {
        super.reset();
        clear();
    }

    @Override
    protected RoadMapPanel generateRoadMapPanel() {
        return new RoadMapPanel(6, 4);
    }

    @Override
    protected JComponent getGameComponent() {
        return this.gamePanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        SicBoGame.SicBoGameResult result = (SicBoGame.SicBoGameResult) gameResult;

        return Arrays.stream(result.getPoints()).map(Position::getReadableFormat).collect(Collectors.joining(", "));
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        SicBoGame.SicBoGameResult result = (SicBoGame.SicBoGameResult) gameResult;

        return createImage(result.getPoints());
    }

    public ButtonGroup getButtonGroup(int index) {
        return gamePanel.getButtonGroups().get(index);
    }

    public List<ButtonGroup> getButtonGroup() {
        return gamePanel.getButtonGroups();
    }

    public boolean isSelectedThreeDice() {
        return 3 == gamePanel.getButtonGroups().stream()
                .map(ButtonGroup::getSelection)
                .filter(Objects::nonNull)
                .count();
    }

    public List<SicBoPoint> getSelectedDicePoint() {
        return gamePanel.getButtonGroups().stream()
                .map(ButtonGroup::getSelection)
                .filter(Objects::nonNull)
                .map(ButtonModel::getActionCommand)
                .map(SicBoPoint::valueOf)
                .collect(Collectors.toList());
    }

    public void clear() {
        gamePanel.getButtonGroups().forEach(ButtonGroup::clearSelection);
    }

    public void setAllButtonsEnabled(final boolean isEnabled) {
        gamePanel.getButtonGroups()
                .forEach(buttonGroup -> {
                    Enumeration<AbstractButton> iterator = buttonGroup.getElements();
                    while (iterator.hasMoreElements()) {
                        iterator.nextElement().setEnabled(isEnabled);
                    }
                });
        revalidate();
        repaint();
    }

    private Image createImage(SicBoPoint[] points) {
        int imageSize = 45;
        int padding = 2;

        Image img1 = GamePanel.imageByPoint.get(points[0]).getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        Image img2 = GamePanel.imageByPoint.get(points[1]).getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        Image img3 = GamePanel.imageByPoint.get(points[2]).getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(imageSize * 3 + padding * 4, imageSize + padding * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(img1, padding, padding, null);
        graphics.drawImage(img2, padding * 2 + imageSize, padding, null);
        graphics.drawImage(img3, padding * 3 + imageSize * 2, padding, null);
        return bufferedImage;
    }

    @Autowired
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}