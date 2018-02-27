package com.dgtalize.dndcharmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterSkill;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterSkillFragment;

import java.util.Map;
import java.util.TreeMap;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CharacterSkill} and makes a call to the
 * specified {@link CharacterSkillRecyclerViewAdapter.OnSkillChangeListener}.
 */
public class CharacterSkillRecyclerViewAdapter extends RecyclerView.Adapter<CharacterSkillRecyclerViewAdapter.ViewHolder> {
    private static final String LOG_TAG = "CharacterSkillRVAdapter";

    private Character mCharacter;
    private final TreeMap<String, CharacterSkill> mValues;
    private final CharacterSkillRecyclerViewAdapter.OnSkillChangeListener mListener;

    public CharacterSkillRecyclerViewAdapter(Map<String, CharacterSkill> items, CharacterSkillRecyclerViewAdapter.OnSkillChangeListener listener, Character character) {
        mValues = new TreeMap<>(items);
        mListener = listener;
        mCharacter = character;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_characterskill_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object[] keys = mValues.keySet().toArray();
        String key = (String) keys[position];

        holder.mItem = mValues.get(key);
        holder.mSkillView.setText(holder.mItem.getSkillName());
        holder.mAbilityModifierTextView.setText(String.valueOf(holder.mItem.getAbilityModifier()));
        holder.mRankEdit.setText(String.valueOf(holder.mItem.getRank()));
        holder.mMiscModifierEdit.setText(String.valueOf(holder.mItem.getMiscModifier()));
        holder.mModifierView.setText(String.valueOf(holder.mItem.getModifier()));
        holder.mClassIndicatorView.setVisibility(
                mCharacter.isClassSkill(holder.mItem.getSkill()) ? View.VISIBLE : View.INVISIBLE);

        //modifying the rank
        holder.mRankEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ConvertUtils.isPoints(charSequence)) {
                    holder.mItem.setRank(Integer.parseInt(charSequence.toString()));
                    holder.mModifierView.setText(String.valueOf(holder.mItem.getModifier()));
                    //Notify that the skill changed
                    mListener.onSkillChange(holder.mItem);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //modifying the misc modifiers
        holder.mMiscModifierEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ConvertUtils.isPoints(charSequence)) {
                    holder.mItem.setMiscModifier(Integer.parseInt(charSequence.toString()));
                    holder.mModifierView.setText(String.valueOf(holder.mItem.getModifier()));
                    //Notify that the skill changed
                    mListener.onSkillChange(holder.mItem);
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
        public final TextView mSkillView;
        public final TextView mClassIndicatorView;
        public final TextView mAbilityModifierTextView;
        public final EditText mRankEdit;
        public final EditText mMiscModifierEdit;
        public final TextView mModifierView;
        public CharacterSkill mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSkillView = (TextView) view.findViewById(R.id.skillTextView);
            mClassIndicatorView = (TextView) view.findViewById(R.id.classIndicatorTextView);
            mAbilityModifierTextView = (TextView) view.findViewById(R.id.abilityModifierTextView);
            mRankEdit = (EditText) view.findViewById(R.id.rankEditText);
            mMiscModifierEdit = (EditText) view.findViewById(R.id.miscModifierEditText);
            mModifierView = (TextView) view.findViewById(R.id.modifierTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSkillView.getText() + "'";
        }
    }

    /**
     * The skill has changed
     */
    public interface OnSkillChangeListener {
        void onSkillChange(CharacterSkill characterSkill);
    }
}
