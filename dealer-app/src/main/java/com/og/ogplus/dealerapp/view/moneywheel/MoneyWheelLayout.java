package com.og.ogplus.dealerapp.view.moneywheel;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.MoneyWheelSymbol;
import com.og.ogplus.dealerapp.game.MoneyWheelGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.GameImages;

@ConditionalOnBean(MoneyWheelGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class MoneyWheelLayout extends DealerAppWindowsLayout implements MoneyWheelView{
    private static final Map<MoneyWheelSymbol, Image> roadMapImageMapper;

    static {
        roadMapImageMapper = new HashMap<>();
        roadMapImageMapper.put(MoneyWheelSymbol.ODDS_1, GameImages.BUTTON_1);
        roadMapImageMapper.put(MoneyWheelSymbol.ODDS_2, GameImages.BUTTON_2);
        roadMapImageMapper.put(MoneyWheelSymbol.ODDS_5, GameImages.BUTTON_5);
        roadMapImageMapper.put(MoneyWheelSymbol.ODDS_10, GameImages.BUTTON_10);
        roadMapImageMapper.put(MoneyWheelSymbol.ODDS_20, GameImages.BUTTON_20);
        roadMapImageMapper.put(MoneyWheelSymbol.OG, GameImages.BUTTON_OG);
        roadMapImageMapper.put(MoneyWheelSymbol.MULTIPLIER_3, GameImages.BUTTON_X3);
    }

    private GamePanel gamePanel;

    @Override
    public void reset() {
        super.reset();
        gamePanel.getRoundSymbolLabel().setText("");
    }

    @Override
    protected JComponent getGameComponent() {
        this.gamePanel = new GamePanel();
        return this.gamePanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        MoneyWheelGame.MoneyWheelGameResult result = (MoneyWheelGame.MoneyWheelGameResult) gameResult;

        return "X" + calculateTotalOdds(Arrays.asList(result.getSymbols()));
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        return null;
    }

    public void addRoadMap(MoneyWheelSymbol symbol) {
        addRoadMap(roadMapImageMapper.get(symbol));
    }


    public void addSpinResult(MoneyWheelSymbol symbol) {
        gamePanel.getRoundSymbolLabel().setText(gamePanel.getRoundSymbolLabel().getText() + "  " + symbol.getReadableFormat());
    }

    public void showTotalOdds(List<MoneyWheelSymbol> symbols) {
        int totalOdds = calculateTotalOdds(symbols);
        MoneyWheelSymbol lastSymbol = symbols.get(symbols.size() - 1);
        showAlert("X" + totalOdds, 156,
                lastSymbol == MoneyWheelSymbol.MULTIPLIER_3 ? Color.WHITE : GamePanel.colors[lastSymbol.ordinal()],
                Color.BLACK, null);
    }

    private int calculateTotalOdds(List<MoneyWheelSymbol> symbols) {
        if (symbols != null && symbols.size() > 0) {
            OptionalInt totalOdds = symbols.stream().mapToInt(symbol -> {
                if (symbol == MoneyWheelSymbol.OG) {
                    return 40;
                } else if (symbol == MoneyWheelSymbol.MULTIPLIER_3) {
                    return 3;
                } else {
                    return Integer.parseInt(symbol.getReadableFormat());
                }
            }).reduce((a, b) -> a * b);
            return totalOdds.getAsInt();
        } else {
            return 0;
        }
    }

    public JButton getSymbolButton(MoneyWheelSymbol symbol) {
        return gamePanel.getButtonMap().get(symbol);
    }
}