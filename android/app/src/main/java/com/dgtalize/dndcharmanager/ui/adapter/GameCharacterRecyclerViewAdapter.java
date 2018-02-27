package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.CharacterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Character}.
 */
public class GameCharacterRecyclerViewAdapter extends RecyclerView.Adapter<GameCharacterRecyclerViewAdapter.ViewHolder> {

    private final List<Character> mValues;
    private OnCharacterChangeListener changeListener;
    private Game game;

    public GameCharacterRecyclerViewAdapter(List<Character> items, Game game) {
        mValues = items;
        this.game = game;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gamecharacter_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mRaceView.setText(mValues.get(position).getRace());
        holder.mClassView.setText(mValues.get(position).getCharClass());
        holder.mLevelView.setText("Lvl " + mValues.get(position).getLevel());
        holder.mHpProgress.setMax(holder.mItem.getHp());
        holder.mHpProgress.setProgress(holder.mItem.getCurrentHp());

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
                popup.inflate(R.menu.game_character_item);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open:
                                Toast.makeText(holder.mView.getContext(), R.string.function_not_implemented, Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_remove:
                                Toast.makeText(holder.mView.getContext(), R.string.function_not_implemented, Toast.LENGTH_SHORT).show();
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
//        Intent intent = new Intent(context, CharacterActivity.class);
//        intent.putExtra(CharacterActivity.EXTRA_CHARACTER, character);
//        context.startActivity(intent);
    }

    private void removeCharacter(final Context context, final Character character) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //--- remove the character from the game
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        //remove game from from character
                        database.child(DatabaseContract.NODE_CHARACTERS).child(character.getUid())
                                .child("game").child(game.getUid()).removeValue();
                        //remove chracter from thame
                        database.child(DatabaseContract.NODE_GAMES).child(game.getUid())
                                .child("characters").child(character.getUid()).removeValue();

                        if (changeListener != null) {
                            changeListener.onRemove(character);
                        }

                        Toast.makeText(context, String.format(context.getString(R.string.character_removed), character.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_remove_character_from_game)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRaceView;
        public final TextView mClassView;
        public final TextView mLevelView;
        public final ProgressBar mHpProgress;
        public final TextView mOptionsView;
        public Character mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mRaceView = (TextView) view.findViewById(R.id.raceTextView);
            mClassView = (TextView) view.findViewById(R.id.classTextView);
            mLevelView = (TextView) view.findViewById(R.id.levelTextView);
            mHpProgress = (ProgressBar) view.findViewById(R.id.hpProgressBar);
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

        void onRemove(Character character);
    }
}
