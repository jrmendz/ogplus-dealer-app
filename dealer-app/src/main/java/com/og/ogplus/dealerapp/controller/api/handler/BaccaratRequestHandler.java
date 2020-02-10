package com.og.ogplus.dealerapp.controller.api.handler;

import com.og.ogplus.common.enums.BaccaratPosition;
import com.og.ogplus.common.model.BaccaratCard;
import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.common.model.Suit;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.BaccaratGame;
import com.og.ogplus.dealerapp.game.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
//@ConditionalOnBean(BaccaratGame.class)
@ConditionalOnExpression("'${app.game-category}'.equals('BACCARAT') && '${app.isvip}'.equals('false')")
@Component
public class BaccaratRequestHandler extends GameRequestHandler {
	private static final String KEY_POSITION = "position";
	private static final String KEY_CARD = "card";

	private BaccaratGame baccaratGame;

	public BaccaratRequestHandler(Game game) {
		super(game);
		this.baccaratGame = (BaccaratGame) game;
	}

	@Override
	public boolean handleChangeResultRequest(Stage stage, Object result) throws FailedChangeGameResultException {
		if (result instanceof Map) {
			Map resultMap = ((Map) result);
			if (!resultMap.containsKey(KEY_POSITION) || !resultMap.containsKey(KEY_CARD)) {
				throw new IllegalArgumentException();
			} else if ((String) resultMap.get(KEY_CARD) == null) {
				BaccaratPosition position = BaccaratPosition.valueOf((String) resultMap.get(KEY_POSITION));
				baccaratGame.setCard(stage, position, null);
				return true;
			} else {
				BaccaratPosition position = BaccaratPosition.valueOf((String) resultMap.get(KEY_POSITION));
				String cardCode = (String) resultMap.get(KEY_CARD);
				String suit = cardCode.substring(cardCode.length() - 1);
				String rank = cardCode.substring(0, cardCode.length() - 1);

				BaccaratCard card = new BaccaratCard(Suit.of(suit), Rank.of(rank));
				baccaratGame.setCard(stage, position, card);
				return true;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
