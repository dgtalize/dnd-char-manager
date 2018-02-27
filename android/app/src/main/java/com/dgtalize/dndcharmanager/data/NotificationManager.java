package com.dgtalize.dndcharmanager.data;

import com.dgtalize.dndcharmanager.model.Notification;
import com.google.firebase.database.DatabaseReference;

public class NotificationManager {
    public static void addNotification(String recipientUID, String type, String message, DatabaseReference.CompletionListener listener) {
        Notification notification = new Notification(type, message);

        DatabaseReference notifRef = DataUtils.getDatabase().child(DatabaseContract.NODE_NOTIFICATIONS);
        String newUid = notifRef.push().getKey();

        //save the new notification
        notifRef.child(recipientUID).child(newUid).setValue(notification, listener);
    }
}
