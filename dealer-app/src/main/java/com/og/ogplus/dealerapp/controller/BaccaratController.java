package com.og.ogplus.dealerapp.controller;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.BaccaratPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.MessageAction;
import com.og.ogplus.common.message.UpdateStatusMessage;
import com.og.ogplus.common.message.baccarat.BroadcastMessage;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.BaccaratGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.GameImages;
import com.og.ogplus.dealerapp.view.baccarat.BaccaratView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(BaccaratGame.class)
@Component
public class BaccaratController extends ShuffleGameController implements BaccaratGame.Listener {
    private BaccaratView layout;

    private Card[] playerCards = new Card[3];
    private Card[] bankerCards = new Card[3];

    private Set<BaccaratPosition> waitScannedSet = new ConcurrentSkipListSet<>();

    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        IntStream.range(0, playerCards.length).forEach(i -> playerCards[i] = bankerCards[i] = null);
        waitScannedSet.clear();
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        BaccaratGame.BaccaratGameResult result = (BaccaratGame.BaccaratGameResult) gameResult;

        BroadcastMessage processMessage = BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(null)
                .cards(new BroadcastMessage.Cards(result.getPlayerCards(), result.getBankerCards()))
                .build();
        processMessage.setAction(MessageAction.PROCESS);

        return processMessage;
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        BaccaratGame.BaccaratGameResult result = (BaccaratGame.BaccaratGameResult) gameResult;

        return BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(result.getResult().stream().map(r -> r.name().toLowerCase()).collect(Collectors.joining(",")))
                .cards(new BroadcastMessage.Cards(result.getPlayerCards(), result.getBankerCards()))
                .build();
    }

    @Override
    public void onStartScanning(Position position) {
        synchronized (this) {
            waitScannedSet.add((BaccaratPosition) position);
            layout.setStatus("Please Scan Card: " + waitScannedSet.stream().map(Position::getReadableFormat).collect(Collectors.joining(", ")));
        }
        switch ((BaccaratPosition) position) {
            case PLAYER1:
                if (playerCards[0] == null) {// pit boss没有设置值
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case PLAYER2:
                if (playerCards[1] == null) {
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case PLAYER3:
                if (playerCards[2] == null) {
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER1:
                if (bankerCards[0] == null) {
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER2:
                if (bankerCards[1] == null) {
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER3:
                if (bankerCards[2] == null) {
                    layout.setCard((BaccaratPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
        }
        layout.setBlinkCard((BaccaratPosition) position);// 扫描开始时设置卡片闪动
    }

    @Override
    public void onCardScanned(Position position, Card card, boolean isCardShown) {
        BaccaratPosition baccaratPosition = (BaccaratPosition) position;
        waitScannedSet.remove(baccaratPosition);

        switch (baccaratPosition) {
            case PLAYER1:
                playerCards[0] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
            case PLAYER2:
                playerCards[1] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
            case PLAYER3:
                playerCards[2] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
            case BANKER1:
                bankerCards[0] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
            case BANKER2:
                bankerCards[1] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
            case BANKER3:
                bankerCards[2] = card;
                layout.clearBlinkCard(baccaratPosition);
                break;
        }

        if (isCardShown) {
            showCards(position);
        } else {
            layout.setCard(baccaratPosition, GameImages.CARD_SLOT_DOWN);
        }
    }

    @Override
    public void showCards(Position... positions) {
        Arrays.stream(positions)
                .map(position -> (BaccaratPosition) position)
                .forEach(position -> {
                    Card card = null;
                    switch (position) {
                        case PLAYER1:
                            card = playerCards[0];
                            break;
                        case PLAYER2:
                            card = playerCards[1];
                            break;
                        case PLAYER3:
                            card = playerCards[2];
                            break;
                        case BANKER1:
                            card = bankerCards[0];
                            break;
                        case BANKER2:
                            card = bankerCards[1];
                            break;
                        case BANKER3:
                            card = bankerCards[2];
                            break;
                    }
                    if (card == null) {
                        layout.setCard(position, GameImages.CARD_SLOT);
                    } else {
                        layout.setCard(position, GameImages.GAME_CARDS.get(card.getRank().getSymbol() + card.getSuit().getSymbol()));
                    }

                });
        layout.setPlayerTotalPoint(Arrays.stream(playerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10);
        layout.setBankerTotalPoint(Arrays.stream(bankerCards).filter(Objects::nonNull).mapToInt(Card::getValue).sum() % 10);
    }

    @Override
    public void onStartSqueeze(LocalDateTime squeezeEndTime, Position... positions) {
        getLayout().setStatus("SQUEEZE START");
        getLayout().startCountDown(squeezeEndTime);

        String squeezeType = null;
        if (include(positions, BaccaratPosition.PLAYER1) || include(positions, BaccaratPosition.PLAYER2)
                || include(positions, BaccaratPosition.BANKER1) || include(positions, BaccaratPosition.BANKER2)) {
            squeezeType = "SQUEEZE";
        } else if (include(positions, BaccaratPosition.PLAYER3) && include(positions, BaccaratPosition.BANKER3)) {
            squeezeType = "SQUEEZEPB";
        } else if (include(positions, BaccaratPosition.PLAYER3)) {
            squeezeType = "SQUEEZEP";
        } else if (include(positions, BaccaratPosition.BANKER3)) {
            squeezeType = "SQUEEZEB";
        }

        if (StringUtils.isNoneBlank(squeezeType)) {
            sendGameMessageOut(new UpdateStatusMessage(squeezeType + "_START"));
            sendGameMessageOut(new UpdateStatusMessage(squeezeType + "_TIME"));
            getTaskScheduler().execute(new SendCountDownTask(squeezeEndTime));
        }
    }

    @Override
    public void onEndSqueeze(Position... positions) {
        getLayout().setStatus("SQUEEZE END");

        UpdateStatusMessage message = null;
        if (include(positions, BaccaratPosition.PLAYER1)) {
            message = new UpdateStatusMessage("SQUEEZE_END");
        } else if (include(positions, BaccaratPosition.PLAYER3) && include(positions, BaccaratPosition.BANKER3)) {
            message = new UpdateStatusMessage("SQUEEZEPB_END");
        } else if (include(positions, BaccaratPosition.PLAYER3)) {
            message = new UpdateStatusMessage("SQUEEZEP_END");
        } else if (include(positions, BaccaratPosition.BANKER3)) {
            message = new UpdateStatusMessage("SQUEEZEB_END");
        }

        if (message != null) {
            sendGameMessageOut(message);
        }
    }

    private boolean include(Position[] positions, Position key) {
        for (Position position : positions) {
            if (position == key) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onUpdateGameInfo() {
        sendGameMessageOut(
                BroadcastMessage.builder()
                        .shoeDate(currentStage.getDate())
                        .cards(new BroadcastMessage.Cards(playerCards, bankerCards))
                        .build());
    }

    @Autowired
    public void setLayout(BaccaratView layout) {
        this.layout = layout;
    }

    @Override
    public void onTempGameResult(List<BaccaratGame.Result> results) {
        if (results.contains(BaccaratGame.Result.PLAYER)) {
            getLayout().showResult("PLAYER WIN", 72, Color.BLUE, Color.WHITE);
        } else if (results.contains(BaccaratGame.Result.BANKER)) {
            getLayout().showResult("BANKER WIN", 72, Color.RED, Color.WHITE);
        } else {
            getLayout().showResult("TIE", 84, Color.GREEN, Color.BLACK);
        }
    }
}
