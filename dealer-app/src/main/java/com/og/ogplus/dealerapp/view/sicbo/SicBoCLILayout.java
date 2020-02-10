package com.og.ogplus.dealerapp.view.sicbo;

import java.util.List;

import javax.swing.ButtonGroup;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.SicBoPoint;
import com.og.ogplus.dealerapp.game.SicBoGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(SicBoGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class SicBoCLILayout extends DealerAppLinuxLayout implements SicBoView {

    @Override
    public List<ButtonGroup> getButtonGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ButtonGroup getButtonGroup(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SicBoPoint> getSelectedDicePoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSelectedThreeDice() {
        // TODO Auto-generated method stub
        return false;
    }

}