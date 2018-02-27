package com.dgtalize.dndcharmanager.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.CharacterAbility;
import com.dgtalize.dndcharmanager.ui.adapter.CharacterAbilityRecyclerViewAdapter;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterAbilityFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterArmorsFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterFeatsFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterHomeFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterItemsFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterMoneyFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterNotesFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterProfileFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterSkillFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterSpellsFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharacterWeaponsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CharacterActivity extends BaseRestrictedActivity implements
        CharacterAbilityRecyclerViewAdapter.OnAbilityChangeListener,
        CharacterFragment.OnCharacterChangeListener,
        ValueEventListener {
    private static final String LOG_TAG = "CharacterActivity";

    public static final String EXTRA_CHARACTER = "CHARACTER";

    private static final int TAB_HOME = 0;
    private static final int TAB_PROFILE = 1;
    private static final int TAB_ABILITIES = 2;
    private static final int TAB_SKILLS = 3;
    private static final int TAB_WEAPONS = 4;
    private static final int TAB_ARMOR = 5;
    private static final int TAB_ITEMS = 6;
    private static final int TAB_SPELLS = 7;
    private static final int TAB_FEATS = 8;
    private static final int TAB_MONEY = 9;
    private static final int TAB_NOTES = 10;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private CharacterActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private View notifRefreshingView;
    private MenuItem charModifiedMenuItem;
    private boolean characterModifNotSaved = false;

    private Character character;
    private DatabaseReference characterDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        Intent intent = getIntent();
        character = intent.getParcelableExtra(EXTRA_CHARACTER);

        //make sure the Char Class is loaded from the beginning
        showProgressDialog();
        character.loadFullClass(new Character.OnCharClassLoadListener() {
            @Override
            public void onCharClassLoaded(CharClass charClass) {
                if (charClass == null) {
                    Toast.makeText(CharacterActivity.this, R.string.error_loading_character, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                //so the screen shows them correctly
                character.recalculateSkillModifiers();
                //Set activity title
                CharacterActivity.this.setTitle(character.getName());
                //prepare for further changes and keep synced
                getCharacterDBReference().addValueEventListener(CharacterActivity.this);

                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                mSectionsPagerAdapter = new CharacterActivity.SectionsPagerAdapter(getSupportFragmentManager(), CharacterActivity.this);

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                //mViewPager.setNestedScrollingEnabled(true);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);

                hideProgressDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.character_edit, menu);

//        notifRefreshingView = menu.findItem(R.id.notif_refreshing).getActionView();
        charModifiedMenuItem = menu.findItem(R.id.notif_modified);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                save();
                return true;
            case R.id.notif_modified:
                Toast.makeText(this, R.string.character_modified_not_saved, Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Prevent closing if the Character was not saved
        if (characterModifNotSaved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.prompt_character_exit_no_save)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CharacterActivity.super.onBackPressed();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void characterRefresh() {

        //show indicator
//        notifRefreshingView.setVisibility(View.VISIBLE);

        //Set activity title
        this.setTitle(character.getName());

        //so the screen shows them correctly
        character.recalculateSkillModifiers();

        mSectionsPagerAdapter.refreshCharacter(character, mViewPager.getCurrentItem());

        //hide indicator
//        notifRefreshingView.setVisibility(View.INVISIBLE);
    }

    //region Database management
    private void save() {
        //dettach listener
        getCharacterDBReference().removeEventListener(this);

        String characterUid = character.getUid();

        //save into database
        getCharacterDBReference().setValue(character, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //re-attach prepare for further changes and keep synced
                getCharacterDBReference().addValueEventListener(CharacterActivity.this);

                characterModifNotSaved = false;
                charModifiedMenuItem.setVisible(false);

                Toast.makeText(CharacterActivity.this, String.format("%s saved", character.getName()), Toast.LENGTH_LONG).show();
            }
        });

    }

    public DatabaseReference getCharacterDBReference() {
        if (characterDBRef == null) {
            characterDBRef = getDatabase().child("characters").child(character.getUid());
            characterDBRef.keepSynced(true);
        }
        return characterDBRef;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(LOG_TAG, "Character changed in DB.");
        //TODO: Refresh character in fragments based on database change
        //character = dataSnapshot.getValue(Character.class);
        //characterRefresh();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(LOG_TAG, "Listening to Character change cancelled.");
    }
    //endregion

    //region Fragment callbacks
    @Override
    public void onCharacterChange(Character character) {
        characterModifNotSaved = true;
        charModifiedMenuItem.setVisible(true);
    }

    @Override
    public void onAbilityChange(CharacterAbility characterAbility) {
        //recalculate modifiers
        character.recalculateSkillModifiers();
    }
    //endregion

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private CharacterActivity activity;

        public SectionsPagerAdapter(FragmentManager fm, CharacterActivity activity) {
            super(fm);
            this.activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return CharacterActivity.PlaceholderFragment.newInstance(position + 1);
            CharacterFragment fragment = null;

            switch (position) {
                case TAB_HOME:
                    fragment = CharacterHomeFragment.newInstance(character);
                    break;
                case TAB_PROFILE:
                    fragment = CharacterProfileFragment.newInstance(character);
                    break;
                case TAB_ABILITIES:
                    fragment = CharacterAbilityFragment.newInstance(character);
                    break;
                case TAB_SKILLS:
                    fragment = CharacterSkillFragment.newInstance(character);
                    break;
                case TAB_WEAPONS:
                    fragment = CharacterWeaponsFragment.newInstance(character);
                    break;
                case TAB_ARMOR:
                    fragment = CharacterArmorsFragment.newInstance(character);
                    break;
                case TAB_ITEMS:
                    fragment = CharacterItemsFragment.newInstance(character);
                    break;
                case TAB_SPELLS:
                    fragment = CharacterSpellsFragment.newInstance(character);
                    break;
                case TAB_FEATS:
                    fragment = CharacterFeatsFragment.newInstance(character);
                    break;
                case TAB_MONEY:
                    fragment = CharacterMoneyFragment.newInstance(character);
                    break;
                case TAB_NOTES:
                    fragment = CharacterNotesFragment.newInstance(character);
                    break;
            }

            fragment.addOnCharacterChangeListener(this.activity);

            return fragment;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 11;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_HOME:
                    return getString(R.string.home);
                case TAB_PROFILE:
                    return getString(R.string.profile);
                case TAB_ABILITIES:
                    return getString(R.string.abilities);
                case TAB_SKILLS:
                    return getString(R.string.skills);
                case TAB_WEAPONS:
                    return getString(R.string.weapons);
                case TAB_ARMOR:
                    return getString(R.string.armor);
                case TAB_ITEMS:
                    return getString(R.string.items);
                case TAB_SPELLS:
                    return getString(R.string.spells);
                case TAB_FEATS:
                    return getString(R.string.feats);
                case TAB_MONEY:
                    return getString(R.string.money);
                case TAB_NOTES:
                    return getString(R.string.notes);
            }
            return null;
        }

        public void refreshCharacter(Character character, int currentItem) {
            CharacterFragment fragment = (CharacterFragment) this.getItem(currentItem);
            fragment.refreshCharacter(character);
        }
    }
}
