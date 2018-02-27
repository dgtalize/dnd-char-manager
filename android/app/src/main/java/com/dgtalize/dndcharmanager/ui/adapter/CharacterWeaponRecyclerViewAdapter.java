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
import com.dgtalize.dndcharmanager.model.CharacterWeapon;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CharacterWeapon}.
 */
public class CharacterWeaponRecyclerViewAdapter extends RecyclerView.Adapter<CharacterWeaponRecyclerViewAdapter.ViewHolder> {

    private final Map<String, CharacterWeapon> mValues;
    private Character mCharacter;
    private OnWeaponActionListener weaponActionListener;

    public CharacterWeaponRecyclerViewAdapter(Map<String, CharacterWeapon> items, Character character) {
        if (items == null) {
            items = new HashMap<>();
        }
        mValues = items;
        mCharacter = character;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characterweapon_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mAttackView.setText(String.valueOf(mCharacter.getAttackBonus(holder.mItem)));
        holder.mDamageView.setText(String.format("%s | %s", holder.mItem.getDamageSmall(), holder.mItem.getDamageMedium()));
        holder.mCriticalView.setText(holder.mItem.getCritical());
        holder.mEquippedView.setVisibility(holder.mItem.isEquipped() ? View.VISIBLE : View.INVISIBLE);

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWeaponView(holder.mView.getContext(), holder.mItem);
            }
        });

        holder.mOptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), holder.mOptionsView);
                //inflating menu from xml resource
                popup.inflate(R.menu.character_weapon);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                editWeaponView(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_duplicate:
                                Toast.makeText(holder.mView.getContext(), "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_delete:
                                deleteWeapon(holder.mView.getContext(), holder.mItem);
                                break;
                            case R.id.action_equip:
//                                mCharacter.getWeapons().get(holder.mItem.getUid())
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

    public void addWeapon(String key, CharacterWeapon item) {
        mValues.put(key, item);
        notifyDataSetChanged();
    }

    private void editWeaponView(Context context, CharacterWeapon weapon) {
        if (weaponActionListener != null) {
            weaponActionListener.onWeaponEdit(weapon);
        }
    }

    private void deleteWeapon(final Context context, final CharacterWeapon weapon) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //remove the weapon from Character
                        mCharacter.getWeapons().remove(weapon.getUid());
                        //remove the item from the list
                        mValues.remove(weapon);
                        notifyDataSetChanged();

                        if (weaponActionListener != null) {
                            weaponActionListener.onWeaponDelete(weapon);
                        }

                        Toast.makeText(context, String.format("The item '%s' has been deleted", weapon.getName()),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.ask_remove_weapon)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public void setWeaponActionListener(OnWeaponActionListener listener) {
        this.weaponActionListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mAttackView;
        public final TextView mDamageView;
        public final TextView mCriticalView;
        public final ImageView mEquippedView;
        public final TextView mOptionsView;
        public CharacterWeapon mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mNameView = (TextView) view.findViewById(R.id.nameTextView);
            mAttackView = (TextView) view.findViewById(R.id.attackTextView);
            mDamageView = (TextView) view.findViewById(R.id.damageTextView);
            mCriticalView = (TextView) view.findViewById(R.id.criticalTextView);
            mEquippedView = (ImageView) view.findViewById(R.id.equippedView);
            mOptionsView = (TextView) view.findViewById(R.id.optionsTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }

    }

    public interface OnWeaponActionListener {
        void onWeaponEdit(CharacterWeapon weapon);

        void onWeaponDelete(CharacterWeapon weapon);
    }
}
