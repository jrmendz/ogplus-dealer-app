package com.og.ogplus.dealerapp.view.dt;

import java.awt.Image;

import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface DragonTigerView extends DealerAppView {
   
    default void setCard(DragonTigerPosition position, Image cardSlotActive) {
    };

    default void setBlinkCard(DragonTigerPosition position) {
    };

    default void clearBlinkCard(DragonTigerPosition dragonTigerPosition) {
    };

 
    
   
}
