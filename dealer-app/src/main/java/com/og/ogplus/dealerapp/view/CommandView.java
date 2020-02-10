package com.og.ogplus.dealerapp.view;

import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.BaccaratGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommandView implements BaccaratGame.Listener {

    @Override
    public void onRoundStart(Stage stage) {
        log.info("Round({}) Start...", stage.getSimpleFormat());
    }

    @Override
    public void onStartBetting(LocalDateTime bettingEndTime) {
        log.info("Betting Start ({}s).", Duration.between(LocalDateTime.now(), bettingEndTime).getSeconds());
    }

    @Override
    public void onEndBetting() {
        log.info("Betting Stop");
    }

    @Override
    public void onStartScanning(Position position) {
        log.info("Scanning Card ({})...", position);
    }

    @Override
    public void onCardScanned(Position position, Card card, boolean isCardShown) {
        log.info("Scanned Card: {} - {}", position, card);
    }

    @Override
    public void onRoundEnd() {
        log.info("Round End");
    }

    @Override
    public void onGameResult(GameResult gameResult) {
        log.info("Result: {}", gameResult);
    }
}
