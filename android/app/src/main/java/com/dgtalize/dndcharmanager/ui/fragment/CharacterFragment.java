package com.dgtalize.dndcharmanager.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.ui.CharacterActivity;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragments located inside CharacterActivity
 */

public abstract class CharacterFragment extends RestrictedFragment {
    protected static final String ARG_CHARACTER = "character";

    private Character character;
    private List<OnCharacterChangeListener> changeListeners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            character = getArguments().getParcelable(ARG_CHARACTER);
        }
    }

    protected Character getCharacter() {
        return this.character;
    }

    protected DatabaseReference getCharacterDBReference() {
        if (getActivity() != null) {
            return ((CharacterActivity) getActivity()).getCharacterDBReference();
        } else {
            return getDatabase().child("characters").child(getCharacter().getUid());
        }
    }

    public void refreshCharacter(Character character) {
        this.character = character;
    }

    //region Character Change listener
    public void addOnCharacterChangeListener(OnCharacterChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeOnCharacterChangeListener(OnCharacterChangeListener listener) {
        changeListeners.remove(listener);
    }

    protected void notifyCharacterChange() {
        for (OnCharacterChangeListener listener : this.changeListeners) {
            listener.onCharacterChange(getCharacter());
        }
    }

    public interface OnCharacterChangeListener {
        void onCharacterChange(Character character);
    }
    //endregion
}
