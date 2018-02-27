package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseRestrictedActivity extends BaseActivity {
    private static final String LOG_TAG = "BaseRestrictedActivity";

    protected FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            //Already logged in
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        firebaseUser = firebaseAuth.getCurrentUser();
    }

    /**
     * Gets the current logged-in firebase user
     * @return
     */
    public FirebaseUser getFirebaseUser() {
        return this.firebaseUser;
    }
}
