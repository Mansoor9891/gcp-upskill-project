package com.nextgen.cloudbackendlab.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.nextgen.cloudbackendlab.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.pubsub.topic.user-events}")
    private String topic;

    public UserEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishUserCreated(UserCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            pubSubTemplate.publish(topic, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }

}
