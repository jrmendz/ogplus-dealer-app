package com.og.ogplus.dealerapp.game;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.og.ogplus.common.db.entity.Table;

//@ConditionalOnProperty(name = "app.game-category", havingValue = "VIPBACCARAT")
@ConditionalOnExpression("'${app.game-category}'.equals('BACCARAT') && '${app.isvip}'.equals('true')")
@Component
public class VipBaccaratGame extends BaccaratGame {
    private final AtomicBoolean vipBaccaratGameStart = new AtomicBoolean(false);
    private final AtomicBoolean vipBaccaratGameSkip = new AtomicBoolean(false);
    private final AtomicBoolean vipBaccaratGameSqueeze = new AtomicBoolean(false);
    
    private final AtomicBoolean vipShuffleOrChangeDeskStart = new AtomicBoolean(false);

	public VipBaccaratGame(Table table) {
		super(table);
	}

	@Override
	protected void reset() {
		super.reset();
		vipBaccaratGameStart.set(false);
		vipBaccaratGameSkip.set(false);
		vipShuffleOrChangeDeskStart.set(false);
	}

	@Override
	public boolean isSqueezeMode() {
		return vipBaccaratGameSqueeze.get() && !vipBaccaratGameSkip.get();
	}
	@Override
	protected boolean isWaitBetting(){
		return !vipBaccaratGameStart.get() || vipBaccaratGameSkip.get();
	}

	public void turnOnTheSqueeze() {
		vipBaccaratGameSqueeze.set(!vipBaccaratGameSqueeze.get());
	}

	public void start() {
		vipBaccaratGameStart.set(true);
	}

	public void skipRound() {
		vipBaccaratGameSkip.set(true);
	}
	
	public boolean shuffle() {
		if(this.getStage().getRound() <= 30 || this.vipShuffleOrChangeDeskStart.get())
			return false;
		this.vipShuffleOrChangeDeskStart.set(true);
		this.enableShuffle();
		return true;
	}
	
	public boolean changeDeck() {
		if(this.getStage().getRound() <= 30 || this.vipShuffleOrChangeDeskStart.get())
			return false;
		this.vipShuffleOrChangeDeskStart.set(true);
		this.enableShuffle();
		return true;
	}
	
	public void changeDealer() {
		
	}
}
