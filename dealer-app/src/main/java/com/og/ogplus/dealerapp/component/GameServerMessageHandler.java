package com.og.ogplus.dealerapp.component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.og.ogplus.dealerapp.game.Game;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class GameServerMessageHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Game game;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection Established.");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("In Bound:{}", message.getPayload());
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload().toString());
            if (jsonNode.has("action") && "extend".equals(jsonNode.get("action").asText())) {
                game.extendBettingTime();
            }
        } catch (JsonParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Transport Error Occur.");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.warn("Connection Closed.");
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Autowired
    public void setGame(Game game) {
        this.game = game;
    }
}
