package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.UserMeta;
import com.dgtalize.dndcharmanager.service.NotificationService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final String LOG_TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private GoogleApiClient mGoogleApiClient;
    private TextView privacyPolicyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        privacyPolicyTextView = (TextView) findViewById(R.id.privacyPolicyTextView);

        // Button listeners
        privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browser);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // UserMeta is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    prepareDatabaseUserMeta(user, new DatabaseUserPrepareListener() {
                        @Override
                        public void onUserPrepared() {
                            //--- Finish entire login process
                            finishLoginProcess();
                        }
                    });

                    return;
                } else {
                    // UserMeta is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(LOG_TAG, "Google SignIn success.");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(LOG_TAG, "Google SignIn failed: " + result.getStatus().toString());
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Log.d(LOG_TAG, String.format("Logged in with Google: %s", user.getEmail()));
            Log.d(LOG_TAG, String.format("Logged in with Firebase: %s", user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            Log.d(LOG_TAG, "Signed out");

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.w(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }
    }

    /**
     * Executed when the login has finished successfully and this Activity and process needs to be
     * finished.
     */
    private void finishLoginProcess() {
        hideProgressDialog();

        // Call the Main Activity and close this one
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.finish();
        startActivity(intent);
    }

    /**
     * Prepares the User meta data in the Database
     *
     * @param fbUser
     * @param listener
     */
    private void prepareDatabaseUserMeta(final FirebaseUser fbUser, final DatabaseUserPrepareListener listener) {
        final DatabaseReference userMetaRef = getDatabase().child(DatabaseContract.NODE_USERS)
                .child(fbUser.getUid()).child("meta");
        userMetaRef.keepSynced(true);
        //gets the user to see if it exists in the DB, or if it has all required fields
        userMetaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserMeta userMeta = dataSnapshot.getValue(UserMeta.class);
                if (userMeta == null) {
                    userMeta = new UserMeta();
                }
                //initialize userMeta values, if required
                userMeta.initializeValues(fbUser);
                //persist
                userMetaRef.setValue(userMeta, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        //the userMeta db update was completed, so proceed
                        //now make sure the mapping between user and email is done
                        getDatabase().child(DatabaseContract.NODE_USERSEMAILS).child(ConvertUtils.encodeEmailDB(fbUser.getEmail()))
                                .setValue(fbUser.getUid(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError != null){
                                            Log.e(LOG_TAG, databaseError.getMessage());
                                        }
                                        //the mapping was saved, then proceed
                                        listener.onUserPrepared();
                                    }
                                });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "db user load cancelled");
            }
        });
    }

    private interface DatabaseUserPrepareListener {
        public void onUserPrepared();
    }
}
