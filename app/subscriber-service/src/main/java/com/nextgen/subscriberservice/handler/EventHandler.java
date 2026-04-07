package com.nextgen.subscriberservice.handler;

import com.nextgen.subscriberservice.dto.UserEvent;

public interface EventHandler {

    String supportsEventType();

    void handle(UserEvent event);
}