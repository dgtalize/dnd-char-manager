package com.dgtalize.dndcharmanager.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterSkill;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterSkillRecyclerViewAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Skills of the Character.
 */
public class CharacterSkillFragment extends CharacterFragment implements
        CharacterSkillRecyclerViewAdapter.OnSkillChangeListener,
        SearchView.OnQueryTextListener {
    private static final String LOG_TAG = "CharacterSkillFragment";

    private Map<String, CharacterSkill> characterSkills;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterSkillFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterSkillFragment newInstance(Character character) {
        CharacterSkillFragment fragment = new CharacterSkillFragment();
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
        View view = inflater.inflate(R.layout.fragment_characterskill_list, container, false);

        setHasOptionsMenu(true);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            LinearLayoutManager layourManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layourManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, layourManager.getOrientation()));
            loadSkills(null);
            recyclerView.setAdapter(new CharacterSkillRecyclerViewAdapter(characterSkills, this, getCharacter()));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.character_skill_list, menu);

        try {
            MenuItem item = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setOnQueryTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private Map<String, CharacterSkill> loadSkills(String searchQuery) {
//        characterSkills = getCharacter().getSkills();
        characterSkills = new HashMap<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            for (Map.Entry<String, CharacterSkill> charSkillEntry : getCharacter().getSkills().entrySet()) {
                //search if Skill name contains query (lowercase)
                if (charSkillEntry.getValue().getSkillName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    characterSkills.put(charSkillEntry.getKey(), charSkillEntry.getValue());
                }
            }
        } else {
            characterSkills = getCharacter().getSkills();
        }

        return characterSkills;
    }

    @Override
    public void onSkillChange(CharacterSkill characterSkill) {
        notifyCharacterChange();
    }

    //region Search events
    @Override
    public boolean onQueryTextSubmit(String query) {
//        loadSkills(query);
//        recyclerView.setAdapter(new CharacterSkillRecyclerViewAdapter(characterSkills, this, getCharacter()));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        loadSkills(newText);
        recyclerView.setAdapter(new CharacterSkillRecyclerViewAdapter(characterSkills, this, getCharacter()));
        return true;
    }
    //endregion
}
