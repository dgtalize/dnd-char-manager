package com.dgtalize.dndcharmanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.data.NotificationManager;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.model.NotificationType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class GamePlayerInviteActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "GamePlayerInviteAct";

    public static final String EXTRA_GAME = "GAME";

    private Game game;
    private TextView emailsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_player_invite);

        Intent intent = getIntent();
        game = intent.getParcelableExtra(EXTRA_GAME);

        emailsText = (EditText) findViewById(R.id.emailsText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_player_invite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        //show loading
        showProgressDialog();

        String emailsStr = emailsText.getText().toString();
        String[] emails = emailsStr.split("[,;\\s]");

        //save the invitations
        HashMap<String, Boolean> invites = new HashMap<>();

        final int[] remainingSaves = {emails.length};
        for (String email : emails) {
            String emailEncoded = ConvertUtils.encodeEmailDB(email);
            //get the user's UID and save it as invite
            getDatabase().child(DatabaseContract.NODE_USERSEMAILS).child(emailEncoded)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userUID = dataSnapshot.getValue(String.class);
                            if (userUID == null) {
                                Log.d(LOG_TAG, String.format("The user '%s' does not exists", dataSnapshot.getKey()));
                                remainingSaves[0]--;
                                if (remainingSaves[0] == 0) {
                                    //no more invites to save, proceed
                                    finishSave();
                                }
                            } else {
                                //save the invite
                                getDatabase().child(DatabaseContract.NODE_GAMEINVITES).child(game.getUid())
                                        .child(userUID)
                                        .setValue(true, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                NotificationManager.addNotification(userUID,
                                                        NotificationType.GAME_INVITATION,
                                                        String.format(getString(R.string.game_invite_message), game.getName()),
                                                        new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                //the invite was saved and notifiation sent
                                                                remainingSaves[0]--;
                                                                if (remainingSaves[0] == 0) {
                                                                    //no more invites to save, proceed
                                                                    finishSave();
                                                                }
                                                            }
                                                        });

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(LOG_TAG, "get user UID by email cancelled");
                        }
                    });
        }

    }

    private void finishSave() {
        Toast.makeText(GamePlayerInviteActivity.this, String.format(getString(R.string.invites_sent)), Toast.LENGTH_LONG).show();

        //hide loading
        hideProgressDialog();

        Intent data = new Intent();
        //set the data to pass back
//                        data.putExtra(EXTRA_CHARACTER, character);
        GamePlayerInviteActivity.this.setResult(Activity.RESULT_OK, data);
        GamePlayerInviteActivity.this.finish();
    }
}
