package com.og.ogplus.dealerapp.controller;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.enums.ThreeCardsPosition;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.common.message.MessageAction;
import com.og.ogplus.common.message.threecards.ThreeCardsMessage;
import com.og.ogplus.common.model.Card;
import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Stage;
import com.og.ogplus.common.model.Suit;
import com.og.ogplus.dealerapp.game.ThreeCardsGame;
import com.og.ogplus.dealerapp.game.ThreeCardsGame.ThreeCardsResult;
import com.og.ogplus.dealerapp.game.model.GameResult;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import com.og.ogplus.dealerapp.view.GameImages;
import com.og.ogplus.dealerapp.view.threecards.ThreeCardsView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnBean(ThreeCardsGame.class)
@Component
public class ThreeCardsController extends CardGameController implements ThreeCardsGame.Listener {
    private ThreeCardsView layout;

    private Card[] phoenixCards = new Card[3];
    private Card[] dragonCards = new Card[3];

    private Set<ThreeCardsPosition> waitScannedSet = new ConcurrentSkipListSet<>();

   
    @Override
    public void onRoundStart(Stage stage) {
        super.onRoundStart(stage);
        IntStream.range(0, phoenixCards.length).forEach(i -> phoenixCards[i] = dragonCards[i] = null);
        waitScannedSet.clear();
    }

    @Override
    protected Message generateProcessMessage(GameResult gameResult) {
        ThreeCardsGame.ThreeCardsGameResult result = (ThreeCardsGame.ThreeCardsGameResult) gameResult;
        ThreeCardsMessage processMessage = ThreeCardsMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(null)
                .cards(new ThreeCardsMessage.Cards(result.getPhoenixCards(), result.getDragonCards()))
                .build();
        processMessage.setAction(MessageAction.PROCESS);

        return processMessage;
    }

    @Override
    protected Message generateBroadcastMessage(GameResult gameResult) {
        ThreeCardsGame.ThreeCardsGameResult result = (ThreeCardsGame.ThreeCardsGameResult) gameResult;

        return ThreeCardsMessage.builder()
                .shoeDate(gameResult.getStage().getDate())
                .result(result.getResult().stream().map(r -> r.name().toLowerCase()).collect(Collectors.joining(",")))
                .cards(new ThreeCardsMessage.Cards(result.getPhoenixCards(), result.getDragonCards()))
                .build();
    }

