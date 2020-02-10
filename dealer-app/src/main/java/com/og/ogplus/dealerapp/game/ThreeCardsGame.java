package com.og.ogplus.dealerapp.game;

import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.enums.ThreeCardsPosition;
import com.og.ogplus.common.enums.Position;
import com.og.ogplus.common.model.*;
import com.og.ogplus.dealerapp.exception.FailedChangeGameResultException;
import com.og.ogplus.dealerapp.game.model.DefaultDealerAI;
import com.og.ogplus.dealerapp.game.model.GameResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "app.game-category", havingValue = "THREECARDS")
@Component
public class ThreeCardsGame extends CardGame {
	@Getter(AccessLevel.PROTECTED)
	private DefaultDealerAI dealerAI;

	private Card[] phoenixCards = new Card[3];
	private Card[] dragonCards = new Card[3];

	private boolean singleScanner;

	private List<Listener> listeners;

	private AtomicBoolean changeResultActionDetected = new AtomicBoolean(false);

	public ThreeCardsGame(Table table) {
		super(table);
	}

	@Autowired
	public void setDealerAI(DefaultDealerAI dealerAI) {
		this.dealerAI = dealerAI;
	}

	@Override
	public void init() {
		super.init();
		dealerAI.setDesks(1);
		dealerAI.setShuffleCard(false);
		dealerAI.shuffle();
	}

	@Override
	protected void doAfterRoundStart() {
		super.doAfterRoundStart();
		dealerAI.shuffle();
	}

    @Override
    protected boolean isShoeNeeded() {
        return false;
    }

	private static List<ThreeCardsWinner> calculateGameResult(Card[] phoenixCards, Card[] dragonCards) throws UnsatisfiedThreeCardsRuleException {
		if (phoenixCards[0] == null || phoenixCards[1] == null || phoenixCards[2] == null || dragonCards[0] == null
				|| dragonCards[1] == null || dragonCards[2] == null) {
			throw new UnsatisfiedThreeCardsRuleException();
		}
		ThreeCardsResult dragonThreeCardsResult = calcThreeCardResult(dragonCards, phoenixCards);
		ThreeCardsResult phoenixThreeCardsResult = calcThreeCardResult(phoenixCards, dragonCards);
		List<ThreeCardsWinner> results = new ArrayList<>();
		if (dragonThreeCardsResult.getValue() > phoenixThreeCardsResult.getValue()) {
			results.add(ThreeCardsWinner.DRAGON);
		} else if (dragonThreeCardsResult.getValue() == phoenixThreeCardsResult.getValue()) {
			results.add(calcSameThreeCardResult(dragonCards, phoenixCards, dragonThreeCardsResult));
		} else {
			results.add(ThreeCardsWinner.PHOENIX);
		}
		return results;
	}

