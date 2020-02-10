package com.og.ogplus.dealerapp.view.bullbulll;

import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.dealerapp.game.BullbullGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.FreezeHeaderRoadMap;
import com.og.ogplus.dealerapp.view.GameImages;
import com.og.ogplus.dealerapp.view.RoadMapPanel;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnBean(BullbullGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class BullbullLayout extends DealerAppWindowsLayout implements BullbullView{
    private CardsPanel cardsPanel;

    private Map<BullbullGame.CardsType, Image> bankerRoadMapImages;
    private Map<BullbullGame.CardsType, Image> playerWinRoadMapImages;
    private Map<BullbullGame.CardsType, Image> playerLoseMapImages;

    public BullbullLayout() throws HeadlessException {
        this.cardsPanel = new CardsPanel();
        bankerRoadMapImages = new HashMap<>();
        bankerRoadMapImages.put(BullbullGame.CardsType.FNIUNIU, GameImages.BNIUNIU);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIUNIU, GameImages.BNIUNIU);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU9, GameImages.BNIU9);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU8, GameImages.BNIU8);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU7, GameImages.BNIU7);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU6, GameImages.BNIU6);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU5, GameImages.BNIU5);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU4, GameImages.BNIU4);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU3, GameImages.BNIU3);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU2, GameImages.BNIU2);
        bankerRoadMapImages.put(BullbullGame.CardsType.NIU1, GameImages.BNIU1);
        bankerRoadMapImages.put(BullbullGame.CardsType.NO_NIU, GameImages.BNONIU);

        playerWinRoadMapImages = new HashMap<>();
        playerWinRoadMapImages.put(BullbullGame.CardsType.FNIUNIU, GameImages.PWNIUNIU);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIUNIU, GameImages.PWNIUNIU);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU9, GameImages.PWNIU9);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU8, GameImages.PWNIU8);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU7, GameImages.PWNIU7);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU6, GameImages.PWNIU6);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU5, GameImages.PWNIU5);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU4, GameImages.PWNIU4);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU3, GameImages.PWNIU3);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU2, GameImages.PWNIU2);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NIU1, GameImages.PWNIU1);
        playerWinRoadMapImages.put(BullbullGame.CardsType.NO_NIU, GameImages.PWNONIU);

        playerLoseMapImages = new HashMap<>();
        playerLoseMapImages.put(BullbullGame.CardsType.FNIUNIU, GameImages.PLNIUNIU);
        playerLoseMapImages.put(BullbullGame.CardsType.NIUNIU, GameImages.PLNIUNIU);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU9, GameImages.PLNIU9);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU8, GameImages.PLNIU8);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU7, GameImages.PLNIU7);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU6, GameImages.PLNIU6);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU5, GameImages.PLNIU5);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU4, GameImages.PLNIU4);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU3, GameImages.PLNIU3);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU2, GameImages.PLNIU2);
        playerLoseMapImages.put(BullbullGame.CardsType.NIU1, GameImages.PLNIU1);
        playerLoseMapImages.put(BullbullGame.CardsType.NO_NIU, GameImages.PLNONIU);
    }

    @Override
    protected JComponent getGameComponent() {
        return this.cardsPanel;
    }

    @Override
    protected RoadMapPanel generateRoadMapPanel() {
        return new FreezeHeaderRoadMap(new Image[]{GameImages.BANKER, GameImages.PLAYER1, GameImages.PLAYER2, GameImages.PLAYER3}, 7);
    }

    @Override
    public void showResult(GameResult gameResult) {
        setStatus(getResultText(gameResult));

        BullbullGame.BullbullGameResult result = (BullbullGame.BullbullGameResult) gameResult;
        BullbullGame.CardsType bankerCardsType = result.getBankerCardsType();
        BullbullGame.CardsType player1CardsType = result.getPlayer1CardsType();
        BullbullGame.CardsType player2CardsType = result.getPlayer2CardsType();
        BullbullGame.CardsType player3CardsType = result.getPlayer3CardsType();

        addRoadMap(getBankerRoadMapImage(bankerCardsType),
                getPlayerRoadMapImage(player1CardsType, result.getResults()[0] == BullbullGame.Result.PLAYER1),
                getPlayerRoadMapImage(player2CardsType, result.getResults()[1] == BullbullGame.Result.PLAYER2),
                getPlayerRoadMapImage(player3CardsType, result.getResults()[2] == BullbullGame.Result.PLAYER3));

        revalidate();
        repaint();
    }


    @Override
    protected String getResultText(GameResult gameResult) {
        BullbullGame.BullbullGameResult result = (BullbullGame.BullbullGameResult) gameResult;

        StringBuilder sb = new StringBuilder();

        for (BullbullGame.Result r : result.getResults()) {
            if (r != BullbullGame.Result.BANKER) {
                sb.append(r).append(", ");
            }
        }

        String resultText = StringUtils.stripEnd(sb.toString(), ", ");
        if (StringUtils.isBlank(resultText)) {
            return "BANKER WIN";
        } else {
            return resultText + " WIN";
        }
    }

    private Image getBankerRoadMapImage(BullbullGame.CardsType bankerCardsType) {
        return bankerRoadMapImages.get(bankerCardsType);
    }

    private Image getPlayerRoadMapImage(BullbullGame.CardsType playerCardsType, boolean isWin) {
        return isWin ? playerWinRoadMapImages.get(playerCardsType) : playerLoseMapImages.get(playerCardsType);
    }

    @Override
    public void reset() {
        super.reset();
        Arrays.stream(BullbullPosition.values()).forEach(position -> setCard(position, GameImages.CARD_SLOT));
        cardsPanel.setBankerResult("");
        cardsPanel.setPlayer1Result("");
        cardsPanel.setPlayer2Result("");
        cardsPanel.setPlayer3Result("");
    }

    public void setCard(BullbullPosition position, Image image) {
        cardsPanel.getCardView(position).setCardImage(image);
    }

    public void setBankerCardsType(BullbullGame.CardsType cardsType) {
        cardsPanel.setBankerResult(parseResultText(cardsType));
    }

    public void setPlayer1CardsType(BullbullGame.CardsType cardsType) {
        cardsPanel.setPlayer1Result(parseResultText(cardsType));
    }

    public void setPlayer2CardsType(BullbullGame.CardsType cardsType) {
        cardsPanel.setPlayer2Result(parseResultText(cardsType));
    }

    public void setPlayer3CardsType(BullbullGame.CardsType cardsType) {
        cardsPanel.setPlayer3Result(parseResultText(cardsType));
    }

    private String parseResultText(BullbullGame.CardsType cardsType) {
        switch (cardsType) {
            case FNIUNIU:
                return "5G";
            case NIUNIU:
                return "NN";
            case NIU9:
                return "N9";
            case NIU8:
                return "N8";
            case NIU7:
                return "N7";
            case NIU6:
                return "N6";
            case NIU5:
                return "N5";
            case NIU4:
                return "N4";
            case NIU3:
                return "N3";
            case NIU2:
                return "N2";
            case NIU1:
                return "N1";
            case NO_NIU:
            default:
                return "X";
        }

    }


    /**
     * 设置�?�片闪动
     *
     * @param position
     */
    public void setBlinkCard(BullbullPosition position) {
        cardsPanel.getCardView(position).blinkBorder();
    }

    /**
     * 清除�?�片闪动
     *
     * @param position
     */
    public void clearBlinkCard(BullbullPosition position) {
        cardsPanel.getCardView(position).unBlinkBorder();
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        return null;
    }

}
