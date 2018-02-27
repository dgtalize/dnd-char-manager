package com.dgtalize.dndcharmanager.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Feat;
import com.dgtalize.dndcharmanager.ui.FeatAddActivity;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterFeatRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A fragment representing a list of Characters.
 */
public class CharacterFeatsFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterFeatsFrag";

    private static final int ACTIVITY_FEAT_ADD = 100;

    private RecyclerView recyclerView;
    private Map<String, Feat> feats;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterFeatsFragment() {
    }

    public static CharacterFeatsFragment newInstance(Character character) {
        CharacterFeatsFragment fragment = new CharacterFeatsFragment();
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
        View view = inflater.inflate(R.layout.fragment_characterfeat_list, container, false);

        FloatingActionButton addFAB = (FloatingActionButton) view.findViewById(R.id.featAddFab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFeat();
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            loadFeats();
        }
        return view;
    }

    private Map<String, Feat> loadFeats() {
        feats = getCharacter().getFeats();
        CharacterFeatRecyclerViewAdapter characterRVAdapter = new CharacterFeatRecyclerViewAdapter(feats, getCharacter());
        characterRVAdapter.setFeatActionListener(new CharacterFeatRecyclerViewAdapter.OnFeatActionListener() {
            @Override
            public void onFeatDelete(Feat feat) {
                notifyCharacterChange();
            }
        });

        recyclerView.setAdapter(characterRVAdapter);

        return feats;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_FEAT_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Feat newFeat = (Feat) extras.get(FeatAddActivity.EXTRA_FEAT);

                    if (TextUtils.isEmpty(newFeat.getUid())) {
                        String newUid = getCharacterDBReference().child("feats").push().getKey();
                        newFeat.setUid(newUid);
                    }
                    //add the feat to the character and to the adapter
                    getCharacter().getFeats().put(newFeat.getUid(), newFeat);
                    ((CharacterFeatRecyclerViewAdapter) recyclerView.getAdapter()).addFeat(newFeat.getUid(), newFeat);
                    //notify to the main activity
                    notifyCharacterChange();

                    Toast.makeText(getActivity(), String.format("%s saved", newFeat.getName()), Toast.LENGTH_LONG).show();
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

    public void addNewFeat() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
        dialogBuilder.setTitle(R.string.select_feat);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final ArrayAdapter<Feat> arrayAdapter = new ArrayAdapter<Feat>(this.getContext(), android.R.layout.select_dialog_singlechoice);

        DatabaseReference featsReference = getDatabase().child("feats");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Feat feat = ds.getValue(Feat.class);
                    arrayAdapter.add(feat);
                }
                //now everythinig is loaded, then show dialog
                dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Feat feat = arrayAdapter.getItem(which);

                        Intent intent = new Intent(CharacterFeatsFragment.this.getContext(), FeatAddActivity.class);
                        intent.putExtra(FeatAddActivity.EXTRA_FEAT, feat);
                        startActivityForResult(intent, ACTIVITY_FEAT_ADD);
                    }
                });
                if(!CharacterFeatsFragment.this.getActivity().isFinishing()) {
                    dialogBuilder.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadFeats:onCancelled", databaseError.toException());
            }
        };
        featsReference.addListenerForSingleValueEvent(valueEventListener);

    }
}
