package com.og.ogplus.dealerapp.view.roulette;

import javax.swing.JButton;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.RouletteSlot;
import com.og.ogplus.dealerapp.game.RouletteGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(RouletteGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class RouletteCLILayout extends DealerAppLinuxLayout implements RouletteView{

    @Override
    public JButton getSlotButton(RouletteSlot slot) {
        // TODO Auto-generated method stub
        return null;
    }
   
}