package com.dgtalize.dndcharmanager.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Feat;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Feat}.
 */
public class CharacterFeatRecyclerViewAdapter extends RecyclerView.Adapter<CharacterFeatRecyclerViewAdapter.ViewHolder> {

    private final Map<String, Feat> mValues;
    private Character mCharacter;
    private OnFeatActionListener featActionListener = null;

    public CharacterFeatRecyclerViewAdapter(Map<String, Feat> items, Character character) {
        if (items == null) {
            items = new HashMap<>();
        }
        mValues = items;
        mCharacter = character;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characterfeat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mShortDescView.setText(String.valueOf(holder.mItem.getShortDescription()));

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeatView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.character_feat);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open:
                                openFeatView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_duplicate:
                                Toast.makeText(holder.mView.getContext(), "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_delete:
                                deleteFeat(holder.mView.getContext(), holder.mItem);
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

    public void addFeat(String key, Feat item) {
        mValues.put(key, item);
        notifyDataSetChanged();
    }

    private void openFeatView(Context context, Feat feat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(feat.getName())
                .setMessage(feat.getDescription())
                .show();
    }

    private void deleteFeat(final Context context, final Feat feat) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //remove the Feat from the character
                        mCharacter.getFeats().remove(feat.getUid());
                        //remove the item from the list
                        mValues.remove(feat);
                        notifyDataSetChanged();

                        if (featActionListener != null) {
                            featActionListener.onFeatDelete(feat);
                        }

                        Toast.makeText(context, String.format("The feat '%s' has been deleted", feat.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_remove_feat)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mShortDescView;
        public final TextView mOptionsView;
        public Feat mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mShortDescView = (TextView) view.findViewById(R.id.shortDescTextView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }


    public void setFeatActionListener(OnFeatActionListener listener) {
        this.featActionListener = listener;
    }

    public interface OnFeatActionListener {
        void onFeatDelete(Feat feat);
    }
}
