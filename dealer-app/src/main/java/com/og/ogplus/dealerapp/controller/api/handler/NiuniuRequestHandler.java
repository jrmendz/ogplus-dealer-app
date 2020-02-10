package com.og.ogplus.dealerapp.controller.api.handler;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.common.model.BullbullCard;
import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.common.model.Suit;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.BullbullGame;
import com.og.ogplus.dealerapp.game.Game;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(BullbullGame.class)
@Component
public class NiuniuRequestHandler extends GameRequestHandler {
	private static final String KEY_POSITION = "position";
	private static final String KEY_CARD = "card";

	private BullbullGame bullbullGame;

	public NiuniuRequestHandler(Game game) {
		super(game);
		this.bullbullGame = (BullbullGame) game;
	}

	@Override
	public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
		if (result instanceof Map) {
			Map resultMap = ((Map) result);
			if (!resultMap.containsKey(KEY_POSITION) || !resultMap.containsKey(KEY_CARD)) {
				throw new IllegalArgumentException();
			} else if ((String) resultMap.get(KEY_CARD) == null) {
			    BullbullPosition position = BullbullPosition.valueOf((String) resultMap.get(KEY_POSITION));
				bullbullGame.setCard(stage, position, null);
				return true;
			} else {
			    BullbullPosition position = BullbullPosition.valueOf((String) resultMap.get(KEY_POSITION));
				String cardCode = (String) resultMap.get(KEY_CARD);
				String suit = cardCode.substring(cardCode.length() - 1);
				String rank = cardCode.substring(0, cardCode.length() - 1);

				BullbullCard card = new BullbullCard(Suit.of(suit), Rank.of(rank));
				bullbullGame.setCard(stage, position, card);
				return true;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
