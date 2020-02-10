package com.og.ogplus.dealerapp.view.fantan;

import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.dealerapp.game.FanTanGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.GameImages;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.*;

@ConditionalOnBean(FanTanGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class FanTanLayout extends DealerAppWindowsLayout implements  FanTanView{
    private static final Map<FanTanSymbol, Image> ROADMAPIMAGEMAPPER;

    static {
        ROADMAPIMAGEMAPPER = new HashMap<>();
        ROADMAPIMAGEMAPPER.put(FanTanSymbol.FT_1, GameImages.FANTAN_1);
        ROADMAPIMAGEMAPPER.put(FanTanSymbol.FT_2, GameImages.FANTAN_2);
        ROADMAPIMAGEMAPPER.put(FanTanSymbol.FT_3, GameImages.FANTAN_3);
        ROADMAPIMAGEMAPPER.put(FanTanSymbol.FT_4, GameImages.FANTAN_4);
    }

    private GamePanel gamePanel;

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected JComponent getGameComponent() {
        this.gamePanel = new GamePanel();
        return this.gamePanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        FanTanGame.FanTanGameResult result = (FanTanGame.FanTanGameResult) gameResult;

        return result.getSymbol().getReadableFormat();
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        return null;
    }

    public void addRoadMap(FanTanSymbol symbol) {
        addRoadMap(ROADMAPIMAGEMAPPER.get(symbol));
    }

    public void showSymbol(FanTanSymbol symbol) {
        showAlert(symbol.getReadableFormat(), 156, GamePanel.COLORS[symbol.ordinal()],
                Color.BLACK, null);
    }

    public JButton getSymbolButton(FanTanSymbol symbol) {
        return gamePanel.getButtonMap().get(symbol);
    }
}