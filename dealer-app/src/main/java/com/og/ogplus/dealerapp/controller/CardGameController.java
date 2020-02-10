package com.og.ogplus.dealerapp.controller;

import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.message.UpdateStatusMessage;
import com.og.ogplus.dealerapp.game.CardGame;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public abstract class CardGameController extends AbstractGameController implements CardGame.Listener {
    @Override
    public void onStartSqueeze(LocalDateTime squeezeEndTime, Position... positions) {
        getLayout().setStatus("SQUEEZE START");
        getLayout().startCountDown(squeezeEndTime);

        sendGameMessageOut(new UpdateStatusMessage("SQUEEZE_START"));
        sendGameMessageOut(new UpdateStatusMessage("SQUEEZE_TIME"));
        getTaskScheduler().execute(new SendCountDownTask(squeezeEndTime));
    }

    @Override
    public void onEndSqueeze(Position... positions) {
        getLayout().setStatus("SQUEEZE END");

        sendGameMessageOut(new UpdateStatusMessage("SQUEEZE_END"));
    }
}
