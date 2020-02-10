package com.og.ogplus.dealerapp.view.dt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.dealerapp.game.DragonTigerGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(DragonTigerGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class DragonTigerCLILayout extends DealerAppLinuxLayout implements DragonTigerView{

}
