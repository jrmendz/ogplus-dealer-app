package com.og.ogplus.dealerapp.view.roulette;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.dealerapp.game.RouletteGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;

@ConditionalOnBean(RouletteGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class RouletteLayout extends DealerAppWindowsLayout implements RouletteView{
    private GamePanel gamePanel;

    @Override
    protected JComponent getGameComponent() {
        return this.gamePanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        RouletteGame.RouletteGameResult result = (RouletteGame.RouletteGameResult) gameResult;

        return result.getSlot().getReadableFormat();
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        RouletteGame.RouletteGameResult result = (RouletteGame.RouletteGameResult) gameResult;

        return createRoadMapImage(result.getSlot());
    }

    public JButton getSlotButton(RouletteSlot slot) {
        return gamePanel.getButtonMap().get(slot);
    }

    private Image createRoadMapImage(RouletteSlot slot) {
        int width = 50, height = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(qualityHints);
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setPaint(GamePanel.getSlotColor(slot));
        g.fillRoundRect(2, 2, 45, 45, 10, 10);

        g.setPaint(Color.WHITE);
        g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 18));
        g.drawString(slot.getReadableFormat(), 25 - slot.getReadableFormat().length() * 5, 30);

        return image;
    }

    public void enableSlotButtons() {
        gamePanel.getButtonMap().values().forEach(button -> button.setEnabled(true));
        revalidate();
        repaint();
    }

    public void disableSlotButtons() {
        gamePanel.getButtonMap().values().forEach(button -> button.setEnabled(false));
        revalidate();
        repaint();
    }

    @Autowired
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}