	private static ThreeCardsWinner calcSameThreeCardResult(Card[] dragonCards, Card[] phoenixCards,
			ThreeCardsResult cardsResult) {
		if (cardsResult == ThreeCardsResult.STRAIGHT_FLUSH || cardsResult == ThreeCardsResult.STRAIGHT) {
			List<Rank> dragonRanks = Arrays.stream(dragonCards).map(card -> {
				return card.getRank();
			}).collect(Collectors.toList());
			List<Rank> phoenixRanks = Arrays.stream(phoenixCards).map(card -> {
				return card.getRank();
			}).collect(Collectors.toList());
			if (dragonRanks.contains(Rank.QUEEN) && dragonRanks.contains(Rank.KING) && dragonRanks.contains(Rank.ACE)
					&& phoenixRanks.contains(Rank.QUEEN) && phoenixRanks.contains(Rank.KING)
					&& phoenixRanks.contains(Rank.ACE)) {
				return ThreeCardsWinner.TIE;
			}
			if (dragonRanks.contains(Rank.QUEEN) && dragonRanks.contains(Rank.KING) && dragonRanks.contains(Rank.ACE)) {
				return ThreeCardsWinner.DRAGON;
			}
			if (phoenixRanks.contains(Rank.QUEEN) && phoenixRanks.contains(Rank.KING)
					&& phoenixRanks.contains(Rank.ACE)) {
				return ThreeCardsWinner.PHOENIX;
			}
			int dragonTotal = Arrays.stream(dragonCards).mapToInt(Card::getValue).sum();
			int phoenixTotal = Arrays.stream(phoenixCards).mapToInt(Card::getValue).sum();
			if (dragonTotal > phoenixTotal) {
				return ThreeCardsWinner.DRAGON;
			} else if (dragonTotal < phoenixTotal) {
				return ThreeCardsWinner.PHOENIX;
			} else {
				return ThreeCardsWinner.TIE;
			}
		}
		if (cardsResult == ThreeCardsResult.LEOPARD) {
			if (Arrays.stream(dragonCards).anyMatch((c) -> c.getRank() == Rank.ACE)) {
				return ThreeCardsWinner.DRAGON;
			}
			if (Arrays.stream(phoenixCards).anyMatch((c) -> c.getRank() == Rank.ACE)) {
				return ThreeCardsWinner.PHOENIX;
			}
			if (Arrays.stream(dragonCards).mapToInt(Card::getValue).sum() > Arrays.stream(phoenixCards)
					.mapToInt(Card::getValue).sum()) {
				return ThreeCardsWinner.DRAGON;
			} else {
				return ThreeCardsWinner.PHOENIX;
			}
		}
		if (cardsResult == ThreeCardsResult.PAIR) {
			List<Rank> dragonSortRanks = Arrays.stream(dragonCards)
					.sorted((c1, c2) -> c2.getRank().getValue() - c1.getRank().getValue()).map(card -> {
						return card.getRank();
					}).collect(Collectors.toList());
			List<Rank> phoenixSortRanks = Arrays.stream(phoenixCards)
					.sorted((c1, c2) -> c2.getRank().getValue() - c1.getRank().getValue()).map(card -> {
						return card.getRank();
					}).collect(Collectors.toList());
			Rank dragonPairRanks = null;
			Rank dragonSingleRanks = null;
			if (dragonSortRanks.get(1) == dragonSortRanks.get(0)) {
				dragonPairRanks = dragonSortRanks.get(0);
				dragonSingleRanks = dragonSortRanks.get(2);
			} else {
				dragonPairRanks = dragonSortRanks.get(2);
				dragonSingleRanks = dragonSortRanks.get(0);
			}
			Rank phoenixPairRanks = null;
			Rank phoenixSingleRanks = null;
			if (phoenixSortRanks.get(1) == phoenixSortRanks.get(0)) {
				phoenixPairRanks = phoenixSortRanks.get(0);
				phoenixSingleRanks = phoenixSortRanks.get(2);
			} else {
				phoenixPairRanks = phoenixSortRanks.get(2);
				phoenixSingleRanks = phoenixSortRanks.get(0);
			}
			int dragonPairTotal = dragonPairRanks == Rank.ACE ? dragonPairRanks.getValue() + 13
					: dragonPairRanks.getValue();
			int dragonSingleTotal = dragonSingleRanks == Rank.ACE ? dragonSingleRanks.getValue() + 13
					: dragonSingleRanks.getValue();

			int phoenixPairTotal = phoenixPairRanks == Rank.ACE ? phoenixPairRanks.getValue() + 13
					: phoenixPairRanks.getValue();
			int phoenixSingleTotal = phoenixSingleRanks == Rank.ACE ? phoenixSingleRanks.getValue() + 13
					: phoenixSingleRanks.getValue();
			if (dragonPairTotal > phoenixPairTotal) {
				return ThreeCardsWinner.DRAGON;
			} else if (dragonPairTotal < phoenixPairTotal) {
				return ThreeCardsWinner.PHOENIX;
			} else {
				if (dragonSingleTotal > phoenixSingleTotal) {
					return ThreeCardsWinner.DRAGON;
				} else if (dragonSingleTotal < phoenixSingleTotal) {
					return ThreeCardsWinner.PHOENIX;
				} else {
					return ThreeCardsWinner.TIE;
				}
			}
		}
		if (cardsResult == ThreeCardsResult.HIGH_CARD || cardsResult == ThreeCardsResult.FLUSH) {
			if (Arrays.stream(dragonCards).anyMatch((c) -> c.getRank() == Rank.ACE)
					&& Arrays.stream(phoenixCards).allMatch((c) -> c.getRank() != Rank.ACE)) {
				return ThreeCardsWinner.DRAGON;
			}
			if (Arrays.stream(dragonCards).allMatch((c) -> c.getRank() != Rank.ACE)
					&& Arrays.stream(phoenixCards).anyMatch((c) -> c.getRank() == Rank.ACE)) {
				return ThreeCardsWinner.PHOENIX;
			}
			List<Rank> dragonSortRanks = Arrays.stream(dragonCards)
					.sorted((c1, c2) -> c2.getRank().getValue() - c1.getRank().getValue()).map(card -> {
						return card.getRank();
					}).collect(Collectors.toList());
			List<Rank> phoenixSortRanks = Arrays.stream(phoenixCards)
					.sorted((c1, c2) -> c2.getRank().getValue() - c1.getRank().getValue()).map(card -> {
						return card.getRank();
					}).collect(Collectors.toList());
			for (int i = 0; i < dragonSortRanks.size(); i++) {
				Rank dragon = dragonSortRanks.get(i);
				Rank phoenix = phoenixSortRanks.get(i);
				if (dragon.getValue() > phoenix.getValue()) {
					return ThreeCardsWinner.DRAGON;
				} else if (dragon.getValue() < phoenix.getValue()) {
					return ThreeCardsWinner.PHOENIX;
				} else {
					continue;
				}
			}
			return ThreeCardsWinner.TIE;
		}
		return ThreeCardsWinner.TIE;
	}

