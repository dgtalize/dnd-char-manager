package com.dgtalize.dndcharmanager.data;

import android.util.Log;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Database Utils
 */

public abstract class DataUtils {
    private static final String LOG_TAG = "DataUtils";

    private static FirebaseDatabase database;
    private static DatabaseReference databaseReference;

    /**
     * Gets the Firebase Database reference
     *
     * @return
     */
    public static DatabaseReference getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            try {
                database.setPersistenceEnabled(true);
            } catch (DatabaseException dbEx) {
                Log.w(LOG_TAG, "Trying to set persistance when DB was already initiated.");
            }
        }
        if (databaseReference == null) {
            databaseReference = database.getReference();
        }
        return databaseReference;
    }
}
