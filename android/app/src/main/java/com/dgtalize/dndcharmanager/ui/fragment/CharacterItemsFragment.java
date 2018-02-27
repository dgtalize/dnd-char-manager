package com.dgtalize.dndcharmanager.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Item;
import com.dgtalize.dndcharmanager.ui.ItemAddActivity;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterItemRecyclerViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Characters.
 */
public class CharacterItemsFragment extends CharacterFragment {
    private static final String LOG_TAG = "CharacterItemsFragment";

    private static final int ACTIVITY_ITEM_ADD = 100;

    private RecyclerView recyclerView;
    private Map<String, Item> items;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterItemsFragment() {
    }

    public static CharacterItemsFragment newInstance(Character character) {
        CharacterItemsFragment fragment = new CharacterItemsFragment();
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
        View view = inflater.inflate(R.layout.fragment_characteritem_list, container, false);

        FloatingActionButton itemAddFAB = (FloatingActionButton) view.findViewById(R.id.itemAddFab);
        itemAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ItemAddActivity.class);
                startActivityForResult(intent, ACTIVITY_ITEM_ADD);
            }
        });

        // Set the adapter
        View listView = view.findViewById(R.id.list);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            loadItems();
        }
        return view;
    }

    private Map<String, Item> loadItems() {
        items = getCharacter().getItems();

        CharacterItemRecyclerViewAdapter itemsRVAdapter = new CharacterItemRecyclerViewAdapter(items, getCharacter());
        itemsRVAdapter.setItemActionListener(new CharacterItemRecyclerViewAdapter.OnItemActionListener() {
            @Override
            public void onItemEdit(Item armor) {
                Intent intent = new Intent(getActivity(), ItemAddActivity.class);
                intent.putExtra(ItemAddActivity.EXTRA_ITEM, armor);
                startActivityForResult(intent, ACTIVITY_ITEM_ADD);
            }

            @Override
            public void onItemDelete(Item item) {
                notifyCharacterChange();
            }
        });
        recyclerView.setAdapter(itemsRVAdapter);

        return items;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_ITEM_ADD:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Item newItem = (Item) extras.get(ItemAddActivity.EXTRA_ITEM);

                    if (TextUtils.isEmpty(newItem.getUid())) {
                        String newUid = getCharacterDBReference().child("items").push().getKey();
                        newItem.setUid(newUid);
                    }

                    //add the item to the character and to the adapter
                    getCharacter().getItems().put(newItem.getUid(), newItem);
                    ((CharacterItemRecyclerViewAdapter) recyclerView.getAdapter()).addItem(newItem.getUid(), newItem);
                    //notify to the main activity
                    notifyCharacterChange();

                    Toast.makeText(getActivity(), String.format("%s saved", newItem.getName()), Toast.LENGTH_LONG).show();
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
}
