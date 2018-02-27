package com.dgtalize.dndcharmanager.ui.fragment;


import android.os.Bundle;

import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.GameActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragments located inside GameActivity
 */

public abstract class GameChildFragment extends RestrictedFragment {
    protected static final String ARG_GAME = "game";

    private Game game;
    private List<OnGameChangeListener> changeListeners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = getArguments().getParcelable(ARG_GAME);
        }
    }

    protected Game getGame() {
        return this.game;
    }

    protected DatabaseReference getGameDBReference() {
        if (getActivity() != null) {
            return ((GameActivity) getActivity()).getGameDBReference();
        } else {
            return getDatabase().child(DatabaseContract.NODE_GAMES).child(getGame().getUid());
        }
    }

    public void refreshGame(Game game) {
        this.game = game;
    }

    //region Game Change listener
    public void addOnGameChangeListener(OnGameChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeOnGameChangeListener(OnGameChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void notifyGameChange() {
        for (OnGameChangeListener listener : this.changeListeners) {
            listener.onGameChange(getGame());
        }
    }

    public interface OnGameChangeListener {
        void onGameChange(Game game);
    }
    //endregion
}
