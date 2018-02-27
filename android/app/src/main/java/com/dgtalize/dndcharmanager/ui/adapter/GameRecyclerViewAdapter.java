package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.GameActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Game}.
 */
public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.ViewHolder> {

    private final List<Game> mValues;
    private OnGameChangeListener changeListener;

    public GameRecyclerViewAdapter(List<Game> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_game_list_item, parent, false);
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
                openGameView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.game_item);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open:
                                openGameView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_rename:
                                renameGame(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_delete:
                                deleteGame(holder.mView.getContext(), holder.mItem);
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

    public void addItem(Game item) {
        mValues.add(item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mValues.clear();
        notifyDataSetChanged();
    }

    private void openGameView(Context context, Game character) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_GAME, character);
        context.startActivity(intent);
    }

    private void renameGame(final Context context, final Game game) {
        // get the Modify Points dialog view
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.dialog_rename, null);
        //get the new name Edit text
        final EditText newnameEditText = (EditText) dialogView.findViewById(R.id.newnameEditText);
        newnameEditText.setText(game.getName());

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
                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    //remove from user
                                    database.child(DatabaseContract.NODE_GAMES).child(game.getUid())
                                            .child("name").setValue(newName);
                                    //--- change in object
                                    game.setName(newName);

                                    if (changeListener != null) {
                                        changeListener.onChange(game);
                                    }

                                    Toast.makeText(context, String.format("The game's name was changed"),
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

    private void deleteGame(final Context context, final Game character) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        /*
                        //--- remove the game
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        //remove from user
                        database.child("users").child(firebaseUser.getUid())
                                .child("characters").child(character.getUid()).removeValue();
                        //remove chracter
                        database.child("characters").child(character.getUid()).removeValue();

                        if(changeListener != null){
                            changeListener.onDelete(character);
                        }
                        */

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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mQuantityView;
        public final TextView mOptionsView;
        public Game mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mQuantityView = (TextView) view.findViewById(R.id.quantityTextView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }


    public void setChangeListener(OnGameChangeListener listener) {
        this.changeListener = listener;
    }

    /**
     * The character has changed
     */
    public interface OnGameChangeListener {
        void onChange(Game game);

        void onDelete(Game game);
    }
}
