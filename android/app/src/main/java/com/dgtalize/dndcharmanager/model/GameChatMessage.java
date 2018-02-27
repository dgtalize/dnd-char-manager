package com.dgtalize.dndcharmanager.model;

import com.google.firebase.database.PropertyName;

import java.util.Map;

/**
 * Chat message inside a Game
 */

public class GameChatMessage {
    private String userUID;
    private String content;
    private Map<String, String> timestamp;

    public GameChatMessage() {
    }

    //region Getters/Setters

    @PropertyName("user")
    public String getUserUID() {
        return userUID;
    }

    @PropertyName("user")
    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    //endregion
}
