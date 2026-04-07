package com.nextgen.cloudbackendlab.event;

public class UserEvent {

    private String eventType;
    private Long userId;
    private String name;
    private String email;

    public UserEvent() {
    }

    public UserEvent(String eventType, Long userId, String name, String email) {
        this.eventType = eventType;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}