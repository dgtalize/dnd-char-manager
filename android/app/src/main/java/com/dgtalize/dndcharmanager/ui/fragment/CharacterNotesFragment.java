package com.dgtalize.dndcharmanager.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterNotesFragment extends CharacterFragment {

    private EditText notesEditText;

    public CharacterNotesFragment() {
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterNotesFragment newInstance(Character character) {
        CharacterNotesFragment fragment = new CharacterNotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_character_notes, container, false);


        this.notesEditText = (EditText) view.findViewById(R.id.notesEditText);

        initializeValues();

        this.notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getCharacter().setNotes(charSequence.toString());
                notifyCharacterChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void initializeValues() {
        this.notesEditText.setText(getCharacter().getNotes());
    }
}
