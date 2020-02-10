package com.og.ogplus.dealerapp.controller.api.handler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import com.og.ogplus.dealerapp.game.Game;
import com.og.ogplus.dealerapp.game.VipBaccaratGame;

//@ConditionalOnBean(VipBaccaratGame.class)
@ConditionalOnExpression("'${app.game-category}'.equals('BACCARAT') && '${app.isvip}'.equals('true')")
@Component
public class VipBaccaratRequestHandler extends BaccaratRequestHandler{

	private VipBaccaratGame vipBaccaratGame;
	
	public VipBaccaratRequestHandler(Game game) {
		super(game);
		this.vipBaccaratGame = (VipBaccaratGame) game;
	}

	public void turnOnTheSqueeze() {
		vipBaccaratGame.turnOnTheSqueeze();
	}
	
	public void start() {
		vipBaccaratGame.start();
	}
	
	public void skipRound() {
		vipBaccaratGame.skipRound();
	}
	
	public boolean shuffle() {
		return vipBaccaratGame.shuffle();
	}
	
	public boolean changeDeck() {
		return vipBaccaratGame.changeDeck();
	}
	
	public void changeDealer() {
		vipBaccaratGame.changeDealer();
	}
}
