package io.github.zhyshko.dto;

import java.util.UUID;

public class User {

    private String username;
    private UUID id;

    public User() {
    }

    public User(String username, UUID id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
