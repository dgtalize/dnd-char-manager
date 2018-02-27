package com.dgtalize.dndcharmanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Race;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CharacterCreateActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "CharacterCreateActivity";

    public static final String EXTRA_CHARACTER = "character";

    private ArrayList<Race> races;
    private ArrayList<CharClass> classes;

    private TextView nameText;
    private Spinner raceSpinner;
    private Spinner classSpinner;
    private Boolean racesLoaded;
    private Boolean classesLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_create);

        nameText = (TextView) findViewById(R.id.nameText);
        raceSpinner = (Spinner) findViewById(R.id.raceSpinner);
        classSpinner = (Spinner) findViewById(R.id.classSpinner);

        loadSpinners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.character_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadSpinners() {
        //prepare loading status
        racesLoaded = false;
        classesLoaded = false;
        showProgressDialog();
        //start loading
        loadRaceSpinner();
        loadClassSpinner();
    }

    /**
     * Hides the progress dialog if the spinners loaded
     */
    private void restoreSpinnersLoadStatus() {
        if (racesLoaded && classesLoaded) {
            hideProgressDialog();
        }
    }

    private void loadRaceSpinner() {
        //--- Race spinner
        races = new ArrayList<>();
        DatabaseReference racesReference = getDatabase().child("races");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Race race = ds.getValue(Race.class);
                    race.setUid(ds.getKey());
                    races.add(race);
                }

                ArrayAdapter<Race> raceAdapter = new ArrayAdapter<Race>(CharacterCreateActivity.this, android.R.layout.simple_spinner_dropdown_item, races);
                raceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Specify the layout to use when the list of choices appears
                raceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                raceSpinner.setAdapter(raceAdapter);

                racesLoaded = true;
                restoreSpinnersLoadStatus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadRaces:onCancelled", databaseError.toException());
            }
        };
        racesReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void loadClassSpinner() {
        //--- Race spinner
        classes = new ArrayList<>();
        DatabaseReference classesReference = getDatabase().child("classes");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CharClass charClass = ds.getValue(CharClass.class);
                    charClass.setUid(ds.getKey());
                    classes.add(charClass);
                }

                ArrayAdapter<CharClass> classAdapter = new ArrayAdapter<CharClass>(CharacterCreateActivity.this, android.R.layout.simple_spinner_dropdown_item, classes);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Specify the layout to use when the list of choices appears
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                classSpinner.setAdapter(classAdapter);

                classesLoaded = true;
                restoreSpinnersLoadStatus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadClasses:onCancelled", databaseError.toException());
            }
        };
        classesReference.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Validates the form
     * @return A boolean indicating if the form passed validation
     */
    private boolean validateForm() {
        //Name
        nameText.setError(null);
        if (TextUtils.isEmpty(nameText.getText())) {
            nameText.setError(getString(R.string.validation_field_cannot_empty));
            return false;
        }

        return true;
    }

    private void save() {
        if (validateForm()) {
            //show loading
            showProgressDialog();

            Race selectedRace = (Race) raceSpinner.getSelectedItem();
            CharClass selectedClass = (CharClass) classSpinner.getSelectedItem();

            String newUid = getDatabase().child("characters").push().getKey();

            final Character character = new Character(newUid);
            character.setName(nameText.getText().toString());
            character.setRace(selectedRace.getUid());
            character.setCharClass(selectedClass.getUid());
            character.setOwner(firebaseUser.getUid());
            //initialize its skills
            character.initializeSkills(new Character.OnCharacterLoadListener() {
                @Override
                public void onSkillsInitialized() {
                    //save into database
                    getDatabase().child("characters").child(character.getUid()).setValue(character, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //once the Character was saved
                            getDatabase().child("users").child(firebaseUser.getUid()).child("characters").child(character.getUid()).setValue(true);

                            Toast.makeText(CharacterCreateActivity.this, String.format("%s saved", character.getName()), Toast.LENGTH_LONG).show();

                            //hide loading
                            hideProgressDialog();

                            Intent data = new Intent();
                            //set the data to pass back
                            data.putExtra(EXTRA_CHARACTER, character);
                            CharacterCreateActivity.this.setResult(Activity.RESULT_OK, data);
                            CharacterCreateActivity.this.finish();
                        }
                    });
                }
            });
        }//finish validation
    }
}