	private static ThreeCardsResult calcThreeCardResult(Card[] cards1, Card[] cards2) {
		Set<Suit> cards1Suits = Arrays.stream(cards1).map(Card::getSuit).collect(Collectors.toSet());
		Set<Rank> card1Ranks = Arrays.stream(cards1).map(Card::getRank).collect(Collectors.toSet());
		Set<Rank> card2Ranks = Arrays.stream(cards2).map(Card::getRank).collect(Collectors.toSet());
		if (cards1Suits.size() == cards1.length && card1Ranks.contains(Rank.TWO) && card1Ranks.contains(Rank.THREE)
				&& card1Ranks.contains(Rank.FIVE) && card2Ranks.size() == 1) {
			return ThreeCardsResult.LEOPARD_KILLER;
		}
		if (card1Ranks.size() == 1) {
			return ThreeCardsResult.LEOPARD;
		}
		if (isStraight(cards1) && cards1Suits.size() == 1) {
			return ThreeCardsResult.STRAIGHT_FLUSH;
		}
		if (cards1Suits.size() == 1) {
			return ThreeCardsResult.FLUSH;
		}
		if (isStraight(cards1)) {
			return ThreeCardsResult.STRAIGHT;
		}
		if (card1Ranks.size() == 2) {
			return ThreeCardsResult.PAIR;
		}
		return ThreeCardsResult.HIGH_CARD;
	}

	public static boolean isStraight(Card[] cards) {
		List<Integer> cardRankValues = Arrays.stream(cards)
				.map(card -> card.getRank().getValue())
				.sorted((v1, v2) -> v2 - v1)
				.collect(Collectors.toList());

		boolean isNormalStraight = cardRankValues.get(0) == cardRankValues.get(1) + 1 && cardRankValues.get(1) == cardRankValues.get(2) + 1;
		boolean isQka = cardRankValues.get(0) == Rank.KING.getValue() && cardRankValues.get(1) == Rank.QUEEN.getValue() && cardRankValues.get(2) == Rank.ACE.getValue();
		return isNormalStraight || isQka;
	}

	@Override
	protected boolean validateScannerSetting() {
		int scannerCount = getScannerCount();
		switch (scannerCount) {
		case 1:
			singleScanner = true;
			return true;
		case 6:
			singleScanner = false;
			return true;
		default:
			log.warn("ThreeCards only support single or 6 scanner devices.");
			showErrorMessage("ThreeCards only support single or 6 scanner devices.");
			return false;
		}
	}

	@Override
	protected void reset() {
		super.reset();
		for (int i = 0; i < 3; ++i) {
			dragonCards[i] = null;
			phoenixCards[i] = null;
		}
	}

	@Override
	protected void deal() throws InterruptedException {
		if (singleScanner) {
			dragonCards[0] = dealCard(ThreeCardsPosition.DRAGON1);
			dragonCards[1] = dealCard(ThreeCardsPosition.DRAGON2);
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();

			phoenixCards[0] = dealCard(ThreeCardsPosition.PHOENIX1);
			phoenixCards[1] = dealCard(ThreeCardsPosition.PHOENIX2);
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();

			dragonCards[2] = dealCard(ThreeCardsPosition.DRAGON3);
			phoenixCards[2] = dealCard(ThreeCardsPosition.PHOENIX3);
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();
		} else {
			Card[] cards = dealCard(ThreeCardsPosition.DRAGON1, ThreeCardsPosition.DRAGON2);
			dragonCards[0] = cards[0];
			dragonCards[1] = cards[1];
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();
			cards = dealCard(ThreeCardsPosition.PHOENIX1, ThreeCardsPosition.PHOENIX2);
			phoenixCards[0] = cards[0];
			phoenixCards[1] = cards[1];
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();
			dragonCards[2] = dealCard(ThreeCardsPosition.DRAGON3);
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();
			phoenixCards[2] = dealCard(ThreeCardsPosition.PHOENIX3);
			Thread.sleep(DATA_PROCESS_PROVE_DELAY);
			updateGameInfo();
		}
	}

