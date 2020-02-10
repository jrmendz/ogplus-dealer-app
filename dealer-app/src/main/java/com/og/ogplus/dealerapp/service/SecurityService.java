package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.db.entity.PitBoss;
import com.og.ogplus.dealerapp.exception.AuthenticateException;

public interface SecurityService {

    Dealer authDealer(String dealerCode) throws AuthenticateException;

    PitBoss authPitBoss(String pitBossCode) throws AuthenticateException;

}
