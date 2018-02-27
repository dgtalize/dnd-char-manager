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
import com.dgtalize.dndcharmanager.model.Item;

import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Item}.
 */
public class CharacterItemRecyclerViewAdapter extends RecyclerView.Adapter<CharacterItemRecyclerViewAdapter.ViewHolder> {

    private final Map<String, Item> mValues;
    private Character mCharacter;
    private OnItemActionListener itemActionListener;

    public CharacterItemRecyclerViewAdapter(Map<String, Item> items, Character character) {
        mValues = items;
        mCharacter = character;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characteritem_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mWeightView.setText(String.format("%.2f lb", holder.mItem.getWeight()));
        holder.mMagicalView.setText(holder.mItem.isMagical() ? "Magical" : "Not magical");

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openItemView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.character_item);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open:
                                openItemView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_duplicate:
                                Toast.makeText(holder.mView.getContext(), "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_delete:
                                deleteItem(holder.mView.getContext(), holder.mItem);
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

    public void addItem(String key, Item item) {
        mValues.put(key, item);
        notifyDataSetChanged();
    }

    private void openItemView(Context context, Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(item.getName())
                .setMessage(item.getDescription())
                .show();
    }

    private void editArmorView(Context context, Item item) {
        if (itemActionListener != null) {
            itemActionListener.onItemEdit(item);
        }
    }

    private void deleteItem(final Context context, final Item item) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //remove the item from Character
                        mCharacter.getItems().remove(item.getUid());
                        //remove the item from the list
                        mValues.remove(item);
                        notifyDataSetChanged();

                        if (itemActionListener != null) {
                            itemActionListener.onItemDelete(item);
                        }

                        Toast.makeText(context, String.format("The item '%s' has been deleted", item.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_delete_item)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public void setItemActionListener(OnItemActionListener listener) {
        this.itemActionListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mWeightView;
        public final TextView mMagicalView;
        public final TextView mOptionsView;
        public Item mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mWeightView = (TextView) view.findViewById(R.id.weightTextView);
            mMagicalView = (TextView) view.findViewById(R.id.magicalTextView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }

    public interface OnItemActionListener {
        void onItemEdit(Item item);

        void onItemDelete(Item item);
    }
}
