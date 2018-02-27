package com.dgtalize.dndcharmanager.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Race;
import com.dgtalize.dndcharmanager.ui.fragment.RacesListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Race} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RaceRecyclerViewAdapter extends RecyclerView.Adapter<RaceRecyclerViewAdapter.ViewHolder> {

    private final List<Race> mValues;
    private final OnListFragmentInteractionListener mListener;

    public RaceRecyclerViewAdapter(List<Race> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_race_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mSizeView.setText(mValues.get(position).getSize());
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
        public final TextView mSizeView;
        public final TextView mNameView;
        public Race mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSizeView = (TextView) view.findViewById(R.id.race_size);
            mNameView = (TextView) view.findViewById(R.id.race_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
