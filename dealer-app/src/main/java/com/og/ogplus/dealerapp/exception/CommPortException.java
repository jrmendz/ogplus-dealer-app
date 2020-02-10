package com.og.ogplus.dealerapp.exception;

import lombok.Getter;

public class CommPortException extends Exception {

    @Getter
    private String port;

    public CommPortException(String message, String port) {
        super(message);
        this.port = port;
    }
}
