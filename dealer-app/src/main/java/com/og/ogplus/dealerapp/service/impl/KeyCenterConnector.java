package com.og.ogplus.dealerapp.service.impl;

import com.og.ogplus.common.message.Message;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.exception.KeyCenterNotFoundException;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
//@Service
@Deprecated
public class KeyCenterConnector implements GameServerClientService {
    private DiscoveryClient discoveryClient;

    private AppProperty appProperty;

    private WebSocketStompClient stompClient;

    @Value("${key-center.service-id}")
    private String keyCenterServiceId;

    @Value("${key-center.endpoint}")
    private String keyCenterEndpoint;

    @Value("${key-center.enabled}")
    private boolean keyCenterEnabled;

    private ThreadPoolTaskScheduler taskScheduler;

    private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    private StompSession session;

    public KeyCenterConnector() {
        WebSocketClient client = new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())));

        stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @PostConstruct
    public void init() {
        taskScheduler.scheduleWithFixedDelay(() -> {
            try {
                Message message = messageQueue.take();
                sendMessage(message);
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }, 100);
    }

    private void sendMessage(Message message) {
        if (session == null || !session.isConnected()) {
            connect();
        }

        try {
            session.send(String.format("/app/%s/%s/info", appProperty.getGameCategory(), appProperty.getTableNumber()), message);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            sendMessage(message);
        }
    }

    private synchronized void connect() {
        while (session == null || !session.isConnected()) {
            log.debug("Connecting...");
            try {
                ListenableFuture<StompSession> future = stompClient.connect(getKeyCenterUrl(), new StompSessionHandlerAdapter() {
                });
                while (!future.isDone()) {
                    Thread.sleep(500);
                }
                session = future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    private String getKeyCenterUrl() {
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(keyCenterServiceId);
        if (serviceInstanceList.isEmpty()) {
            throw new KeyCenterNotFoundException();
        } else {
            return String.format("ws://%s/%s", serviceInstanceList.get(0).getUri().getAuthority(), keyCenterEndpoint);
        }
    }

    public void send(Message message) {
        if (keyCenterEnabled) {
            messageQueue.add(message);
        }
    }

    @Autowired
    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Autowired
    public void setAppProperty(AppProperty appProperty) {
        this.appProperty = appProperty;
    }

    @Autowired
    public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }
}