	@Override
	protected void doBeforeRoundEnd() throws InterruptedException {
		showTempResult();
		super.doBeforeRoundEnd();
	}

	@Override
	protected GameResult calculateResult() {
		return new ThreeCardsGameResult(getStage().clone(), Arrays.copyOf(phoenixCards, phoenixCards.length),
				Arrays.copyOf(dragonCards, dragonCards.length));
	}

	private void showTempResult() {
		try {
			List<ThreeCardsWinner> results = calculateGameResult(phoenixCards, dragonCards);
			listeners.forEach(listener -> getScheduler().execute(() -> listener.onTempGameResult(results)));
		} catch (UnsatisfiedThreeCardsRuleException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			alert(e.getMessage());
		}
	}

	@Override
	protected List<Position> getPositions() {
		return Arrays.asList(ThreeCardsPosition.values());
	}

	public void setCard(Stage stage, ThreeCardsPosition position, Card card) throws FailedChangeGameResultException {
		if (isAllowChangeGameResult() && getStage().equals(stage)) {
			getChangeLock().lock();
			try {
				if (isAllowChangeGameResult() && getStage().equals(stage)) {
					switch (position) {
					case PHOENIX1:
						phoenixCards[0] = card;
						break;
					case PHOENIX2:
						phoenixCards[1] = card;
						break;
					case PHOENIX3:
						phoenixCards[2] = card;
						break;
					case DRAGON1:
						dragonCards[0] = card;
						break;
					case DRAGON2:
						dragonCards[1] = card;
						break;
					case DRAGON3:
						dragonCards[2] = card;
						break;
					}
					changeResultActionDetected.set(true);
					onCardScanned(position, card, true);
					Thread.sleep(DATA_PROCESS_PROVE_DELAY);
					updateGameInfo();
					showTempResult();
				} else {
					throw new FailedChangeGameResultException(
							String.format("Not allowed to change game result at stage(%s, %s-%s)", stage.getDate(),
									stage.getShoe(), stage.getRound()));
				}
			} catch (InterruptedException e) {
				throw new FailedChangeGameResultException(e);
			} finally {
				getChangeLock().unlock();
			}
		} else {
			throw new FailedChangeGameResultException(
					String.format("Not allowed to change game result at stage(%s, %s-%s)", stage.getDate(),
							stage.getShoe(), stage.getRound()));
		}

	}

	@Override
	public Card getCard(Position position) {
		if (position instanceof ThreeCardsPosition) {
			switch ((ThreeCardsPosition)position) {
				case PHOENIX1:
					return phoenixCards[0];
				case PHOENIX2:
					return phoenixCards[1];
				case PHOENIX3:
					return phoenixCards[2];
				case DRAGON1:
					return dragonCards[0];
				case DRAGON2:
					return dragonCards[1];
				case DRAGON3:
					return dragonCards[2];

			}
		}
		return null;
	}

	@Lazy
	@Autowired
	public void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
	}

	public enum ThreeCardsWinner {
		DRAGON, PHOENIX, TIE;
	}

	public enum ThreeCardsResult {
		LEOPARD_KILLER(7), LEOPARD(6), STRAIGHT_FLUSH(5), FLUSH(4), STRAIGHT(3), PAIR(2), HIGH_CARD(1),;
		@Getter
		private int value;

		ThreeCardsResult(int value) {
			this.value = value;
		}
	}

	public interface Listener extends CardGame.Listener {
		default void onTempGameResult(List<ThreeCardsGame.ThreeCardsWinner> results) {
		}
	}

	@Getter
	public static class ThreeCardsGameResult implements GameResult {
		private static final long serialVersionUID = 1406782741595931290L;

		private Stage stage;

		private Card[] phoenixCards;
		private Card[] dragonCards;

		private List<ThreeCardsWinner> result;

		private ThreeCardsGameResult(Stage stage, Card[] phoenixCards, Card[] dragonCards) {
			this.stage = stage;

			this.phoenixCards = phoenixCards;
			this.dragonCards = dragonCards;

			try {
				this.result = calculateGameResult(phoenixCards, dragonCards);
			} catch (UnsatisfiedThreeCardsRuleException e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}

	private static class UnsatisfiedThreeCardsRuleException extends Exception {
		private static final long serialVersionUID = 1L;

		private UnsatisfiedThreeCardsRuleException() {
		}

		private UnsatisfiedThreeCardsRuleException(String message) {
			super(message);
		}
	}

	@Override
	protected boolean validateStage(Stage stage) {
		// TODO Auto-generated method stub
		return false;
	}

}
