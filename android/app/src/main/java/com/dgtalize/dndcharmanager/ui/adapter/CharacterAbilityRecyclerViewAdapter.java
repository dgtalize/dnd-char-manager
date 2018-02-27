package com.dgtalize.dndcharmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Ability;
import com.dgtalize.dndcharmanager.model.CharacterAbility;

import java.util.Map;
import java.util.TreeMap;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CharacterAbility} and makes a call to the
 * specified {@link CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener}.
 */
public class CharacterAbilityRecyclerViewAdapter extends RecyclerView.Adapter<CharacterAbilityRecyclerViewAdapter.ViewHolder> {

    private final TreeMap<String, CharacterAbility> mValues;
    private final CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener mListener;

    public CharacterAbilityRecyclerViewAdapter(Map<String, CharacterAbility> items, CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener listener) {
        mValues = new TreeMap<>(new Ability.Comparator());
        mValues.putAll(items);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characterability_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mAbilityView.setText(holder.mItem.getAbility());
        holder.mScoreView.setText(String.valueOf(holder.mItem.getScore()));
        holder.mModifierView.setText(String.valueOf(holder.mItem.getModifier()));
        holder.mTemporaryScoreView.setText(String.valueOf(holder.mItem.getTemporaryScore()));
        holder.mActiveModifierView.setText(String.valueOf(holder.mItem.getActiveModifier()));

        //modifying the score
        holder.mScoreView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ConvertUtils.isPoints(charSequence)) {
                    holder.mItem.setScore(Integer.parseInt(charSequence.toString()));
                    holder.mModifierView.setText(String.valueOf(holder.mItem.getModifier()));
                    holder.mActiveModifierView.setText(String.valueOf(holder.mItem.getActiveModifier()));
                    // Notify the active callbacks interface that a score changed
                    mListener.onAbilityChange(holder.mItem);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //modifying the temporary score
        holder.mTemporaryScoreView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (ConvertUtils.isPoints(charSequence)) {
                    holder.mItem.setTemporaryScore(Integer.parseInt(charSequence.toString()));
                    holder.mActiveModifierView.setText(String.valueOf(holder.mItem.getActiveModifier()));
                    // Notify the active callbacks interface that a score changed
                    mListener.onAbilityChange(holder.mItem);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAbilityView;
        public final EditText mScoreView;
        public final TextView mModifierView;
        public final EditText mTemporaryScoreView;
        public final TextView mActiveModifierView;
        public CharacterAbility mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAbilityView = (TextView) view.findViewById(R.id.abilityTextView);
            mScoreView = (EditText) view.findViewById(R.id.scoreEditText);
            mModifierView = (TextView) view.findViewById(R.id.modifierTextView);
            mTemporaryScoreView = (EditText) view.findViewById(R.id.temporaryScoreEditText);
            mActiveModifierView = (TextView) view.findViewById(R.id.activeModifierTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAbilityView.getText() + "'";
        }
    }


    /**
     * The skill has changed
     */
    public interface OnAbilityChangeListener {
        void onAbilityChange(CharacterAbility characterAbility);
    }
}
