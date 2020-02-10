package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.model.GameIdentity;
import com.og.ogplus.common.model.Stage;

public interface StageRecordService {

    void saveStage(GameIdentity gameIdentity, Stage stage) throws Exception;

    Stage loadStage(GameIdentity gameIdentity) throws Exception;

}
