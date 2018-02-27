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
import com.dgtalize.dndcharmanager.data.DatabaseContract;
import com.dgtalize.dndcharmanager.model.Game;
import com.dgtalize.dndcharmanager.ui.fragment.GameCharactersFragment;
import com.dgtalize.dndcharmanager.ui.fragment.GameChildFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends BaseRestrictedActivity implements
        GameChildFragment.OnGameChangeListener,
        ValueEventListener {
    private static final String LOG_TAG = "GameActivity";

    public static final String EXTRA_GAME = "GAME";

    private static final int TAB_CHARACTERS = 0;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private GameActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private View notifRefreshingView;
    private MenuItem charModifiedMenuItem;
    private boolean gameModifNotSaved = false;

    private Game game;
    private DatabaseReference gameDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        game = intent.getParcelableExtra(EXTRA_GAME);

        //make sure the Char Class is loaded from the beginning
        showProgressDialog();

        //Set activity title
        GameActivity.this.setTitle(game.getName());
        //prepare for further changes and keep synced
        getGameDBReference().addValueEventListener(GameActivity.this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new GameActivity.SectionsPagerAdapter(getSupportFragmentManager(), GameActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setNestedScrollingEnabled(true);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        hideProgressDialog();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_edit, menu);

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
                Toast.makeText(this, R.string.game_modified_not_saved, Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Prevent closing if the Game was not saved
        if (gameModifNotSaved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.prompt_game_exit_no_save)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GameActivity.super.onBackPressed();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    private void gameRefresh() {

        //show indicator
//        notifRefreshingView.setVisibility(View.VISIBLE);

        //Set activity title
        this.setTitle(game.getName());

        mSectionsPagerAdapter.refreshGame(game, mViewPager.getCurrentItem());

        //hide indicator
//        notifRefreshingView.setVisibility(View.INVISIBLE);
    }

    //region Database management
    private void save() {
        //dettach listener
        getGameDBReference().removeEventListener(this);

        String gameUid = game.getUid();

        //save into database
        getGameDBReference().setValue(game, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //re-attach prepare for further changes and keep synced
                getGameDBReference().addValueEventListener(GameActivity.this);

                gameModifNotSaved = false;
                charModifiedMenuItem.setVisible(false);

                Toast.makeText(GameActivity.this, String.format("%s saved", game.getName()), Toast.LENGTH_LONG).show();
            }
        });

    }

    public DatabaseReference getGameDBReference() {
        if (gameDBRef == null) {
            gameDBRef = getDatabase().child(DatabaseContract.NODE_GAMES).child(game.getUid());
            gameDBRef.keepSynced(true);
        }
        return gameDBRef;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(LOG_TAG, "Game changed in DB.");
        //TODO: Refresh game in fragments based on database change
        //game = dataSnapshot.getValue(Game.class);
        //gameRefresh();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(LOG_TAG, "Listening to Game change cancelled.");
    }
    //endregion

    //region Fragment callbacks
    @Override
    public void onGameChange(Game game) {
        gameModifNotSaved = true;
        charModifiedMenuItem.setVisible(true);
    }
    //endregion

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private GameActivity activity;

        public SectionsPagerAdapter(FragmentManager fm, GameActivity activity) {
            super(fm);
            this.activity = activity;
        }

        @Override
        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return GameActivity.PlaceholderFragment.newInstance(position + 1);
            GameChildFragment fragment = null;

            switch (position) {
                case TAB_CHARACTERS:
                    fragment = GameCharactersFragment.newInstance(game);
                    break;
            }

            fragment.addOnGameChangeListener(this.activity);

            return fragment;
        }

        @Override
        public int getCount() {
            // Show total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_CHARACTERS:
                    return getString(R.string.characters);
            }
            return null;
        }

        public void refreshGame(Game game, int currentItem) {
            GameChildFragment fragment = (GameChildFragment) this.getItem(currentItem);
            fragment.refreshGame(game);
        }
    }
}
