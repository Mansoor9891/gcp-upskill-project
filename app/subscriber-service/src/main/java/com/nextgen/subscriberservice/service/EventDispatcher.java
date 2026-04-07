package com.nextgen.subscriberservice.service;

import com.nextgen.subscriberservice.dto.UserEvent;
import com.nextgen.subscriberservice.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    private final Map<String, EventHandler> handlers;

    public EventDispatcher(List<EventHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        handler -> handler.supportsEventType().toUpperCase(),
                        handler -> handler
                ));
    }

    public void dispatch(UserEvent event) {
        String eventType = event.getEventType().toUpperCase();

        EventHandler handler = handlers.get(eventType);

        if (handler != null) {
            handler.handle(event);
        } else {
            log.info("yakabakabu");
            log.warn("No handler found for eventType={}", event.getEventType());
        }
    }
}