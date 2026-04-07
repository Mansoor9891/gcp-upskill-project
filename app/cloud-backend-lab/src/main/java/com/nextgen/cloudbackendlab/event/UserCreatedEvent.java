package com.nextgen.cloudbackendlab.event;

public class UserCreatedEvent {

    private Long userId;
    private String name;
    private String email;

    public UserCreatedEvent() {}

    public UserCreatedEvent(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

}
