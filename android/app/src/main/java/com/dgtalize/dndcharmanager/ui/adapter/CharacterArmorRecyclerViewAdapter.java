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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterArmor;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CharacterArmor}.
 */
public class CharacterArmorRecyclerViewAdapter extends RecyclerView.Adapter<CharacterArmorRecyclerViewAdapter.ViewHolder> {

    private final Map<String, CharacterArmor> mValues;
    private Character mCharacter;
    private OnArmorActionListener armorActionListener;

    public CharacterArmorRecyclerViewAdapter(Map<String, CharacterArmor> items, Character character) {
        if (items == null) {
            items = new HashMap<>();
        }
        mValues = items;
        mCharacter = character;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characterarmor_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mBonusView.setText(String.valueOf(holder.mItem.getArmorBonus()));
        holder.mEquippedView.setVisibility(holder.mItem.isEquipped() ? View.VISIBLE : View.INVISIBLE);

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editArmorView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.character_armor);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                editArmorView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_duplicate:
                                Toast.makeText(holder.mView.getContext(), "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_delete:
                                deleteArmor(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_equip:
//                                mCharacter.getArmors().get(holder.mItem.getUid())
//                                        .setEquipped(!holder.mItem.isEquipped());
                                holder.mItem.setEquipped(!holder.mItem.isEquipped());
                                notifyItemChanged(holder.getAdapterPosition());
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

    public void addArmor(String key, CharacterArmor item) {
        mValues.put(key, item);
        notifyDataSetChanged();
    }

    private void editArmorView(Context context, CharacterArmor armor) {
        if (armorActionListener != null) {
            armorActionListener.onArmorEdit(armor);
        }
    }

    private void deleteArmor(final Context context, final CharacterArmor armor) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //remove the armor from the Character
                        mCharacter.getArmors().remove(armor.getUid());
                        //remove the item from the list
                        mValues.remove(armor);
                        notifyDataSetChanged();

                        if (armorActionListener != null) {
                            armorActionListener.onArmorDelete(armor);
                        }

                        Toast.makeText(context, String.format("The armor '%s' has been deleted", armor.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_remove_armor)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public void setArmorActionListener(OnArmorActionListener listener) {
        this.armorActionListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mBonusView;
        public final ImageView mEquippedView;
        public final TextView mOptionsView;
        public CharacterArmor mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mBonusView = (TextView) view.findViewById(R.id.armorBonusTextView);
            mEquippedView = (ImageView) view.findViewById(R.id.equippedView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }

    public interface OnArmorActionListener {
        void onArmorEdit(CharacterArmor armor);

        void onArmorDelete(CharacterArmor armor);
    }
}