    @Override
    public void onStartScanning(Position position) {
        synchronized (this) {
            waitScannedSet.add((ThreeCardsPosition) position);
            layout.setStatus("Please Scan Card: " + waitScannedSet.stream().map(Position::getReadableFormat).collect(Collectors.joining(", ")));
        }
        switch ((ThreeCardsPosition) position) {
            case PHOENIX1:
                if (phoenixCards[0] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case PHOENIX2:
                if (phoenixCards[1] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case PHOENIX3:
                if (phoenixCards[2] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case DRAGON1:
                if (dragonCards[0] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case DRAGON2:
                if (dragonCards[1] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
            case DRAGON3:
                if (dragonCards[2] == null) {
                    layout.setCard((ThreeCardsPosition) position, GameImages.CARD_SLOT_ACTIVE);
                }
                break;
        }
        layout.setBlinkCard((ThreeCardsPosition) position);
    }

    @Override
    public void onCardScanned(Position position, Card card, boolean isCardShown) {
        ThreeCardsPosition ThreeCardsPosition = (ThreeCardsPosition) position;
        waitScannedSet.remove(ThreeCardsPosition);

        switch (ThreeCardsPosition) {
            case PHOENIX1:
                phoenixCards[0] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
            case PHOENIX2:
                phoenixCards[1] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
            case PHOENIX3:
                phoenixCards[2] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
            case DRAGON1:
                dragonCards[0] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
            case DRAGON2:
                dragonCards[1] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
            case DRAGON3:
                dragonCards[2] = card;
                layout.clearBlinkCard(ThreeCardsPosition);
                break;
        }
        if (isCardShown) {
            showCards(position);
        } else {
            layout.setCard(ThreeCardsPosition, GameImages.CARD_SLOT_DOWN);
        }
    }

    @Override
    public void showCards(Position... positions) {
        Arrays.stream(positions)
                .map(position -> (ThreeCardsPosition) position)
                .forEach(position -> {
                    Card card = null;
                    switch (position) {
                        case PHOENIX1:
                            card = phoenixCards[0];
                            break;
                        case PHOENIX2:
                            card = phoenixCards[1];
                            break;
                        case PHOENIX3:
                            card = phoenixCards[2];
                            break;
                        case DRAGON1:
                            card = dragonCards[0];
                            break;
                        case DRAGON2:
                            card = dragonCards[1];
                            break;
                        case DRAGON3:
                            card = dragonCards[2];
                            break;
                    }
                    if (card == null) {
                        layout.setCard(position, GameImages.CARD_SLOT);
                    } else {
                        layout.setCard(position, GameImages.GAME_CARDS.get(card.getRank().getSymbol() + card.getSuit().getSymbol()));
                    }
                });

        if (Arrays.stream(dragonCards).allMatch(Objects::nonNull)) {
            layout.setDragonPoint(getDCardsDisResult(dragonCards));
        }

        if (Arrays.stream(phoenixCards).allMatch(Objects::nonNull)) {
            layout.setPhoenixPoint(getDCardsDisResult(phoenixCards));
        }
    }


    private String getDCardsDisResult(Card[] cards) {
        ThreeCardsResult cardsResult = calcThreeCardResult(cards);
        String cardsDisResult = cardsResult.name();
        if (cardsResult == ThreeCardsResult.HIGH_CARD) {
            Rank maxValueRank = Arrays.stream(cards).map(Card::getRank).max((card1Rank, card2Rank) -> {
                int card1RankValue = card1Rank == Rank.ACE ? Rank.KING.getValue() + 1 : card1Rank.getValue();
                int card2RankValue = card2Rank == Rank.ACE ? Rank.KING.getValue() + 1 : card2Rank.getValue();
                return card1RankValue - card2RankValue;
            }).get();
            cardsDisResult += "(" + maxValueRank.getSymbol() + ")";
        }

        return cardsDisResult;
    }

    private static ThreeCardsResult calcThreeCardResult(Card[] cards) {
        Set<Suit> suitSet = Arrays.stream(cards).map(Card::getSuit).collect(Collectors.toSet());
        Set<Rank> rankSet = Arrays.stream(cards).map(Card::getRank).collect(Collectors.toSet());

        if (suitSet.size() == 3 && (rankSet.contains(Rank.TWO) && rankSet.contains(Rank.THREE) && rankSet.contains(Rank.FIVE))) {
            return ThreeCardsResult.LEOPARD_KILLER;
        }

        if (rankSet.size() == 1) {
            return ThreeCardsResult.LEOPARD;
        }

        if (suitSet.size() == 1) {
            if (ThreeCardsGame.isStraight(cards)) {
                return ThreeCardsResult.STRAIGHT_FLUSH;
            } else {
                return ThreeCardsResult.FLUSH;
            }
        }

        if (ThreeCardsGame.isStraight(cards)) {
            return ThreeCardsResult.STRAIGHT;
        }

        if (rankSet.size() == 2) {
            return ThreeCardsResult.PAIR;
        }

        return ThreeCardsResult.HIGH_CARD;
    }

    @Override
    public void onUpdateGameInfo() {
        sendGameMessageOut(
                ThreeCardsMessage.builder()
                        .shoeDate(currentStage.getDate())
                        .cards(new ThreeCardsMessage.Cards(phoenixCards, dragonCards))
                        .build());
    }

    @Override
    public void onTempGameResult(List<ThreeCardsGame.ThreeCardsWinner> results) {
        if (results.contains(ThreeCardsGame.ThreeCardsWinner.PHOENIX)) {
            getLayout().showResult("PHOENIX WIN", 72, Color.RED, Color.WHITE);
        } else if (results.contains(ThreeCardsGame.ThreeCardsWinner.DRAGON)) {
            getLayout().showResult("DRAGON WIN", 72, Color.YELLOW, Color.WHITE);
        } else {
            getLayout().showResult("TIE", 84, Color.GREEN, Color.BLACK);
        }
    }

    @Autowired
    public void setLayout(ThreeCardsView layout) {
        this.layout = layout;
    }

    @Autowired(required = false)
    @Qualifier("gameInfoPublisher")
    @Override
    public void setGameServerClientService(GameServerClientService gameServerClientService) {
        super.setGameServerClientService(gameServerClientService);
    }
}
