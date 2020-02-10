package com.og.ogplus.dealerapp.game.model;

import com.og.ogplus.common.model.Rank;
import com.og.ogplus.common.model.Suit;

import lombok.Setter;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DefaultDealerAI implements DealerAI {

    private LinkedBlockingQueue<String> cards = new LinkedBlockingQueue<>();
    @Setter
    private int desks = 8;
    @Setter
    private boolean isShuffleCard = true;

    public DefaultDealerAI() {
        shuffle();
    }
    
    @Override
    public synchronized String drawCard() {
        try {
            Thread.sleep(RandomUtils.nextInt(800, 2000));
            return cards.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void shuffle() {
        cards.clear();
        List<String> eightDeskCards = IntStream.range(0, desks).parallel()
                .mapToObj(i -> Arrays.stream(Suit.values()).parallel()
                        .flatMap(suit -> Arrays.stream(Rank.values()).parallel()
                                .map(rank -> rank.getSymbol() + suit.getSymbol()))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Collections.shuffle(eightDeskCards);
        if(isShuffleCard)
            eightDeskCards.add(RandomUtils.nextInt((desks - 1) * 50, desks * 50), SHUFFLE_CARD);

        cards.addAll(eightDeskCards);
    }


}
