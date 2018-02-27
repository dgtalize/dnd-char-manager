package com.dgtalize.dndcharmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.ui.fragment.SkillsListFragment.OnListFragmentInteractionListener;
import com.dgtalize.dndcharmanager.model.Skill;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Skill} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SkillRecyclerViewAdapter extends RecyclerView.Adapter<SkillRecyclerViewAdapter.ViewHolder> {

    private final List<Skill> mValues;
    private final OnListFragmentInteractionListener mListener;

    public SkillRecyclerViewAdapter(List<Skill> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_skill_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mKeyView.setText(mValues.get(position).getKey());
        holder.mNameView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKeyView;
        public final TextView mNameView;
        public Skill mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mKeyView = (TextView) view.findViewById(R.id.skill_key);
            mNameView = (TextView) view.findViewById(R.id.skill_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
