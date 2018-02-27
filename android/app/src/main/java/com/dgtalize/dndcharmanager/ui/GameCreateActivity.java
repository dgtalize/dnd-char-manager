package com.dgtalize.dndcharmanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class GameCreateActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "GameCreateActivity";

    public static final String EXTRA_GAME = "game";

    private EditText nameText;
    private EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);

        nameText = (EditText) findViewById(R.id.nameText);
        descriptionText = (EditText) findViewById(R.id.descriptionEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        //show loading
        showProgressDialog();

        String newUid = getDatabase().child(DatabaseContract.NODE_GAMES).push().getKey();

        final Game game = new Game(newUid);
        game.setName(nameText.getText().toString());
        game.setDescription(descriptionText.getText().toString());
        game.setMasterUID(firebaseUser.getUid());

        //save into database
        getDatabase().child(DatabaseContract.NODE_GAMES).child(game.getUid()).setValue(game, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //once the Game was saved
                if(databaseError != null) {
                    Toast.makeText(GameCreateActivity.this, String.format("Error: %s", databaseError.getMessage()), Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, databaseError.toString());

                    //hide loading
                    hideProgressDialog();
                }else {
                    //assign the Game to the user, as master
                    getDatabase().child("users").child(firebaseUser.getUid()).child("games").child(game.getUid()).setValue(true, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //once the game was assigned to the user
                            Toast.makeText(GameCreateActivity.this, String.format("%s saved", game.getName()), Toast.LENGTH_LONG).show();

                            //hide loading
                            hideProgressDialog();

                            Intent data = new Intent();
                            //set the data to pass back
                            data.putExtra(EXTRA_GAME, game);
                            GameCreateActivity.this.setResult(Activity.RESULT_OK, data);
                            GameCreateActivity.this.finish();
                        }
                    });
                }//saving error
            }
        });
    }
}
