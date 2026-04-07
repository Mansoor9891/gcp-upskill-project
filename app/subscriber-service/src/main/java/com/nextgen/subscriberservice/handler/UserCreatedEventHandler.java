package com.nextgen.subscriberservice.handler;

import com.nextgen.subscriberservice.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventHandler implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedEventHandler.class);

    @Override
    public String supportsEventType() {
        return "USER_CREATED";
    }

    @Override
    public void handle(UserEvent event) {
        log.info("Processing USER_CREATED event: userId={}, name={}, email={}",
                event.getUserId(), event.getName(), event.getEmail());

        log.info("User created event processed successfully for email={}", event.getEmail());
    }
}