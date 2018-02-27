package com.dgtalize.dndcharmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
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
import com.dgtalize.dndcharmanager.ui.adapter.GameInvitationRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Games.
 */
public class InvitesListFragment extends RestrictedFragment {
    private static final String LOG_TAG = "GamesListFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Game> games;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InvitesListFragment() {
    }

    public static InvitesListFragment newInstance() {
        InvitesListFragment fragment = new InvitesListFragment();
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
        View view = inflater.inflate(R.layout.fragment_gameinvite_list, container, false);

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //initialize adapters
            games = new ArrayList<>();
            GameInvitationRecyclerViewAdapter gameRVAdapter = new GameInvitationRecyclerViewAdapter(games);
            recyclerView.setAdapter(gameRVAdapter);
            //load items into adapter
            loadInvitations();
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadInvitations();
            }
        });

        return view;
    }

    private List<Game> loadInvitations() {
        showProgressDialog();

        ((GameInvitationRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
        Query invitationsQuery = getDatabase().child(DatabaseContract.NODE_GAMEINVITES)
                .orderByChild(getFirebaseUser().getUid()).equalTo(true);
        invitationsQuery.keepSynced(true);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gameKeysDS) {
                ((GameInvitationRecyclerViewAdapter) recyclerView.getAdapter()).clearItems();
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
                                ((GameInvitationRecyclerViewAdapter) recyclerView.getAdapter()).addItem(game);
                            } else {
                                //the game was not found
                                Log.e(LOG_TAG, String.format("Game not found: %s", gameDS.getKey()));
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
                Log.w(LOG_TAG, "loadInvitations:onCancelled", databaseError.toException());
            }
        };
        invitationsQuery.addListenerForSingleValueEvent(valueEventListener);

        return games;
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
