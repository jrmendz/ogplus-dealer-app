package com.og.ogplus.dealerapp.view.fantan;

import javax.swing.JButton;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.FanTanSymbol;
import com.og.ogplus.dealerapp.game.FanTanGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(FanTanGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class FanTanCLILayout extends DealerAppLinuxLayout implements FanTanView {

    @Override
    public JButton getSymbolButton(FanTanSymbol symbol) {
        // TODO Auto-generated method stub
        return null;
    }

}