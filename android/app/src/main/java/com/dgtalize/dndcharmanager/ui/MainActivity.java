package com.dgtalize.dndcharmanager.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Race;
import com.dgtalize.dndcharmanager.model.Skill;
import com.dgtalize.dndcharmanager.service.NotificationService;
import com.dgtalize.dndcharmanager.ui.fragment.AboutFragment;
import com.dgtalize.dndcharmanager.ui.fragment.CharactersListFragment;
import com.dgtalize.dndcharmanager.ui.fragment.ClassesListFragment;
import com.dgtalize.dndcharmanager.ui.fragment.GamesListFragment;
import com.dgtalize.dndcharmanager.ui.fragment.InvitesListFragment;
import com.dgtalize.dndcharmanager.ui.fragment.RacesListFragment;
import com.dgtalize.dndcharmanager.ui.fragment.SkillsListFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseRestrictedActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        ClassesListFragment.OnListFragmentInteractionListener,
        RacesListFragment.OnListFragmentInteractionListener,
        SkillsListFragment.OnListFragmentInteractionListener {

    private static final String LOG_TAG = "MainActivity";

    public static final String ARG_INITIAL_FRAGMENT = "initial_frag";

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private ComponentName notificationService;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);
            ImageView userImageView = (ImageView) headerView.findViewById(R.id.userImageView);
            TextView userNameView = (TextView) headerView.findViewById(R.id.userNameView);
            TextView userEmailView = (TextView) headerView.findViewById(R.id.userEmailView);

            userNameView.setText(user.getDisplayName());
            userEmailView.setText(user.getEmail());
//            Log.d(LOG_TAG, "UserMeta image URL: " + user.getPhotoUrl().toString());
            new ImageLoadTask(user.getPhotoUrl().toString(), userImageView).execute();
        }

        //start the notification listener service
        if (firebaseUser != null) {
            Intent serviceIntent = new Intent(this, NotificationService.class);
            notificationService = startService(serviceIntent);
        }

        //set initial fragment
        Fragment initialFragment = null;
        int initialFragId = this.getIntent().getIntExtra(ARG_INITIAL_FRAGMENT, 0);
        switch (initialFragId) {
            case R.id.nav_invites:
                initialFragment = InvitesListFragment.newInstance();
                setTitle(R.string.invites);
                break;
            default:
                initialFragment = CharactersListFragment.newInstance();
                setTitle(R.string.characters);
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, initialFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;

        // Handle character_navigation view item clicks here.
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_characters:
                fragmentClass = CharactersListFragment.class;
                break;
            case R.id.nav_games:
                fragmentClass = GamesListFragment.class;
                break;
            case R.id.nav_invites:
                fragmentClass = InvitesListFragment.class;
                break;
            case R.id.nav_classes:
                fragmentClass = ClassesListFragment.class;
                break;
            case R.id.nav_skills:
                fragmentClass = SkillsListFragment.class;
                break;
            case R.id.nav_races:
                fragmentClass = RacesListFragment.class;
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Function not implemented yet.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_logout:
                signOut();
                return true;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Set action bar title
        setTitle(menuItem.getTitle());

        // Close the character_navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawers();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.w(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Firebase sign out
                        mAuth.signOut();

                        // Google sign out
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        //close app
                                        MainActivity.this.finish();
                                    }
                                });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_logout)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();

    }


    @Override
    public void onListFragmentInteraction(CharClass item) {
        Log.d(LOG_TAG, "onListFragmentInteraction(CharClass) executed");
    }

    @Override
    public void onListFragmentInteraction(Race item) {
        Log.d(LOG_TAG, "onListFragmentInteraction(Race) executed");
    }

    @Override
    public void onListFragmentInteraction(Skill item) {
        Log.d(LOG_TAG, "onListFragmentInteraction(Skill) executed");
    }
}
