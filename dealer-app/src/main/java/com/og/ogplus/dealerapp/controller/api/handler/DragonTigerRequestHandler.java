package com.og.ogplus.dealerapp.controller.api.handler;

import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.common.enums.DragonTigerType;
import com.og.ogplus.common.model.*;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.DragonTigerGame;
import com.og.ogplus.dealerapp.game.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionalOnBean(DragonTigerGame.class)
@Component
public class DragonTigerRequestHandler extends GameRequestHandler {
    private static final String KEY_POSITION = "position";
    private static final String KEY_CARD = "card";

    private DragonTigerGame dragonTigerGame;

    public DragonTigerRequestHandler(Game game) {
        super(game);
        dragonTigerGame = (DragonTigerGame) game;
    }

    @Override
    public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
        if (result instanceof Map) {
            Map resultMap = ((Map) result);
            if (!resultMap.containsKey(KEY_POSITION) || !resultMap.containsKey(KEY_CARD)) {
                throw new IllegalArgumentException();
            } else {
                DragonTigerPosition position = DragonTigerPosition.valueOf((String) resultMap.get(KEY_POSITION));
                String cardCode = (String) resultMap.get(KEY_CARD);
                String suit = cardCode.substring(cardCode.length() - 1);
                String rank = cardCode.substring(0, cardCode.length() - 1);
                Card card;
                if (dragonTigerGame.getType() == DragonTigerType.NEW) {
                    card = new Card(Suit.of(suit), Rank.of(rank));
                } else {
                    card = new ClassicDTCard(Suit.of(suit), Rank.of(rank));
                }
                dragonTigerGame.setCard(stage, position, card);
                return true;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
