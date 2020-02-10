package com.og.ogplus.dealerapp.view.sicbo;

import java.util.List;

import javax.swing.ButtonGroup;

import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.dealerapp.view.DealerAppView;

public interface SicBoView extends DealerAppView {
    public List<ButtonGroup> getButtonGroup();

    public ButtonGroup getButtonGroup(int index);

    public List<SicBoPoint> getSelectedDicePoint();

    public boolean isSelectedThreeDice();

    default void setAllButtonsEnabled(boolean b) {
    };

    default void clear() {
    };

}
