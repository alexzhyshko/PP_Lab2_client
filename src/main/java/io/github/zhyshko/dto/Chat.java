package io.github.zhyshko.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat {

    private List<String> messages = new ArrayList<>();
    private UUID uuid;
    private String name;
    private boolean broadcast;

    public Chat() {

    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        if(messages == null) {
            return;
        }
        List<String> oldValue = new ArrayList<>();
        oldValue.addAll(this.messages);
        this.messages.clear();
        this.messages.addAll(messages);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }



}
