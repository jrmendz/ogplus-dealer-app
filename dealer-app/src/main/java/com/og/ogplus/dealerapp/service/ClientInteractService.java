package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;

public interface ClientInteractService {

    String CANCEL = "cancel";

    String getDealerCode() throws ClientInteractInterruptedException;

    String getDealerCode(boolean allowSkip) throws ClientInteractInterruptedException;

    String getPitBossCode() throws ClientInteractInterruptedException;

    Stage showStage(GameCategory gameCategory) throws ClientInteractInterruptedException;

    Stage getStage(GameCategory gameCategory) throws ClientInteractInterruptedException;

    void showMessage(String message);

    void showWarningMessage(String message);

    void showErrorMessage(String message);

}
