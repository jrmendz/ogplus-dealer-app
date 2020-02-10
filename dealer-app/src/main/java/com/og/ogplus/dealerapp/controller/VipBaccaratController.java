package com.og.ogplus.dealerapp.controller;

import java.time.LocalDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.message.UpdateStatusMessage;
import com.og.ogplus.dealerapp.exception.AuthenticateException;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.game.ShuffleGame;
import com.og.ogplus.dealerapp.game.VipBaccaratGame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(VipBaccaratGame.class)
@Component
public class VipBaccaratController extends BaccaratController{
	
	/*
	 * @Override public void init() { if(checkGui()) { super.init();
	 * getLayout().getDetailsPanel().getChangeDeckButton().addActionListener(e ->
	 * getTaskScheduler().execute(() -> { try { String pitBossCode =
	 * getClientInteractService().getPitBossCode();
	 * getSecurityService().authPitBoss(pitBossCode); ((ShuffleGame)
	 * getGame()).enableShuffle(); } catch (ClientInteractInterruptedException ex) {
	 * log.warn("Client Interact Interrupted"); } catch (AuthenticateException ex) {
	 * getClientInteractService().showErrorMessage(ex.getMessage()); } })); }
	 * 
	 * getLayout().getDetailsPanel().getChangeDealerButton().addActionListener(e ->
	 * getTaskScheduler().execute(() -> { do { try { String dealerCode =
	 * clientInteractService.getDealerCode(); Dealer dealer =
	 * securityService.authDealer(dealerCode); game.setDealer(dealer); return; }
	 * catch (ClientInteractInterruptedException ex) {
	 * log.warn("Client Interact Interrupted"); return; } catch
	 * (AuthenticateException ex) {
	 * clientInteractService.showErrorMessage(ex.getMessage()); } } while (true);
	 * 
	 * })); }
	 */
    
	@Override
	public void onStartBetting(LocalDateTime bettingEndTime) {
		getLayout().setStatus("Wait Betting");
		sendGameMessageOut(new UpdateStatusMessage("BETTING TIME"));

	}
}
