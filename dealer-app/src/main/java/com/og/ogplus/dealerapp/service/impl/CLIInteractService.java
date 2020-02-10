package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.exception.ClientInteractInterruptedException;
import com.og.ogplus.dealerapp.service.ClientInteractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@ConditionalOnProperty(name = "app.gui", havingValue = "false")
@Component
public class CLIInteractService implements ClientInteractService {
    @Override
    public String getDealerCode() {
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }

    @Override
    public String getDealerCode(boolean allowSkip) throws ClientInteractInterruptedException {
        int dealerCode = (int)(Math.random()*900 + 100);
        return Integer.toString(dealerCode);
    }

    @Override
    public String getPitBossCode() throws ClientInteractInterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stage showStage(GameCategory gameCategory) throws ClientInteractInterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stage getStage(GameCategory gameCategory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showMessage(String message) {
        log.info(message);
    }

    @Override
    public void showWarningMessage(String message) {
        log.warn(message);
    }

    @Override
    public void showErrorMessage(String message) {
        log.error(message);
    }

}
