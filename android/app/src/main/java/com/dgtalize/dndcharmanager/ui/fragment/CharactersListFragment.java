package com.dgtalize.dndcharmanager.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.ui.CharacterCreateActivity;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Characters.
 */
public class CharactersListFragment extends RestrictedFragment implements
        CharacterRecyclerViewAdapter.OnCharacterChangeListener {
    private static final String LOG_TAG = "CharactersListFragment";

    private static final int ACTIVITY_CREATE_CHARACTER = 100;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Character> characters;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharactersListFragment() {
    }

    public static CharactersListFragment newInstance() {
        CharactersListFragment fragment = new CharactersListFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_character_list, container, false);

        FloatingActionButton charAddFAB = (FloatingActionButton) view.findViewById(R.id.characterAddFab);
        charAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), CharacterCreateActivity.class);
                startActivityForResult(intent, ACTIVITY_CREATE_CHARACTER);
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //initialize adapters
            characters = new ArrayList<>();
            CharacterRecyclerViewAdapter characterRVAdapter = new CharacterRecyclerViewAdapter(characters);
            characterRVAdapter.setChangeListener(this);
            recyclerView.setAdapter(characterRVAdapter);
            //load items into adapter
            loadCharacters();
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCharacters();
            }
        });

        return view;
    }

    private List<Character> loadCharacters() {
        showProgressDialog();

        ((CharacterRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
        DatabaseReference userCharsReference = getDatabase().child("users").child(getFirebaseUser().getUid()).child("characters");
        userCharsReference.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot charKeysDS) {
                ((CharacterRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
                //loop through the characters KEYs
                for (DataSnapshot charKeyDS : charKeysDS.getChildren()) {
                    //now query for that singlge character and add it to the collection
                    DatabaseReference characterReference = getDatabase().child("characters").child(charKeyDS.getKey());
                    characterReference.keepSynced(true);
                    characterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot characterDS) {
                            Character character = characterDS.getValue(Character.class);
                            if (character != null) {
                                character.setUid(characterDS.getKey());
                                ((CharacterRecyclerViewAdapter) recyclerView.getAdapter()).addItem(character);
                            } else {
                                //the character was not found
                                Log.e(LOG_TAG, String.format("Character not found: %s", characterDS.getKey()));
                                //cleanup
                                getDatabase().child("users").child(getFirebaseUser().getUid())
                                        .child("characters").child(characterDS.getKey()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(LOG_TAG, "loadCharacter:onCancelled", databaseError.toException());
                        }
                    });

                }

                hideProgressDialog();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadCharacters:onCancelled", databaseError.toException());
            }
        };
        userCharsReference.addListenerForSingleValueEvent(valueEventListener);

        return characters;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_CREATE_CHARACTER:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Character newCharacter = (Character) extras.get(CharacterCreateActivity.EXTRA_CHARACTER);
                    ((CharacterRecyclerViewAdapter) recyclerView.getAdapter()).addItem(newCharacter);
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onChange(Character character) {
        loadCharacters();
    }

    @Override
    public void onDelete(Character character) {
        loadCharacters();
    }
}
