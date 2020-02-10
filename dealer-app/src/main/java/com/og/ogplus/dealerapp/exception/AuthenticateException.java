package com.og.ogplus.dealerapp.exception;

import lombok.Getter;

public class AuthenticateException extends Exception {
    @Getter
    private Object token;

    public AuthenticateException(String message, Object token) {
        super(message);
        this.token = token;
    }

    public AuthenticateException(String message, Throwable cause, Object token) {
        super(message, cause);
        this.token = token;
    }
}
