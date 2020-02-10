package com.og.ogplus.dealerapp.controller;


import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.BullbullPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.MessageAction;
import com.og.ogplus.common.message.bullbull.BroadcastMessage;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.dealerapp.game.BullbullGame;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import com.og.ogplus.dealerapp.view.GameImages;
import com.og.ogplus.dealerapp.view.bullbulll.BullbullView;

@ConditionalOnBean(BullbullGame.class)
@Component
public class BullbullController extends CardGameController implements BullbullGame.Listener {
    private BullbullView layout;

    private Card[] firstPlayerCards = new Card[5];
    private Card[] secondPlayerCards = new Card[5];
    private Card[] thirdPlayerCards = new Card[5];
    private Card[] bankerCards = new Card[5];

    private Card firstCard;

    private Set<BullbullPosition> waitScannedSet = new ConcurrentSkipListSet<>();



    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        IntStream.range(0, firstPlayerCards.length)
                .forEach(i -> firstPlayerCards[i] = secondPlayerCards[i] = thirdPlayerCards[i] = bankerCards[i] = firstCard = null);
        waitScannedSet.clear();
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        BullbullGame.BullbullGameResult result = (BullbullGame.BullbullGameResult) gameResult;

        BroadcastMessage processMessage = BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(null)
                .cards(new BroadcastMessage.Cards(result.getFirstPlayerCards(), result.getSecondPlayerCards(),
                        result.getThirdPlayerCards(), result.getBankerCards(), firstCard))
                .build();
        processMessage.setAction(MessageAction.PROCESS);

        return processMessage;
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        BullbullGame.BullbullGameResult result = (BullbullGame.BullbullGameResult) gameResult;

