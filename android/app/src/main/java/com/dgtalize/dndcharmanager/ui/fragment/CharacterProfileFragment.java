package com.dgtalize.dndcharmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Alignment;
import com.dgtalize.dndcharmanager.model.Character;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterProfileFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterProfileFgmt";

    private TextView raceTextView;
    private TextView classTextView;
    private Spinner alignSpinner;

    private Boolean alignSpinnerFirstChange = true;

    public CharacterProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterProfileFragment newInstance(Character character) {
        CharacterProfileFragment fragment = new CharacterProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARACTER, character);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_character_profile, container, false);

        raceTextView = (TextView) view.findViewById(R.id.raceTextView);
        classTextView = (TextView) view.findViewById(R.id.classTextView);
        alignSpinner = (Spinner) view.findViewById(R.id.alignSpinner);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadAlignmentSpinner(new SpinnerLoadListener() {
            @Override
            public void onLoaded() {
                //fill the view with data from Character
                fillCharacterData();

                //events
                alignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Alignment alignment = (Alignment) alignSpinner.getSelectedItem();

                        if (!alignment.getUid().equals(getCharacter().getAlignmentUid())) {
                            getCharacter().setAlignment(alignment);

                            CharacterProfileFragment.this.notifyCharacterChange();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    private void fillCharacterData() {
        raceTextView.setText(getCharacter().getRace());
        classTextView.setText(getCharacter().getCharClass());

        if (getCharacter().getAlignmentUid() != null) {
            int indexToSelect = 0;
            for (int i = 0; i < alignSpinner.getAdapter().getCount(); i++) {
                Alignment alignment = (Alignment) alignSpinner.getAdapter().getItem(i);
                if (alignment.getUid().equals(getCharacter().getAlignmentUid())) {
                    indexToSelect = i;
                    break;
                }
            }
            alignSpinner.setSelection(indexToSelect);
        }
    }

    private void loadAlignmentSpinner(final SpinnerLoadListener listener) {
        //--- Race spinner
        final ArrayList<Alignment> alignments = new ArrayList<>();
        DatabaseReference alignmentsRef = getDatabase().child(DatabaseContract.NODE_ALIGNMENTS);
        alignmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Alignment alignment = ds.getValue(Alignment.class);
                    alignment.setUid(ds.getKey());
                    alignments.add(alignment);
                }

                ArrayAdapter<Alignment> alignAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, alignments);
                alignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Specify the layout to use when the list of choices appears
                alignAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                alignSpinner.setAdapter(alignAdapter);

                if (listener != null) {
                    listener.onLoaded();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadAligns:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private interface SpinnerLoadListener {
        public void onLoaded();
    }
}
