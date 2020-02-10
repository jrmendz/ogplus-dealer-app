package com.og.ogplus.dealerapp.view.roulette;

import javax.swing.JButton;

import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface RouletteView extends DealerAppView {
    default void disableSlotButtons() {
    };

    default void enableSlotButtons() {
    };

    public JButton getSlotButton(RouletteSlot slot);

}
