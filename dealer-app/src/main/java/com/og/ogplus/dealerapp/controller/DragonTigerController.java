package com.og.ogplus.dealerapp.controller;

import java.awt.Color;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.DragonTigerPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.MessageAction;
import com.og.ogplus.common.message.dragontiger.BroadcastMessage;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.DragonTigerGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.view.DealerAppView;
import com.og.ogplus.dealerapp.view.GameImages;
import com.og.ogplus.dealerapp.view.dt.DragonTigerView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(DragonTigerGame.class)
@Component
public class DragonTigerController extends ShuffleGameController implements DragonTigerGame.Listener {
    private DragonTigerView layout;
    private Card dragonCard;
    private Card tigerCard;

    private Set<DragonTigerPosition> waitScannedSet = new ConcurrentSkipListSet<>();

  
    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        dragonCard = tigerCard = null;
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        DragonTigerGame.DragonTigerGameResult result = (DragonTigerGame.DragonTigerGameResult) gameResult;

        BroadcastMessage processMessage = BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(null)
                .cards(new BroadcastMessage.Cards(result.getDragonCard(), result.getTigerCard()))
                .dtType(((DragonTigerGame) getGame()).getType())
                .build();
        processMessage.setAction(MessageAction.PROCESS);

        return processMessage;
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        DragonTigerGame.DragonTigerGameResult result = (DragonTigerGame.DragonTigerGameResult) gameResult;

        return BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(result.getResult().stream().map(r -> r.name().toLowerCase()).collect(Collectors.joining(",")))
                .cards(new BroadcastMessage.Cards(result.getDragonCard(), result.getTigerCard()))
                .dtType(((DragonTigerGame) getGame()).getType())
                .build();
    }

    @Override
    public void onStartScanning(Position position) {
        waitScannedSet.add((DragonTigerPosition) position);
        layout.setStatus("Please Scan Card: " + waitScannedSet.stream().map(Position::getReadableFormat).collect(Collectors.joining(", ")));
        switch ((DragonTigerPosition) position) {
            case DRAGON:
                if (dragonCard == null) {// pit boss没有设置值
                    layout.setCard((DragonTigerPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case TIGER:
                if (tigerCard == null) {
                    layout.setCard((DragonTigerPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
        }
        layout.setBlinkCard((DragonTigerPosition) position);// 扫描开始时设置卡片闪动
    }

    @Override
    public void onCardScanned(Position position, Card card, boolean isCardShown) {
        DragonTigerPosition dragonTigerPosition = (DragonTigerPosition) position;
        waitScannedSet.remove(dragonTigerPosition);

        switch (dragonTigerPosition) {
            case DRAGON:
                dragonCard = card;
                layout.clearBlinkCard(dragonTigerPosition);
                break;
            case TIGER:
                tigerCard = card;
                layout.clearBlinkCard(dragonTigerPosition);
                break;
        }

        if (isCardShown) {
            layout.setCard(dragonTigerPosition, GameImages.GAME_CARDS.get(card.getRank().getSymbol() + card.getSuit().getSymbol()));
        } else {
            layout.setCard(dragonTigerPosition, GameImages.CARD_SLOT_DOWN);
        }
    }

    @Override
    public void showCards(Position... positions) {
        Arrays.stream(positions)
                .map(position -> (DragonTigerPosition) position)
                .forEach(position -> {
                    Card card = null;
                    switch (position) {
                        case DRAGON:
                            card = dragonCard;
                            break;
                        case TIGER:
                            card = tigerCard;
                            break;
                    }
                    layout.setCard(position, GameImages.GAME_CARDS.get(card.getRank().getSymbol() + card.getSuit().getSymbol()));
                });
    }

    @Override
    public void onUpdateGameInfo() {
        sendGameMessageOut(
                BroadcastMessage.builder()
                        .shoeDate(currentStage.getDate())
                        .cards(new BroadcastMessage.Cards(dragonCard, tigerCard))
                        .dtType(((DragonTigerGame) getGame()).getType())
                        .build());
    }

    @Autowired
    public void setLayout(DragonTigerView layout) {
        this.layout = layout;
    }

    @Override
    public void onTempGameResult(DragonTigerGame.Result results) {
        if (results == DragonTigerGame.Result.DRAGON) {
            getLayout().showResult("DRAGON WIN", 84, Color.BLUE, Color.WHITE);
        } else if (results == DragonTigerGame.Result.TIGER) {
            getLayout().showResult("TIGER WIN", 84, Color.RED, Color.WHITE);
        } else {
            getLayout().showResult("TIE", 84, Color.GREEN, Color.BLACK);
        }
    }

}
