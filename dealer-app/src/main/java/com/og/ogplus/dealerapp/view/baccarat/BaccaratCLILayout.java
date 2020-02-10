package com.og.ogplus.dealerapp.view.baccarat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.dealerapp.game.BaccaratGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(BaccaratGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class BaccaratCLILayout extends DealerAppLinuxLayout implements BaccaratView {

}
