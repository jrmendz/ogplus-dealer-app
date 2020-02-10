package com.og.ogplus.dealerapp.view.threecards;

import com.og.ogplus.common.enums.ThreeCardsPosition;
import com.og.ogplus.dealerapp.game.ThreeCardsGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.GameImages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@ConditionalOnBean(ThreeCardsGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class ThreeCardsLayout extends DealerAppWindowsLayout implements ThreeCardsView{
    private ThreeCardsPanel threeCardsPanel;

    @Override
    protected JComponent getGameComponent() {
        this.threeCardsPanel = new ThreeCardsPanel();
        return this.threeCardsPanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        ThreeCardsGame.ThreeCardsGameResult result = (ThreeCardsGame.ThreeCardsGameResult) gameResult;
        if (result.getResult().stream().allMatch((rs) -> rs.name().startsWith(ThreeCardsGame.ThreeCardsWinner.PHOENIX.name()))) {
            return "PHOENIX WIN";
        } else if (result.getResult().stream().allMatch((rs) -> rs.name().startsWith(ThreeCardsGame.ThreeCardsWinner.DRAGON.name()))) {
            return "DRAGON WIN";
        } else {
            return "TIE";
        }
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
    	ThreeCardsGame.ThreeCardsGameResult result = (ThreeCardsGame.ThreeCardsGameResult) gameResult;

    	if (result.getResult().contains(ThreeCardsGame.ThreeCardsWinner.DRAGON)) {
            return GameImages.THREE_CARDS_DRAGON;
        } else if (result.getResult().contains(ThreeCardsGame.ThreeCardsWinner.PHOENIX)) {
            return GameImages.THREE_CARDS_PHOENIX;
        } else {
            return GameImages.THREE_CARDS_TIE;
        }
    }

    @Override
    public void reset() {
        super.reset();
        setCard(ThreeCardsPosition.PHOENIX1, GameImages.CARD_SLOT);
        setCard(ThreeCardsPosition.PHOENIX2, GameImages.CARD_SLOT);
        setCard(ThreeCardsPosition.PHOENIX3, GameImages.CARD_SLOT);
        setCard(ThreeCardsPosition.DRAGON1, GameImages.CARD_SLOT);
        setCard(ThreeCardsPosition.DRAGON2, GameImages.CARD_SLOT);
        setCard(ThreeCardsPosition.DRAGON3, GameImages.CARD_SLOT);
        setPhoenixPoint("");
        setDragonPoint("");
    }

    public void setCard(ThreeCardsPosition position, Image image) {
        switch (position) {
            case PHOENIX1:
            	threeCardsPanel.getPhoenixCard1().setCardImage(image);
                break;
            case PHOENIX2:
            	threeCardsPanel.getPhoenixCard2().setCardImage(image);
                break;
            case PHOENIX3:
            	threeCardsPanel.getPhoenixCard3().setCardImage(image);
                break;
            case DRAGON1:
            	threeCardsPanel.getDragonCard1().setCardImage(image);
                break;
            case DRAGON2:
            	threeCardsPanel.getDragonCard2().setCardImage(image);
                break;
            case DRAGON3:
            	threeCardsPanel.getDragonCard3().setCardImage(image);
                break;
        }
    }

    /**
     * 设置卡片闪动
     *
     * @param position
     */
    public void setBlinkCard(ThreeCardsPosition position) {
        switch (position) {
            case PHOENIX1:
            	threeCardsPanel.getPhoenixCard1().blinkBorder();
                break;
            case PHOENIX2:
            	threeCardsPanel.getPhoenixCard2().blinkBorder();
                break;
            case PHOENIX3:
            	threeCardsPanel.getPhoenixCard3().blinkBorder();
                break;
            case DRAGON1:
            	threeCardsPanel.getDragonCard1().blinkBorder();
                break;
            case DRAGON2:
            	threeCardsPanel.getDragonCard2().blinkBorder();
                break;
            case DRAGON3:
            	threeCardsPanel.getDragonCard3().blinkBorder();
                break;
        }
    }

    /**
     * 清除卡片闪动
     *
     * @param position
     */
    public void clearBlinkCard(ThreeCardsPosition position) {
        switch (position) {
            case PHOENIX1:
            	threeCardsPanel.getPhoenixCard1().unBlinkBorder();
                break;
            case PHOENIX2:
            	threeCardsPanel.getPhoenixCard2().unBlinkBorder();
                break;
            case PHOENIX3:
            	threeCardsPanel.getPhoenixCard3().unBlinkBorder();
                break;
            case DRAGON1:
            	threeCardsPanel.getDragonCard1().unBlinkBorder();
                break;
            case DRAGON2:
            	threeCardsPanel.getDragonCard2().unBlinkBorder();
                break;
            case DRAGON3:
            	threeCardsPanel.getDragonCard3().unBlinkBorder();
                break;
        }
    }

    public void setPhoenixPoint(String cardText) {
    	threeCardsPanel.getPhoenixResult().setText(cardText);
    }

    public void setDragonPoint(String cardText) {
    	threeCardsPanel.getDragonResult().setText(cardText);
    }
}
