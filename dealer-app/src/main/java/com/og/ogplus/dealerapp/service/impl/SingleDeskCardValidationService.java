package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.*;
import com.og.ogplus.dealerapp.service.CardValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Conditional(SingleDeskCardValidationService.ConfigNameCondition.class)
@Service
public class SingleDeskCardValidationService implements CardValidationService, AbstractGame.GameListener {

    private Set<String> set;

    static class ConfigNameCondition extends AnyNestedCondition {

        public ConfigNameCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(name = "app.game-category", havingValue = "THREECARDS")
        static class Value1Condition {
        }

        @ConditionalOnProperty(name = "app.game-category", havingValue = "NIUNIU")
        static class Value2Condition {
        }
    }

    @Override
    public synchronized boolean isValid(Card card) {
        String cardVal = card.getRank().getSymbol() + card.getSuit().getSymbol();
        if (set.contains(cardVal))
            return false;
        set.add(cardVal);
        return true;
    }

    @Override
    public void onRoundStart(Stage stage) {
        set = new HashSet<>();
    }
}
