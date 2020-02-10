package com.og.ogplus.dealerapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.og.ogplus.common.enums.GameCategory;
import com.og.ogplus.common.message.Message;
import com.og.ogplus.dealerapp.config.AppProperty;
import com.og.ogplus.dealerapp.service.GameServerClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@Profile("!slim")
public class GameInfoPublisher implements GameServerClientService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final GameCategory gameCategory;

    private final String tableNumber;

    private final String topic;

    private KafkaTemplate<Object, Object> kafkaTemplate;

    private LinkedBlockingQueue<ProducerRecord<Object, Object>> messages = new LinkedBlockingQueue<>();

    private ThreadPoolTaskScheduler scheduler;

    public GameInfoPublisher(AppProperty appProperty, KafkaTemplate<Object, Object> kafkaTemplate, ThreadPoolTaskScheduler scheduler) {
        topic = appProperty.getMQTopic();
        gameCategory = appProperty.getGameCategory();
        tableNumber = appProperty.getTableNumber();
        this.kafkaTemplate = kafkaTemplate;
        this.scheduler = scheduler;
    }

    @PostConstruct
    private void init() {
        waitMessageToSend();
    }

    @Override
    public void send(Message message) {
        RecordHeaders headers = new RecordHeaders();
        headers.add("type", message.getAction().toValue().getBytes());
        headers.add("category", gameCategory.name().getBytes());
        headers.add("table", tableNumber.getBytes());

        try {
            String payload = objectMapper.writeValueAsString(message);
            messages.add(new ProducerRecord<>(topic, null, null, null, payload, headers));
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void waitMessageToSend() {
        scheduler.execute(() -> {
            try {
                ProducerRecord record = messages.take();
                send(record);
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }

    private void send(ProducerRecord record) {
        kafkaTemplate.send(record).addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error(ExceptionUtils.getStackTrace(ex));
                scheduler.schedule(() -> send(record), Instant.now().plusSeconds(3));
            }

            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                waitMessageToSend();
            }
        });
    }
}
