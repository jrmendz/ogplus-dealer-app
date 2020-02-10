package com.og.ogplus.dealerapp.view.threecards;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.og.ogplus.dealerapp.game.ThreeCardsGame;
import com.og.ogplus.dealerapp.view.DealerAppLinuxLayout;

@ConditionalOnBean(ThreeCardsGame.class)
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class ThreeCardsCLILayout extends DealerAppLinuxLayout implements ThreeCardsView {

}
