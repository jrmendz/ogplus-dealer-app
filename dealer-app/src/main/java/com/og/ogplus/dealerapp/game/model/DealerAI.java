package com.og.ogplus.dealerapp.game.model;

import com.og.ogplus.dealerapp.config.CardProperty;

public interface DealerAI {
    String SHUFFLE_CARD = CardProperty.SHUFFLE_CODE;

    String drawCard();

    void shuffle();

}
