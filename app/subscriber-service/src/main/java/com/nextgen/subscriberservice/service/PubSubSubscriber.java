package com.nextgen.subscriberservice.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.pubsub.v1.Subscriber;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PubSubSubscriber {

    private static final Logger log = LoggerFactory.getLogger(PubSubSubscriber.class);

    private final PubSubTemplate pubSubTemplate;

    @Value("${subscriber.subscription}")
    private String subscriptionName;

    private Subscriber subscriber;

    public PubSubSubscriber(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void startSubscriber() {
        log.info("Starting Pub/Sub subscriber for subscription: {}", subscriptionName);

        subscriber = pubSubTemplate.subscribe(subscriptionName, this::handleMessage);

        log.info("Pub/Sub subscriber started successfully for: {}", subscriptionName);
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        try {
            String payload = message.getPubsubMessage().getData().toStringUtf8();
            String messageId = message.getPubsubMessage().getMessageId();

            log.info("Received messageId={} payload={}", messageId, payload);

            // Temporary processing for Block 5:
            // just log the event and acknowledge it
            message.ack();

            log.info("Acknowledged messageId={}", messageId);
        } catch (Exception e) {
            log.error("Error while processing Pub/Sub message", e);
            message.nack();
        }
    }

    @PreDestroy
    public void stopSubscriber() {
        if (subscriber != null) {
            log.info("Stopping Pub/Sub subscriber...");
            subscriber.stopAsync();
        }
    }
}