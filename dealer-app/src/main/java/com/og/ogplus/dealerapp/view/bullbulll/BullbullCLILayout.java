package com.og.ogplus.dealerapp.view.bullbulll;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.dealerapp.game.BullbullGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(BullbullGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class BullbullCLILayout extends DealerAppLinuxLayout implements BullbullView{
  
}
