package com.nextgen.cloudbackendlab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.nextgen.cloudbackendlab.event.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.pubsub.topic.user-events}")
    private String topic;
    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);
    public UserEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishUserCreated(UserEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Published eventType={} payload={}", event.getEventType(), payload);
            pubSubTemplate.publish(topic, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish USER_CREATED event", e);
        }
    }

    public void publishUserUpdated(UserEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Published eventType={} payload={}", event.getEventType(), payload);
            pubSubTemplate.publish(topic, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish USER_UPDATED event", e);
        }
    }
}