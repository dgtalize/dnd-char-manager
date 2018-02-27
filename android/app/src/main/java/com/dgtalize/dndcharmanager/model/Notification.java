package com.dgtalize.dndcharmanager.model;

import android.content.Context;

import com.dgtalize.dndcharmanager.R;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.util.Map;

/**
 * Notification for a user
 */

public class Notification {
    private String type;
    private String message;
    private int read;

    public Notification() {
        this.read = 0;
    }

    public Notification(String type, String message) {
        this();
        this.type = type;
        this.message = message;
    }

    //region Getters/Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
//endregion

    @Exclude
    public String getTypeTitle(Context context) {
        switch (this.type) {
            case NotificationType.GAME_INVITATION:
                return context.getString(R.string.game_invitation);
            case NotificationType.GAME_CHAT_MESSAGE:
                return context.getString(R.string.game_chat_message);
        }
        return context.getString(R.string.unknown);
    }
}
