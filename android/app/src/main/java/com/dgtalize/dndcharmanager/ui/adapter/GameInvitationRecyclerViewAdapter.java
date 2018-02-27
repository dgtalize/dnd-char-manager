package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DataUtils;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.data.GameManager;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Game}.
 */
public class GameInvitationRecyclerViewAdapter extends RecyclerView.Adapter<GameInvitationRecyclerViewAdapter.ViewHolder> {
    private static final String LOG_TAG = "GameInvitationRVAdapter";

    private final List<Game> mValues;

    public GameInvitationRecyclerViewAdapter(List<Game> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gameinvite_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mQuantityView.setText(String.valueOf(holder.mItem.getCharactersCount()));

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInviteView(holder.mView.getContext(), holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void openInviteView(final Context context, final Game game) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.select_character_game);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final ArrayAdapter<Character> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Character character = arrayAdapter.getItem(which);
                //process the Invitation acceptance
                acceptInvitation(context, game, character);
            }
        });

        DatabaseReference charsRef = DataUtils.getDatabase().child(DatabaseContract.NODE_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("characters");
        charsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot charKeyDS : dataSnapshot.getChildren()) {
                    //now get the entire Character
                    DatabaseReference characterReference = DataUtils.getDatabase().child(DatabaseContract.NODE_CHARACTERS)
                            .child(charKeyDS.getKey());
                    characterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot characterDS) {
                            Character character = characterDS.getValue(Character.class);
                            //Only characters that are NOT part of a game
                            if (character != null && TextUtils.isEmpty(character.getGame())) {
                                character.setUid(characterDS.getKey());
                                arrayAdapter.add(character);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                //now everythinig is loaded, then show dialog
                dialogBuilder.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadUserCharacters:onCancelled", databaseError.toException());
            }
        });
    }

    private void acceptInvitation(final Context context, final Game game, final Character character) {
        GameManager.addCharacterToGame(game, character, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, String.format(context.getString(R.string.invitation_game_accepted), game.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, String.format(context.getString(R.string.generic_problem_message), databaseError.getMessage()), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void addItem(Game item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mValues.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mQuantityView;
        public Game mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mQuantityView = (TextView) view.findViewById(R.id.quantityTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }

}
