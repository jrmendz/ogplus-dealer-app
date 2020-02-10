package com.og.ogplus.dealerapp.service;

public interface MessageListener {

    int INFO = 0;

    int WARM = 1;

    int ERROR = 2;

    void handleMessage(int type, String message);

}
