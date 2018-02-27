package com.dgtalize.dndcharmanager.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.GameChatMessage;
import com.dgtalize.dndcharmanager.model.Notification;
import com.dgtalize.dndcharmanager.model.NotificationType;
import com.dgtalize.dndcharmanager.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Service {
    private static final String LOG_TAG = "NotificationService";

    public FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //Listener for Notifications
        setupNotificationListener();

        //Stop service is user logs out
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //stop the service
                    Log.d(LOG_TAG, "User logged out. Stopping service.");
                    stopSelf();
                }
            }
        });

    }

    private void setupNotificationListener() {

        //----- Notifications
        mDatabase.getReference().child(DatabaseContract.NODE_NOTIFICATIONS)
                .child(getUser().getUid())
                .orderByChild("read").equalTo(0)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            Notification notification = dataSnapshot.getValue(Notification.class);

                            showNotification(context, notification, dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d(LOG_TAG, "onChildChanged");
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(LOG_TAG, "onChildRemoved");
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Log.d(LOG_TAG, "onChildMoved");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, String.format("onCancelled. Error: %s", databaseError.getMessage()));
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }

    private void showNotification(Context context, Notification notification, String messageUid) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTypeTitle(context))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText(notification.getMessage())
                .setAutoCancel(true);

        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        switch (notification.getType()){
            case NotificationType.GAME_INVITATION:
                mainIntent.putExtra(MainActivity.ARG_INITIAL_FRAGMENT, R.id.nav_invites);
                break;
        }


        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[]{mainIntent}, PendingIntent.FLAG_ONE_SHOT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        /* Update firebase set notifcation with this key to 1 so it doesnt get pulled by our notification listener*/
        flagNotificationAsSent(messageUid);
    }

    private void flagNotificationAsSent(String notificationUid) {
//        mDatabase.getReference().child(DatabaseContract.NODE_NOTIFICATIONS)
//                .child(getUser().getUid())
//                .child(notificationUid)
//                .child("read")
//                .setValue(1);

        //remove the notification
        mDatabase.getReference().child(DatabaseContract.NODE_NOTIFICATIONS)
                .child(getUser().getUid())
                .child(notificationUid)
                .removeValue();
    }
}
