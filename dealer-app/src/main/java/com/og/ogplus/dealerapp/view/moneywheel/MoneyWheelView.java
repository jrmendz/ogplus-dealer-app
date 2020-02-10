package com.og.ogplus.dealerapp.view.moneywheel;

import java.util.List;

import javax.swing.JButton;

import com.og.ogplus.common.enums.MoneyWheelSymbol;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface MoneyWheelView extends DealerAppView {
    default void addRoadMap(MoneyWheelSymbol symbol) {
    };

    default void showSymbol(MoneyWheelSymbol fanTanSymbol) {
    }

    public JButton getSymbolButton(MoneyWheelSymbol symbol);

    default void addSpinResult(MoneyWheelSymbol moneyWheelSymbol) {
    };

    default void showTotalOdds(List<MoneyWheelSymbol> symbols) {
    };

}
