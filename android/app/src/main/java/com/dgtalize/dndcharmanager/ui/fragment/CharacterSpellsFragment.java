package com.dgtalize.dndcharmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Spell;
import com.dgtalize.dndcharmanager.ui.adapter.LevelEntry;
import com.dgtalize.dndcharmanager.ui.adapter.SpellLevelListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterSpellsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterSpellsFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterSpellsFragment";

    private ExpandableListView spellLevelsExpandableList;

    public CharacterSpellsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterSpellsFragment newInstance(Character character) {
        CharacterSpellsFragment fragment = new CharacterSpellsFragment();
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
        View view = inflater.inflate(R.layout.fragment_character_spells, container, false);

        spellLevelsExpandableList = (ExpandableListView) view.findViewById(R.id.spellLevelsExpandableList);


        //fill the view with data from Character
        fillSpells();

        return view;
    }

    private void fillSpells() {
        List<LevelEntry> listDataHeader = new ArrayList<>();
        HashMap<String, List<Spell>> listDataChild = new HashMap<>();
        final SpellLevelListAdapter listAdapter = new SpellLevelListAdapter(getActivity(), listDataHeader, listDataChild, getCharacter());
        // setting list adapter
        spellLevelsExpandableList.setAdapter(listAdapter);

        final String levelString = getString(R.string.level_number);
        DatabaseReference classRef = getDatabase().child("classes").child(getCharacter().getCharClass());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get the Class
                CharClass charClass = dataSnapshot.getValue(CharClass.class);
                charClass.setUid(dataSnapshot.getKey());

                if (charClass.hasSpells()) {
                    List<String> levelKeys = charClass.getSpells().getSortedLevelKeys();
                    for (String levelKey : levelKeys) {
                        int level = ConvertUtils.getLevelFromKey(levelKey);
                        Collection<String> spellsKeys = charClass.getSpells().getLevels().get(levelKey).keySet();
                        LevelEntry levelEntry = new LevelEntry(level, String.format(levelString, level));
                        listAdapter.addGroup(levelEntry);
                        for (String spellKey : spellsKeys) {
                            //add it with the key (for now)
                            Spell tempSpell = new Spell();
                            tempSpell.setUid(spellKey);
                            tempSpell.setName(spellKey);
                            listAdapter.addChild(levelEntry.getNumber(), tempSpell);
                            //now look for the Spell in the database and try to find it
                            DatabaseReference classRef = getDatabase().child("spells").child(spellKey);
                            ValueEventListener spellValueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Spell spell = dataSnapshot.getValue(Spell.class);
                                    if(spell != null) {
                                        spell.setUid(dataSnapshot.getKey());
                                        listAdapter.replaceChild(spell.getUid(), spell);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(LOG_TAG, "loadSpell:onCancelled", databaseError.toException());
                                }
                            };
                            classRef.addListenerForSingleValueEvent(spellValueEventListener);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadClassSpells:onCancelled", databaseError.toException());
            }
        };
        classRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
