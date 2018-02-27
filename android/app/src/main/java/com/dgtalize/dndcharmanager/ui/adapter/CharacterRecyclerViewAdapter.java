package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DataUtils;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.data.GameManager;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.CharacterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Character}.
 */
public class CharacterRecyclerViewAdapter extends RecyclerView.Adapter<CharacterRecyclerViewAdapter.ViewHolder> {
    private static final String LOG_TAG = "CharacterRVAdapter";

    private final List<Character> mValues;
    private OnCharacterChangeListener changeListener;

    public CharacterRecyclerViewAdapter(List<Character> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_character_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mRaceView.setText(mValues.get(position).getRace());
        holder.mClassView.setText(mValues.get(position).getCharClass());
        holder.mLevelView.setText("Lvl " + mValues.get(position).getLevel());
        if (holder.mItem.getGame() != null) {
            holder.mGameView.setVisibility(View.VISIBLE);
        }

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCharacterView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.character_listitem);

                //if the Character is not part of a Game, hide the option
                if(TextUtils.isEmpty(holder.mItem.getGame())) {
                    popup.getMenu().findItem(R.id.action_remove_from_game).setVisible(false);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open:
                                openCharacterView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_rename:
                                renameCharacter(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_duplicate:
                                Toast.makeText(holder.mView.getContext(), "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_remove_from_game:
                                removeCharacterFromGame(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_delete:
                                deleteCharacter(holder.mView.getContext(), holder.mItem);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addItem(Character item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mValues.clear();
        notifyDataSetChanged();
    }

    private void openCharacterView(Context context, Character character) {
        Intent intent = new Intent(context, CharacterActivity.class);
        intent.putExtra(CharacterActivity.EXTRA_CHARACTER, character);
        context.startActivity(intent);

    }

    private void renameCharacter(final Context context, final Character character) {
        // get the Modify Points dialog view
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_rename, null);
        //get the new name Edit text
        final EditText newnameEditText = (EditText) dialogView.findViewById(R.id.newnameEditText);
        newnameEditText.setText(character.getName());

        //build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String newName = newnameEditText.getText().toString();
                                if (!TextUtils.isEmpty(newName)) {
                                    //--- change in database
                                    DatabaseReference database = DataUtils.getDatabase();
                                    //remove from user
                                    database.child("characters").child(character.getUid())
                                            .child("name").setValue(newName);
                                    //--- change in object
                                    character.setName(newName);

                                    if (changeListener != null) {
                                        changeListener.onChange(character);
                                    }

                                    Toast.makeText(context, String.format("The character's name was changed"),
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    private void deleteCharacter(final Context context, final Character character) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //--- remove the character
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference database = DataUtils.getDatabase();
                        //remove from user
                        database.child("users").child(firebaseUser.getUid())
                                .child("characters").child(character.getUid()).removeValue();
                        //remove chracter
                        database.child("characters").child(character.getUid()).removeValue();

                        if (changeListener != null) {
                            changeListener.onDelete(character);
                        }

                        Toast.makeText(context, String.format("The character '%s' has been deleted", character.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_delete_character)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void removeCharacterFromGame(final Context context, final Character character) {
        DatabaseReference gameRef = DataUtils.getDatabase().child(DatabaseContract.NODE_GAMES).child(character.getGame());
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Game game = dataSnapshot.getValue(Game.class);
                if (game != null) {
                    game.setUid(dataSnapshot.getKey());
                    //now that we have the Game, remove
                    GameManager.removeCharacterFromGame(game, character, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(context, String.format(context.getString(R.string.character_removed_from_game), character.getName(), game.getName()), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, String.format(context.getString(R.string.generic_problem_message), databaseError.getMessage()), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "Getting DB Game. " + databaseError.getMessage());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRaceView;
        public final TextView mClassView;
        public final TextView mGameView;
        public final TextView mLevelView;
        public final TextView mOptionsView;
        public Character mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mRaceView = (TextView) view.findViewById(R.id.raceTextView);
            mClassView = (TextView) view.findViewById(R.id.classTextView);
            mLevelView = (TextView) view.findViewById(R.id.levelTextView);
            mGameView = (TextView) view.findViewById(R.id.gameTextView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }


    public void setChangeListener(OnCharacterChangeListener listener) {
        this.changeListener = listener;
    }

    /**
     * The character has changed
     */
    public interface OnCharacterChangeListener {
        void onChange(Character character);

        void onDelete(Character character);
    }
}
