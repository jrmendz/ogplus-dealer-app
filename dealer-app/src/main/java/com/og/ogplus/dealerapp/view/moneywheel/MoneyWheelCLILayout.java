package com.og.ogplus.dealerapp.view.moneywheel;

import javax.swing.JButton;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.MoneyWheelSymbol;
import com.og.ogplus.dealerapp.game.MoneyWheelGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(MoneyWheelGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class MoneyWheelCLILayout extends DealerAppLinuxLayout implements MoneyWheelView {
    @Override
    public JButton getSymbolButton(MoneyWheelSymbol symbol) {
        // TODO Auto-generated method stub
        return null;
    }

}