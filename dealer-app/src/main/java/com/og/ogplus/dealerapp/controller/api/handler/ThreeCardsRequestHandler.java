package com.og.ogplus.dealerapp.controller.api.handler;

import com.og.ogplus.common.enums.ThreeCardsPosition;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.common.model.Suit;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.ThreeCardsGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@ConditionalOnBean(ThreeCardsGame.class)
@Component
public class ThreeCardsRequestHandler extends GameRequestHandler {
	private static final String KEY_POSITION = "position";
	private static final String KEY_CARD = "card";

	private ThreeCardsGame threeCardsGame;

	public ThreeCardsRequestHandler(Game game) {
		super(game);
		this.threeCardsGame = (ThreeCardsGame) game;
	}

	@Override
	public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
		if (result instanceof Map) {
			Map resultMap = ((Map) result);
			if (!resultMap.containsKey(KEY_POSITION) || !resultMap.containsKey(KEY_CARD)) {
				throw new IllegalArgumentException();
			} else if ((String) resultMap.get(KEY_CARD) == null) {
				ThreeCardsPosition position = ThreeCardsPosition.valueOf((String) resultMap.get(KEY_POSITION));
				threeCardsGame.setCard(stage, position, null);
				return true;
			} else {
				ThreeCardsPosition position = ThreeCardsPosition.valueOf((String) resultMap.get(KEY_POSITION));
				String cardCode = (String) resultMap.get(KEY_CARD);
				String suit = cardCode.substring(cardCode.length() - 1);
				String rank = cardCode.substring(0, cardCode.length() - 1);
				Card card = new Card(Suit.of(suit), Rank.of(rank));
				threeCardsGame.setCard(stage, position, card);
				return true;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
