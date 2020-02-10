package com.og.ogplus.dealerapp.controller.api.handler;

import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.SicBoGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "SIC_BO")
@Component
public class SicBoRequestHandler extends GameRequestHandler {
    private static final String KEY_POSITION = "position";
    private static final String KEY_CARD = "card";

    private SicBoGame sicBoGame;

    public SicBoRequestHandler(Game game) {
        super(game);
        this.sicBoGame = (SicBoGame) game;
    }

    @Override
    public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
        if (result instanceof Map) {
            Map resultMap = ((Map) result);
            if (!resultMap.containsKey(KEY_POSITION) || !resultMap.containsKey(KEY_CARD)) {
                throw new IllegalArgumentException();
            } else {
				/*
				 * BaccaratPosition position = BaccaratPosition.valueOf((String)
				 * resultMap.get(KEY_POSITION)); String cardCode = (String)
				 * resultMap.get(KEY_CARD); String suit = cardCode.substring(cardCode.length() -
				 * 1); String rank = cardCode.substring(0, cardCode.length() - 1);
				 * 
				 * BaccaratCard card = new BaccaratCard(Suit.valueOf(suit), Rank.valueOf(rank));
				 * baccaratGame.setCard(stage, position, card);
				 */
                return true;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
