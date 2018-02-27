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
import com.dgtalize.dndcharmanager.model.CharacterWeapon;
import com.dgtalize.dndcharmanager.model.Weapon;
import com.dgtalize.dndcharmanager.ui.WeaponAddActivity;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterWeaponRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A fragment representing a list of Characters.
 */
public class CharacterWeaponsFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterWeaponsFrag";

    private static final int ACTIVITY_WEAPON_ADD = 100;

    private RecyclerView recyclerView;
    private Map<String, CharacterWeapon> weapons;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterWeaponsFragment() {
    }

    public static CharacterWeaponsFragment newInstance(Character character) {
        CharacterWeaponsFragment fragment = new CharacterWeaponsFragment();
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
        View view = inflater.inflate(R.layout.fragment_characterweapon_list, container, false);

        FloatingActionButton addFAB = (FloatingActionButton) view.findViewById(R.id.weaponAddFab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewWeapon();
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            loadWeapons();
        }
        return view;
    }

    private Map<String, CharacterWeapon> loadWeapons() {
        weapons = getCharacter().getWeapons();
        CharacterWeaponRecyclerViewAdapter characterRVAdapter = new CharacterWeaponRecyclerViewAdapter(weapons, getCharacter());
        characterRVAdapter.setWeaponActionListener(new CharacterWeaponRecyclerViewAdapter.OnWeaponActionListener() {
            @Override
            public void onWeaponEdit(CharacterWeapon weapon) {
                Intent intent = new Intent(getActivity(), WeaponAddActivity.class);
                intent.putExtra(WeaponAddActivity.EXTRA_WEAPON, weapon);
                startActivityForResult(intent, ACTIVITY_WEAPON_ADD);
            }

            @Override
            public void onWeaponDelete(CharacterWeapon weapon) {
                notifyCharacterChange();
            }
        });

        recyclerView.setAdapter(characterRVAdapter);

        return weapons;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_WEAPON_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    CharacterWeapon newWeapon = (CharacterWeapon) extras.get(WeaponAddActivity.EXTRA_WEAPON);

                    if (TextUtils.isEmpty(newWeapon.getUid())) {
                        String newUid = getCharacterDBReference().child("weapons").push().getKey();
                        newWeapon.setUid(newUid);
                    }
//                    getCharacterDBReference().child("weapons").child(newUid).setValue(newWeapon);
                    //add the weapon to the character and to the adapter
                    getCharacter().getWeapons().put(newWeapon.getUid(), newWeapon);
                    ((CharacterWeaponRecyclerViewAdapter) recyclerView.getAdapter()).addWeapon(newWeapon.getUid(), newWeapon);
                    //notify to the main activity
                    notifyCharacterChange();

                    Toast.makeText(getActivity(), String.format("%s saved", newWeapon.getName()), Toast.LENGTH_LONG).show();
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

    public void addNewWeapon() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
        dialogBuilder.setTitle(R.string.select_weapon);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final ArrayAdapter<Weapon> arrayAdapter = new ArrayAdapter<Weapon>(this.getContext(), android.R.layout.select_dialog_singlechoice);

        DatabaseReference weaponsReference = getDatabase().child("weapons");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Weapon weapon = ds.getValue(Weapon.class);
                    arrayAdapter.add(weapon);
                }
                //now everythinig is loaded, then show dialog
                dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Weapon weaponBase = arrayAdapter.getItem(which);
                        CharacterWeapon weapon = new CharacterWeapon(weaponBase);

                        Intent intent = new Intent(CharacterWeaponsFragment.this.getContext(), WeaponAddActivity.class);
                        intent.putExtra(WeaponAddActivity.EXTRA_WEAPON, weapon);
                        startActivityForResult(intent, ACTIVITY_WEAPON_ADD);
                    }
                });
                dialogBuilder.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadWeapons:onCancelled", databaseError.toException());
            }
        };
        weaponsReference.addListenerForSingleValueEvent(valueEventListener);
    }
}
