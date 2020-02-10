package com.og.ogplus.dealerapp.exception;

public class FailedChangeGameResultException extends Exception{

    public FailedChangeGameResultException(String message) {
        super(message);
    }

    public FailedChangeGameResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedChangeGameResultException(Throwable cause) {
        super(cause);
    }
}
