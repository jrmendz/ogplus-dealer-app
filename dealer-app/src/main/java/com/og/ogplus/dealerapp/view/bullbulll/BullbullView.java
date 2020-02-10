package com.og.ogplus.dealerapp.view.bullbulll;

import java.awt.Image;

import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.dealerapp.game.BullbullGame.CardsType;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface BullbullView extends DealerAppView {
    default void setCard(BullbullPosition position, Image cardSlotActive){};

    default void setBlinkCard(BullbullPosition position){};

    default void clearBlinkCard(BullbullPosition bullbullPosition){};

    default void setBankerCardsType(CardsType calculateCardsType){};

    default void setPlayer1CardsType(CardsType calculateCardsType){};

    default void setPlayer2CardsType(CardsType calculateCardsType){};

    default void setPlayer3CardsType(CardsType calculateCardsType){};

    default void setCard(DragonTigerPosition position, Image cardSlotActive){};

    default void setBlinkCard(DragonTigerPosition position){};

    default void clearBlinkCard(DragonTigerPosition dragonTigerPosition){};

 
    
   
}
