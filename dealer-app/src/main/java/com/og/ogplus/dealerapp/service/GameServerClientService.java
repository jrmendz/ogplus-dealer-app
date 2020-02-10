package com.og.ogplus.dealerapp.service;

import com.og.ogplus.common.message.Message;

public interface GameServerClientService {

    void send(Message message);

    interface Listener {

        default void onSendGameMessageSuccess() {

        }

        default void onSendGameMessageFailed() {

        }
    }
}
