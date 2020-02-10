package com.og.ogplus.dealerapp.view.dt;

import java.awt.Image;

import javax.swing.JComponent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.dealerapp.game.DragonTigerGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppWindowsLayout;
import com.og.ogplus.dealerapp.view.GameImages;

@ConditionalOnBean(DragonTigerGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "true")
@Component
public class DragonTigerLayout extends DealerAppWindowsLayout implements DragonTigerView{

    private static final long serialVersionUID = -1737859687248478652L;

    private CardsPanel cardsPanel;

    @Override
    public void reset() {
        super.reset();
        cardsPanel.getDragonCardView().setCardImage(GameImages.CARD_SLOT);
        cardsPanel.getTigerCardView().setCardImage(GameImages.CARD_SLOT);
    }

    @Override
    protected JComponent getGameComponent() {
        this.cardsPanel = new CardsPanel();
        return this.cardsPanel;
    }

    @Override
    protected String getResultText(GameResult gameResult) {
        DragonTigerGame.DragonTigerGameResult result = (DragonTigerGame.DragonTigerGameResult) gameResult;

        if (result.getResult().contains(DragonTigerGame.Result.DRAGON)) {
            return "DRAGON WIN";
        } else if (result.getResult().contains(DragonTigerGame.Result.TIGER)) {
            return "TIGER WIN";
        } else {
            return "TIE";
        }
    }

    @Override
    protected Image parseRoadMapImage(GameResult gameResult) {
        DragonTigerGame.DragonTigerGameResult result = (DragonTigerGame.DragonTigerGameResult) gameResult;

        if (result.getResult().contains(DragonTigerGame.Result.DRAGON)) {
            return GameImages.ROADMAP_DRAGON;
        } else if (result.getResult().contains(DragonTigerGame.Result.TIGER)) {
            return GameImages.ROADMAP_TIGER;
        } else {
            return GameImages.ROADMAP_TIE;
        }
    }

    public void setCard(DragonTigerPosition position, Image image) {
        switch (position) {
            case DRAGON:
                cardsPanel.getDragonCardView().setCardImage(image);
                break;
            case TIGER:
                cardsPanel.getTigerCardView().setCardImage(image);
                break;
        }
    }

    /**
     * 设置卡片闪动
     *
     * @param position
     */
    public void setBlinkCard(DragonTigerPosition position) {
        switch (position) {
            case DRAGON:
                cardsPanel.getDragonCardView().blinkBorder();
                break;
            case TIGER:
                cardsPanel.getTigerCardView().blinkBorder();
                break;
        }
    }

    /**
     * 清除卡片闪动
     *
     * @param position
     */
    public void clearBlinkCard(DragonTigerPosition position) {
        switch (position) {
            case DRAGON:
                cardsPanel.getDragonCardView().unBlinkBorder();
                break;
            case TIGER:
                cardsPanel.getTigerCardView().unBlinkBorder();
                break;
        }
    }

}