        return BroadcastMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(Arrays.stream(result.getResults()).map(r -> r.name().toLowerCase()).collect(Collectors.joining(",")))
                .cards(new BroadcastMessage.Cards(result.getFirstPlayerCards(), result.getSecondPlayerCards(),
                        result.getThirdPlayerCards(), result.getBankerCards(), firstCard))
                .build();
    }

    @Override
    public void onStartScanning(Position position) {
        synchronized (this) {
            waitScannedSet.add((BullbullPosition) position);
            layout.setStatus("Please Scan Card: " + waitScannedSet.stream().map(Position::getReadableFormat).collect(Collectors.joining(", ")));
        }
        switch ((BullbullPosition) position) {
            case FIRSTCARD:
                if (firstCard == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;

            case FIRSTPLAYER1ST:
                if (firstPlayerCards[0] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case FIRSTPLAYER2ST:
                if (firstPlayerCards[1] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case FIRSTPLAYER3ST:
                if (firstPlayerCards[2] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case FIRSTPLAYER4ST:
                if (firstPlayerCards[3] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case FIRSTPLAYER5ST:
                if (firstPlayerCards[4] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;


            case SECONDPLAYER1ST:
                if (secondPlayerCards[0] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case SECONDPLAYER2ST:
                if (secondPlayerCards[1] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case SECONDPLAYER3ST:
                if (secondPlayerCards[2] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case SECONDPLAYER4ST:
                if (secondPlayerCards[3] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case SECONDPLAYER5ST:
                if (secondPlayerCards[4] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;


            case THIRDPLAYER1ST:
                if (thirdPlayerCards[0] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case THIRDPLAYER2ST:
                if (thirdPlayerCards[1] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case THIRDPLAYER3ST:
                if (thirdPlayerCards[2] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case THIRDPLAYER4ST:
                if (thirdPlayerCards[3] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case THIRDPLAYER5ST:
                if (thirdPlayerCards[4] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;


            case BANKER1ST:
                if (bankerCards[0] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER2ST:
                if (bankerCards[1] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER3ST:
                if (bankerCards[2] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER4ST:
                if (bankerCards[3] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case BANKER5ST:
                if (bankerCards[4] == null) {
                    layout.setCard((BullbullPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
        }
        layout.setBlinkCard((BullbullPosition) position);// 扫�??开始时设置�?�片闪动
    }

    @Override
    public void onCardScanned(Position position, Card card, boolean isCardShown) {
        BullbullPosition bullbullPosition = (BullbullPosition) position;
        waitScannedSet.remove(bullbullPosition);

        switch (bullbullPosition) {
            case FIRSTCARD:
                firstCard = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case FIRSTPLAYER3ST:
                firstPlayerCards[2] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case FIRSTPLAYER2ST:
                firstPlayerCards[1] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case FIRSTPLAYER1ST:
                firstPlayerCards[0] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case FIRSTPLAYER5ST:
                firstPlayerCards[4] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case FIRSTPLAYER4ST:
                firstPlayerCards[3] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;


            case SECONDPLAYER3ST:
                secondPlayerCards[2] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case SECONDPLAYER2ST:
                secondPlayerCards[1] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case SECONDPLAYER1ST:
                secondPlayerCards[0] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case SECONDPLAYER5ST:
                secondPlayerCards[4] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case SECONDPLAYER4ST:
                secondPlayerCards[3] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;


            case THIRDPLAYER3ST:
                thirdPlayerCards[2] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case THIRDPLAYER2ST:
                thirdPlayerCards[1] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case THIRDPLAYER1ST:
                thirdPlayerCards[0] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case THIRDPLAYER5ST:
                thirdPlayerCards[4] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case THIRDPLAYER4ST:
                thirdPlayerCards[3] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;


            case BANKER5ST:
                bankerCards[4] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case BANKER4ST:
                bankerCards[3] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case BANKER3ST:
                bankerCards[2] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case BANKER2ST:
                bankerCards[1] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
            case BANKER1ST:
                bankerCards[0] = card;
                layout.clearBlinkCard(bullbullPosition);
                break;
        }

        if (isCardShown) {
            showCards(position);
        } else {
            layout.setCard(bullbullPosition, GameImages.CARD_SLOT_DOWN);
        }
    }

    @Override
    public void showCards(Position... positions) {
        Arrays.stream(positions)
                .map(position -> (BullbullPosition) position)
                .forEach(position -> {
                    Card card = null;
                    switch (position) {
                        case FIRSTCARD:
                            card = firstCard;
                            break;
                        case FIRSTPLAYER1ST:
                            card = firstPlayerCards[0];
                            break;
                        case FIRSTPLAYER2ST:
                            card = firstPlayerCards[1];
                            break;
                        case FIRSTPLAYER3ST:
                            card = firstPlayerCards[2];
                            break;
                        case FIRSTPLAYER4ST:
                            card = firstPlayerCards[3];
                            break;
                        case FIRSTPLAYER5ST:
                            card = firstPlayerCards[4];
                            break;


                        case SECONDPLAYER1ST:
                            card = secondPlayerCards[0];
                            break;
                        case SECONDPLAYER2ST:
                            card = secondPlayerCards[1];
                            break;
                        case SECONDPLAYER3ST:
                            card = secondPlayerCards[2];
                            break;
                        case SECONDPLAYER4ST:
                            card = secondPlayerCards[3];
                            break;
                        case SECONDPLAYER5ST:
                            card = secondPlayerCards[4];
                            break;


                        case THIRDPLAYER1ST:
                            card = thirdPlayerCards[0];
                            break;
                        case THIRDPLAYER2ST:
                            card = thirdPlayerCards[1];
                            break;
                        case THIRDPLAYER3ST:
                            card = thirdPlayerCards[2];
                            break;
                        case THIRDPLAYER4ST:
                            card = thirdPlayerCards[3];
                            break;
                        case THIRDPLAYER5ST:
                            card = thirdPlayerCards[4];
                            break;


                        case BANKER1ST:
                            card = bankerCards[0];
                            break;
                        case BANKER2ST:
                            card = bankerCards[1];
                            break;
                        case BANKER3ST:
                            card = bankerCards[2];
                            break;
                        case BANKER4ST:
                            card = bankerCards[3];
                            break;
                        case BANKER5ST:
                            card = bankerCards[4];
                            break;
                    }
                    if (card == null) {
                        layout.setCard(position, GameImages.CARD_SLOT);
                    } else {
                        layout.setCard(position, GameImages.NIUNIU_GAME_CARDS.get(card.getRank().getSymbol() + card.getSuit().getSymbol()));

                        //TODO have to optimize
                        if (Arrays.stream(bankerCards).allMatch(Objects::nonNull)) {
                            layout.setBankerCardsType(BullbullGame.calculateCardsType(bankerCards));
                        }
                        if (Arrays.stream(firstPlayerCards).allMatch(Objects::nonNull)) {
                            layout.setPlayer1CardsType(BullbullGame.calculateCardsType(firstPlayerCards));
                        }
                        if (Arrays.stream(secondPlayerCards).allMatch(Objects::nonNull)) {
                            layout.setPlayer2CardsType(BullbullGame.calculateCardsType(secondPlayerCards));
                        }
                        if (Arrays.stream(thirdPlayerCards).allMatch(Objects::nonNull)) {
                            layout.setPlayer3CardsType(BullbullGame.calculateCardsType(thirdPlayerCards));
                        }
                    }

                });
    }

    @Override
    public void onUpdateGameInfo() {
        sendGameMessageOut(
                BroadcastMessage.builder()
                        .shoeDate(currentStage.getDate())
                        .cards(new BroadcastMessage.Cards(firstPlayerCards, secondPlayerCards, thirdPlayerCards, bankerCards, firstCard))
                        .build());
    }

    @Override
    public void onTempGameResult(BullbullGame.Result[] results) {
        StringBuilder sb = new StringBuilder();

        for (BullbullGame.Result r : results) {
            if (r != BullbullGame.Result.BANKER) {
                sb.append(r).append(" WIN<br>");
            }
        }

        String resultText = StringUtils.stripEnd(sb.toString(), "<br>");
        if (StringUtils.isBlank(resultText)) {
            getLayout().showResult("BANKER WIN", 72, Color.RED, Color.WHITE);
        } else {
            getLayout().showResult("<html>"+resultText+"</html>", 72, Color.BLUE, Color.WHITE);
        }
    }

    @Autowired
    public void setLayout(BullbullView layout) {
        this.layout = layout;
    }

    @Autowired(required = false)
    @Qualifier("gameInfoPublisher")
    @Override
    public void setGameServerClientService(GameServerClientService gameServerClientService) {
        super.setGameServerClientService(gameServerClientService);
    }
}
