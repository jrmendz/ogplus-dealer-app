package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.db.dao.DealerDao;
import com.og.ogplus.common.db.dao.PitBossDao;
import com.og.ogplus.common.db.entity.Dealer;
import com.og.ogplus.common.db.entity.PitBoss;
import com.og.ogplus.dealerapp.exception.AuthenticateException;
import com.og.ogplus.dealerapp.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

    private DealerDao dealerDao;

    private PitBossDao pitBossDao;

    public SecurityServiceImpl(DealerDao dealerDao, PitBossDao pitBossDao) {
        this.dealerDao = dealerDao;
        this.pitBossDao = pitBossDao;
    }

    @Override
    public Dealer authDealer(String dealerCode) throws AuthenticateException {
        try {
            Optional<Dealer> dealerOptional = dealerDao.findByCode(Integer.parseInt(dealerCode.trim()));
            if (dealerOptional.isPresent()) {
                return dealerOptional.get();
            } else {
                throw new AuthenticateException("Invalid RID.", dealerCode);
            }
        } catch (NumberFormatException e) {
            throw new AuthenticateException("Dealer's RID format error.", e, dealerCode);
        }
    }

    @Override
    public PitBoss authPitBoss(String pitBossCode) throws AuthenticateException {
        try {
            Optional<PitBoss> pitBossOptional = pitBossDao.findByCode(Integer.parseInt(pitBossCode.trim()));
            if (pitBossOptional.isPresent()) {
                return pitBossOptional.get();
            } else {
                throw new AuthenticateException("Invalid Pass Code.", pitBossCode);
            }
        } catch (NumberFormatException e) {
            throw new AuthenticateException("Pass Code format error.", e, pitBossCode);
        }
    }
}
