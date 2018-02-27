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
import com.dgtalize.dndcharmanager.model.Armor;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterArmor;
import com.dgtalize.dndcharmanager.ui.ArmorAddActivity;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterArmorRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A fragment representing a list of Characters.
 */
public class CharacterArmorsFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterArmorsFrag";

    private static final int ACTIVITY_ARMOR_ADD = 100;

    private RecyclerView recyclerView;
    private Map<String, CharacterArmor> armors;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterArmorsFragment() {
    }

    public static CharacterArmorsFragment newInstance(Character character) {
        CharacterArmorsFragment fragment = new CharacterArmorsFragment();
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
        View view = inflater.inflate(R.layout.fragment_characterarmor_list, container, false);

        FloatingActionButton addFAB = (FloatingActionButton) view.findViewById(R.id.armorAddFab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewArmor();
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            loadArmors();
        }
        return view;
    }

    private Map<String, CharacterArmor> loadArmors() {
        armors = getCharacter().getArmors();
        CharacterArmorRecyclerViewAdapter characterRVAdapter = new CharacterArmorRecyclerViewAdapter(armors, getCharacter());
        characterRVAdapter.setArmorActionListener(new CharacterArmorRecyclerViewAdapter.OnArmorActionListener() {
            @Override
            public void onArmorEdit(CharacterArmor armor) {
                Intent intent = new Intent(getActivity(), ArmorAddActivity.class);
                intent.putExtra(ArmorAddActivity.EXTRA_WEAPON, armor);
                startActivityForResult(intent, ACTIVITY_ARMOR_ADD);
            }

            @Override
            public void onArmorDelete(CharacterArmor armor) {
                notifyCharacterChange();
            }
        });

        recyclerView.setAdapter(characterRVAdapter);

        return armors;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_ARMOR_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    CharacterArmor newArmor = (CharacterArmor) extras.get(ArmorAddActivity.EXTRA_WEAPON);

                    if (TextUtils.isEmpty(newArmor.getUid())) {
                        String newUid = getCharacterDBReference().child("armors").push().getKey();
                        newArmor.setUid(newUid);
                    }
                    //add the armor to the character and to the adapter
                    getCharacter().getArmors().put(newArmor.getUid(), newArmor);
                    ((CharacterArmorRecyclerViewAdapter) recyclerView.getAdapter()).addArmor(newArmor.getUid(), newArmor);
                    //notify to the main activity
                    notifyCharacterChange();

                    Toast.makeText(getActivity(), String.format("%s saved", newArmor.getName()), Toast.LENGTH_LONG).show();
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

    public void addNewArmor() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
        dialogBuilder.setTitle(R.string.select_armor);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final ArrayAdapter<Armor> arrayAdapter = new ArrayAdapter<Armor>(this.getContext(), android.R.layout.select_dialog_singlechoice);

        DatabaseReference armorsReference = getDatabase().child("armors");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Armor weapon = ds.getValue(Armor.class);
                    arrayAdapter.add(weapon);
                }
                //now everythinig is loaded, then show dialog
                dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Armor armorBase = arrayAdapter.getItem(which);
                        CharacterArmor armor = new CharacterArmor(armorBase);

                        Intent intent = new Intent(CharacterArmorsFragment.this.getContext(), ArmorAddActivity.class);
                        intent.putExtra(ArmorAddActivity.EXTRA_WEAPON, armor);
                        startActivityForResult(intent, ACTIVITY_ARMOR_ADD);
                    }
                });
                dialogBuilder.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadArmors:onCancelled", databaseError.toException());
            }
        };
        armorsReference.addListenerForSingleValueEvent(valueEventListener);
    }
}
