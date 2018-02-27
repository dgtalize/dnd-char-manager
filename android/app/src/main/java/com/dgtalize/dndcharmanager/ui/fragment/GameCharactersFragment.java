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
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.GamePlayerInviteActivity;
import com.dgtalize.dndcharmanager.ui.adapter.GameCharacterRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Characters.
 */
public class GameCharactersFragment extends GameChildFragment
        implements GameCharacterRecyclerViewAdapter.OnCharacterChangeListener {
    private static final String LOG_TAG = "GameCharactersFragment";

    private static final int ACTIVITY_CHARACTER_ADD = 100;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Character> items;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameCharactersFragment() {
    }

    public static GameCharactersFragment newInstance(Game game) {
        GameCharactersFragment fragment = new GameCharactersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GAME, game);
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
        View view = inflater.inflate(R.layout.fragment_gamecharacter_list, container, false);

        FloatingActionButton characterAddFab = (FloatingActionButton) view.findViewById(R.id.characterAddFab);
        characterAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GamePlayerInviteActivity.class);
                intent.putExtra(GamePlayerInviteActivity.EXTRA_GAME, getGame());
                startActivityForResult(intent, ACTIVITY_CHARACTER_ADD);
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //initialize adapters
            items = new ArrayList<>();
            GameCharacterRecyclerViewAdapter characterRVAdapter = new GameCharacterRecyclerViewAdapter(items, getGame());
            characterRVAdapter.setChangeListener(this);
            recyclerView.setAdapter(characterRVAdapter);
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

//        ((GameCharacterRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
        DatabaseReference userCharsReference = getDatabase().child(DatabaseContract.NODE_GAMES).child(getGame().getUid())
                .child("characters");
        userCharsReference.keepSynced(true);
        userCharsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot charKeysDS) {
                ((GameCharacterRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
                //loop through the characters KEYs
                for (DataSnapshot charKeyDS : charKeysDS.getChildren()) {
                    //now query for that singlge character and add it to the collection
                    DatabaseReference characterReference = getDatabase().child(DatabaseContract.NODE_CHARACTERS).child(charKeyDS.getKey());
                    characterReference.keepSynced(true);
                    characterReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot characterDS) {
                            Character character = characterDS.getValue(Character.class);
                            if (character != null) {
                                character.setUid(characterDS.getKey());
                                ((GameCharacterRecyclerViewAdapter) recyclerView.getAdapter()).addItem(character);
                            } else {
                                //the character was not found
                                Log.e(LOG_TAG, String.format("Character not found: %s", characterDS.getKey()));
                                //cleanup
                                getDatabase().child(DatabaseContract.NODE_GAMES).child(getGame().getUid())
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
        });

        return items;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CHARACTER_ADD:
                if (resultCode == Activity.RESULT_OK) {
//                    Bundle extras = data.getExtras();
//                    Item newItem = (Item) extras.get(ItemAddActivity.EXTRA_ITEM);
//
//                    if (TextUtils.isEmpty(newItem.getUid())) {
//                        String newUid = getCharacterDBReference().child("items").push().getKey();
//                        newItem.setUid(newUid);
//                    }
//
//                    //add the item to the character and to the adapter
//                    getCharacter().getItems().put(newItem.getUid(), newItem);
//                    ((CharacterItemRecyclerViewAdapter) recyclerView.getAdapter()).addItem(newItem.getUid(), newItem);
//                    //notify to the main activity
//                    notifyCharacterChange();
//
//                    Toast.makeText(getActivity(), String.format("%s saved", newItem.getName()), Toast.LENGTH_LONG).show();

                    Toast.makeText(getActivity(), String.format("Player invited"), Toast.LENGTH_LONG).show();
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

    //region GameCharacterRecyclerViewAdapter.OnCharacterChangeListener
    @Override
    public void onChange(Character character) {

    }

    @Override
    public void onRemove(Character character) {

    }
    //endregion
}
