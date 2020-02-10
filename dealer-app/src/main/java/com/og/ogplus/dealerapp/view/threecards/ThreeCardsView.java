package com.og.ogplus.dealerapp.view.threecards;

import java.awt.Image;

import com.og.ogplus.common.enums.ThreeCardsPosition;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface ThreeCardsView extends DealerAppView {
    default void setCard(ThreeCardsPosition position, Image cardSlotActive) {
    };

    default void setBlinkCard(ThreeCardsPosition position) {
    };

    default void clearBlinkCard(ThreeCardsPosition threeCardsPosition) {
    };

    default void setDragonPoint(String dCardsDisResult) {
    };

    default void setPhoenixPoint(String dCardsDisResult) {
    };

}
