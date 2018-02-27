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
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.GameCreateActivity;
import com.dgtalize.dndcharmanager.ui.adapter.GameRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Games.
 */
public class GamesListFragment extends RestrictedFragment implements
        GameRecyclerViewAdapter.OnGameChangeListener {
    private static final String LOG_TAG = "GamesListFragment";

    private static final int ACTIVITY_CREATE_GAME = 100;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Game> games;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GamesListFragment() {
    }

    public static GamesListFragment newInstance() {
        GamesListFragment fragment = new GamesListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        FloatingActionButton gameAddFAB = (FloatingActionButton) view.findViewById(R.id.gameAddFab);
        gameAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), GameCreateActivity.class);
                startActivityForResult(intent, ACTIVITY_CREATE_GAME);
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //initialize adapters
            games = new ArrayList<>();
            GameRecyclerViewAdapter gameRVAdapter = new GameRecyclerViewAdapter(games);
            gameRVAdapter.setChangeListener(this);
            recyclerView.setAdapter(gameRVAdapter);
            //load items into adapter
            loadGames();
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGames();
            }
        });

        return view;
    }

    private List<Game> loadGames() {
        showProgressDialog();

        ((GameRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
        DatabaseReference userGamesReference = getDatabase().child("users").child(getFirebaseUser().getUid()).child("games");
        userGamesReference.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameKeysDS) {
                ((GameRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
                //loop through the games KEYs
                for (DataSnapshot gameKeyDS : gameKeysDS.getChildren()) {
                    //now query for that singlge game and add it to the collection
                    DatabaseReference gameReference = getDatabase().child(DatabaseContract.NODE_GAMES).child(gameKeyDS.getKey());
                    gameReference.keepSynced(true);
                    gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot gameDS) {
                            Game game = gameDS.getValue(Game.class);
                            if (game != null) {
                                game.setUid(gameDS.getKey());
                                ((GameRecyclerViewAdapter) recyclerView.getAdapter()).addItem(game);
                            } else {
                                //the game was not found
                                Log.e(LOG_TAG, String.format("Game not found: %s", gameDS.getKey()));
                                //cleanup
                                getDatabase().child("users").child(getFirebaseUser().getUid())
                                        .child("games").child(gameDS.getKey()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(LOG_TAG, "loadGame:onCancelled", databaseError.toException());
                        }
                    });

                }

                hideProgressDialog();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadGames:onCancelled", databaseError.toException());
            }
        };
        userGamesReference.addListenerForSingleValueEvent(valueEventListener);

        return games;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_CREATE_GAME:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Game newGame = (Game) extras.get(GameCreateActivity.EXTRA_GAME);
                    ((GameRecyclerViewAdapter) recyclerView.getAdapter()).addItem(newGame);
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
    public void onChange(Game game) {
        loadGames();
    }

    @Override
    public void onDelete(Game game) {
        loadGames();
    }
}
