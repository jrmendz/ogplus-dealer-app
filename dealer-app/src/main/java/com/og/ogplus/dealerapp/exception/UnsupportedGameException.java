package com.og.ogplus.dealerapp.exception;

import com.og.ogplus.common.enums.GameCategory;
import lombok.Getter;

public class UnsupportedGameException extends Exception {

    @Getter
    private GameCategory gameCategory;

    public UnsupportedGameException(String message, GameCategory gameCategory) {
        super(message);
        this.gameCategory = gameCategory;
    }
}
