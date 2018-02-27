package com.dgtalize.dndcharmanager.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Firebase Messaging Service
 */

public class DnDFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = "DnDFBMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "Message From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(LOG_TAG, "Message data payload: " + remoteMessage.getData());
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(LOG_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
    }
}
