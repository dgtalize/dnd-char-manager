package com.dgtalize.dndcharmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterAbility;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterAbilityRecyclerViewAdapter;

import java.util.Map;

/**
 * A fragment representing a list of Items.
 */
public class CharacterAbilityFragment extends CharacterFragment
        implements CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener {

    private Map<String, CharacterAbility> characterAbilities;
    private CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener changeListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterAbilityFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character Character
     * @return A new instance of fragment CharacterHomeFragment.
     */
    public static CharacterAbilityFragment newInstance(Character character) {
        CharacterAbilityFragment fragment = new CharacterAbilityFragment();
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
        View view = inflater.inflate(R.layout.fragment_characterability_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
            loadAbilities();
            recyclerView.setAdapter(new CharacterAbilityRecyclerViewAdapter(characterAbilities, this));
        }
        return view;
    }

    private Map<String, CharacterAbility> loadAbilities() {
        characterAbilities = getCharacter().getAbilities();

        return characterAbilities;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener) {
            changeListener = (CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener) context;
        }
    }

    @Override
    public void onAbilityChange(CharacterAbility characterAbility) {
        if (changeListener != null) {
            changeListener.onAbilityChange(characterAbility);
        }
        notifyCharacterChange();
    }
}
