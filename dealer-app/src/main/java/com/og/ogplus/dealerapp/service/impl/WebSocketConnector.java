package com.og.ogplus.dealerapp.service.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.og.ogplus.common.db.entity.Table;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.dealerapp.component.GameServerMessageHandler;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.game.AbstractGame;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Primary
@Service
@Profile("!slim")
@ConditionalOnExpression("'${app.game-category}'=='MONEY_WHEEL' || '${app.game-category}'=='BACCARAT' || '${app.game-category}'=='DRAGON_TIGER'")
public class WebSocketConnector implements GameServerClientService, AbstractGame.GameListener {

    private final Lock lock = new ReentrantLock();
    private WebSocketClient client;
    private WebSocketSession session;
    private GameServerMessageHandler messageHandler;
    private ObjectMapper objectMapper;
    private ThreadPoolTaskScheduler taskScheduler;
    private AppProperty appProperty;
    private Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private List<Listener> listenerList = new ArrayList<>();
    private String url;

    @Autowired
    private GameInfoPublisher keyCenterConnector;

    public WebSocketConnector() {
        client = new StandardWebSocketClient();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
        sp.setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("");
            }
        });
        objectMapper.setSerializerProvider(sp);
    }


    @Override
    public void onGameInitialized(Table table) {
        url = String.format("%s/%s/%s%s", appProperty.getSocketUrl(table.getGameCode().getCode()), table.getNumber(),
                appProperty.getSocketSessionPrefix(), table.getNumber());
        startSendingGameMessage();
    }

    private void startSendingGameMessage() {
        taskScheduler.scheduleWithFixedDelay(() -> {
            if (session == null || !session.isOpen()) {
                try {
                    connect();
                    listenerList.forEach(listener -> taskScheduler.execute(listener::onSendGameMessageSuccess));
                } catch (InterruptedException | ExecutionException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    listenerList.forEach(listener -> taskScheduler.execute(listener::onSendGameMessageFailed));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        log.error(ExceptionUtils.getStackTrace(ex));
                    }
                    return;
                }
            }

            String message = messageQueue.peek();
            if (message != null) {
                try {
                    session.sendMessage(new TextMessage(message));
                    messageQueue.remove(message);
                    log.info("Out Bound:{}", message);
                    listenerList.forEach(listener -> taskScheduler.execute(listener::onSendGameMessageSuccess));
                } catch (Exception e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                    listenerList.forEach(listener -> taskScheduler.execute(listener::onSendGameMessageFailed));
                }
            }
        }, 100);
    }

    private void connect() throws InterruptedException, ExecutionException {
        if (session == null || !session.isOpen()) {
            boolean isGetLock = lock.tryLock(3, TimeUnit.SECONDS);
            if (isGetLock) {
                try {
                    if (session == null || !session.isOpen()) {
                        log.debug("Connecting...");
                        ListenableFuture<WebSocketSession> future = client.doHandshake(messageHandler, url);

                        while (!future.isDone()) {
                            Thread.sleep(500);
                        }
                        session = future.get();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private void disconnect() {
        try {
            lock.lock();
            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void send(Message message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            messageQueue.add(payload);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        keyCenterConnector.send(message);
    }

    @Autowired
    public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Autowired
    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    @Autowired
    public void setMessageHandler(GameServerMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Autowired(required = false)
    public void setListenerList(List<Listener> listenerList) {
        this.listenerList.addAll(listenerList);
    }
}
