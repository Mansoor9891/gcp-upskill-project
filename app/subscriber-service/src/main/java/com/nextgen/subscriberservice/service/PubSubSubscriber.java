package com.nextgen.subscriberservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.nextgen.subscriberservice.dto.UserCreatedEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PubSubSubscriber {

    private static final Logger log = LoggerFactory.getLogger(PubSubSubscriber.class);

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Value("${subscriber.subscription}")
    private String subscriptionName;

    private Subscriber subscriber;

    public PubSubSubscriber(PubSubTemplate pubSubTemplate,
                            ObjectMapper objectMapper,
                            Validator validator) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostConstruct
    public void startSubscriber() {
        log.info("Starting Pub/Sub subscriber for subscription: {}", subscriptionName);
        subscriber = pubSubTemplate.subscribe(subscriptionName, this::handleMessage);
        log.info("Pub/Sub subscriber started successfully for: {}", subscriptionName);
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        String messageId = "unknown";

        try {
            String payload = message.getPubsubMessage().getData().toStringUtf8();
            messageId = message.getPubsubMessage().getMessageId();

            log.info("Received raw messageId={} payload={}", messageId, payload);

            UserCreatedEvent event = objectMapper.readValue(payload, UserCreatedEvent.class);

            validateEvent(event);

            processUserCreatedEvent(event);

            message.ack();
            log.info("Acknowledged messageId={}", messageId);

        } catch (Exception e) {
            log.error("Error while processing messageId={}: {}", messageId, e.getMessage(), e);
            message.nack();
        }
    }

    private void validateEvent(UserCreatedEvent event) {
        Set<ConstraintViolation<UserCreatedEvent>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + " - " + v.getMessage())
                    .collect(Collectors.joining(", "));

            throw new IllegalArgumentException("Event validation failed: " + errorMessage);
        }
    }

    private void processUserCreatedEvent(UserCreatedEvent event) {
        log.info("Processing USER_CREATED event: userId={}, name={}, email={}",
                event.getUserId(), event.getName(), event.getEmail());

        log.info("User created event processed successfully for email={}", event.getEmail());
    }

    @PreDestroy
    public void stopSubscriber() {
        if (subscriber != null) {
            log.info("Stopping Pub/Sub subscriber...");
            subscriber.stopAsync();
        }
    }
}