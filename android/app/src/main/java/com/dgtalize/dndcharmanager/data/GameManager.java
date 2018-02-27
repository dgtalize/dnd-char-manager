package com.dgtalize.dndcharmanager.data;

import android.util.Log;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Procedures for Game management
 */

public class GameManager {

    private static final String LOG_TAG = "GameManager";

    public static void addCharacterToGame(final Game game, Character character, final DatabaseReference.CompletionListener listener) {
        Map<String, Object> childUpdates = new HashMap<>();
        //Add the Character to the Game
        childUpdates.put("/games/" + game.getUid() + "/characters/" + character.getUid(), true);
        //set the Game to the character
        childUpdates.put("/characters/" + character.getUid() + "/game", game.getUid());

        DataUtils.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    //Remove the invitation
                    DataUtils.getDatabase().child(DatabaseContract.NODE_GAMEINVITES)
                            .child(game.getUid())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    //invitation removed
                                    if (listener != null) {
                                        listener.onComplete(databaseError, databaseReference);
                                    }
                                }
                            });
                } else {
                    Log.e(LOG_TAG, databaseError.getMessage());
                    if (listener != null) {
                        listener.onComplete(databaseError, databaseReference);
                    }
                }
            }
        });
    }

    public static void removeCharacterFromGame(final Game game, Character character, final DatabaseReference.CompletionListener listener) {
        Map<String, Object> childUpdates = new HashMap<>();
        //Set NULL to the Character of the Game
        childUpdates.put("/games/" + game.getUid() + "/characters/" + character.getUid(), null);
        //set NULL to the Game of the character
        childUpdates.put("/characters/" + character.getUid() + "/game", null);

        DataUtils.getDatabase().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    //Remove the invitation
                    DataUtils.getDatabase().child(DatabaseContract.NODE_GAMEINVITES)
                            .child(game.getUid())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    //invitation removed
                                    if (listener != null) {
                                        listener.onComplete(databaseError, databaseReference);
                                    }
                                }
                            });
                } else {
                    Log.e(LOG_TAG, databaseError.getMessage());
                    if (listener != null) {
                        listener.onComplete(databaseError, databaseReference);
                    }
                }
            }
        });
    }
}
