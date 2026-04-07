package com.nextgen.cloudbackendlab.event;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class UserEvent {

    @NotBlank(message = "eventType is required")
    private String eventType;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
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