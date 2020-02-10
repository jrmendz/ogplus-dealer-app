package com.og.ogplus.dealerapp.view.baccarat;

import com.og.ogplus.common.enums.BaccaratPosition;
import com.og.ogplus.dealerapp.game.BaccaratGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.GameImages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@ConditionalOnBean(BaccaratGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class BaccaratLayout extends DealerAppWindowsLayout implements BaccaratView{
    private CardsPanel cardsPanel;

    @Override
    protected JComponent getGameComponent() {
        this.cardsPanel = new CardsPanel();
        return this.cardsPanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        BaccaratGame.BaccaratGameResult result = (BaccaratGame.BaccaratGameResult) gameResult;

        if (result.getResult().contains(BaccaratGame.Result.PLAYER)) {
            return "PLAYER WIN";
        } else if (result.getResult().contains(BaccaratGame.Result.BANKER)) {
            return "BANKER WIN";
        } else {
            return "TIE";
        }
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        BaccaratGame.BaccaratGameResult result = (BaccaratGame.BaccaratGameResult) gameResult;

        if (result.getResult().contains(BaccaratGame.Result.PLAYER)) {
            return GameImages.ROADMAP_PLAYER;
        } else if (result.getResult().contains(BaccaratGame.Result.BANKER)) {
            return GameImages.ROADMAP_BANKER;
        } else {
            return GameImages.ROADMAP_TIE;
        }
    }

    @Override
    public void reset() {
        super.reset();
        setCard(BaccaratPosition.PLAYER1, GameImages.CARD_SLOT);
        setCard(BaccaratPosition.PLAYER2, GameImages.CARD_SLOT);
        setCard(BaccaratPosition.PLAYER3, GameImages.CARD_SLOT);
        setCard(BaccaratPosition.BANKER1, GameImages.CARD_SLOT);
        setCard(BaccaratPosition.BANKER2, GameImages.CARD_SLOT);
        setCard(BaccaratPosition.BANKER3, GameImages.CARD_SLOT);
        setPlayerTotalPoint(0);
        setBankerTotalPoint(0);
    }

    public void setCard(BaccaratPosition position, Image image) {
        switch (position) {
            case PLAYER1:
                cardsPanel.getPlayerCard1().setCardImage(image);
                break;
            case PLAYER2:
                cardsPanel.getPlayerCard2().setCardImage(image);
                break;
            case PLAYER3:
                cardsPanel.getPlayerCard3().setCardImage(image);
                break;
            case BANKER1:
                cardsPanel.getBankerCard1().setCardImage(image);
                break;
            case BANKER2:
                cardsPanel.getBankerCard2().setCardImage(image);
                break;
            case BANKER3:
                cardsPanel.getBankerCard3().setCardImage(image);
                break;
        }
    }

    /**
     * 设置卡片闪动
     *
     * @param position
     */
    public void setBlinkCard(BaccaratPosition position) {
        switch (position) {
            case PLAYER1:
                cardsPanel.getPlayerCard1().blinkBorder();
                break;
            case PLAYER2:
                cardsPanel.getPlayerCard2().blinkBorder();
                break;
            case PLAYER3:
                cardsPanel.getPlayerCard3().blinkBorder();
                break;
            case BANKER1:
                cardsPanel.getBankerCard1().blinkBorder();
                break;
            case BANKER2:
                cardsPanel.getBankerCard2().blinkBorder();
                break;
            case BANKER3:
                cardsPanel.getBankerCard3().blinkBorder();
                break;
        }
    }

    /**
     * 清除卡片闪动
     *
     * @param position
     */
    public void clearBlinkCard(BaccaratPosition position) {
        switch (position) {
            case PLAYER1:
                cardsPanel.getPlayerCard1().unBlinkBorder();
                break;
            case PLAYER2:
                cardsPanel.getPlayerCard2().unBlinkBorder();
                break;
            case PLAYER3:
                cardsPanel.getPlayerCard3().unBlinkBorder();
                break;
            case BANKER1:
                cardsPanel.getBankerCard1().unBlinkBorder();
                break;
            case BANKER2:
                cardsPanel.getBankerCard2().unBlinkBorder();
                break;
            case BANKER3:
                cardsPanel.getBankerCard3().unBlinkBorder();
                break;
        }
    }

    public void setPlayerTotalPoint(int point) {
        cardsPanel.getPlayerTotal().setText(Integer.toString(point));
    }

    public void setBankerTotalPoint(int point) {
        cardsPanel.getBankerTotal().setText(Integer.toString(point));
    }
}
