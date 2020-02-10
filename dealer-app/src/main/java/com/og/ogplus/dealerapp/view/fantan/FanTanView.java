package com.og.ogplus.dealerapp.view.fantan;

import javax.swing.JButton;

import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface FanTanView extends DealerAppView {
    default void addRoadMap(FanTanSymbol symbol) {
    };

    default void showSymbol(FanTanSymbol fanTanSymbol) {
    }

    public JButton getSymbolButton(FanTanSymbol symbol);

}
