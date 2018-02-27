package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharacterWeapon;

public class WeaponAddActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "WeaponAddActivity";

    public static final String EXTRA_WEAPON = "weapon";

    private EditText nameText;
    private AutoCompleteTextView damageSmallTextEdit;
    private AutoCompleteTextView damageMediumTextEdit;
    private AutoCompleteTextView criticalEditText;
    private EditText weightText;
    private CheckBox rangedCheckBox;

    CharacterWeapon weapon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_add);

        nameText = (EditText) findViewById(R.id.nameText);
        damageSmallTextEdit = (AutoCompleteTextView) findViewById(R.id.damageSmallTextEdit);
        damageMediumTextEdit = (AutoCompleteTextView) findViewById(R.id.damageMediumTextEdit);
        criticalEditText = (AutoCompleteTextView) findViewById(R.id.criticalEditText);
        weightText = (EditText) findViewById(R.id.weightText);
        rangedCheckBox = (CheckBox) findViewById(R.id.rangedCheckBox);

        //damage suggestions
        ArrayAdapter<String> dmgAdapterS = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.damage));
        damageSmallTextEdit.setAdapter(dmgAdapterS);
        ArrayAdapter<String> dmgAdapterM = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.damage));
        damageMediumTextEdit.setAdapter(dmgAdapterM);
        //critical suggestions
        ArrayAdapter<String> critAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.critical));
        criticalEditText.setAdapter(critAdapter);

        //initialize weapon
        Intent intent = getIntent();
        weapon = intent.getParcelableExtra(EXTRA_WEAPON);
        if (weapon == null) {
            weapon = new CharacterWeapon();
        } else {
            //if it's not null then load view
            initializeView(weapon);
        }
    }

    private void initializeView(CharacterWeapon weapon) {
        nameText.setText(weapon.getName());
        damageSmallTextEdit.setText(weapon.getDamageSmall());
        damageMediumTextEdit.setText(weapon.getDamageMedium());
        criticalEditText.setText(weapon.getCritical());
        weightText.setText(String.valueOf(weapon.getWeight()));
        rangedCheckBox.setChecked(weapon.isRanged());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weapon_add, menu);
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

    private void save() {
        weapon.setName(nameText.getText().toString());
        weapon.setDamageSmall(damageSmallTextEdit.getText().toString());
        weapon.setDamageMedium(damageMediumTextEdit.getText().toString());
        weapon.setCritical(criticalEditText.getText().toString());
        weapon.setRanged(rangedCheckBox.isChecked());
        String weightStr = weightText.getText().toString();
        try {
            weapon.setWeight(Double.parseDouble(weightStr));
        } catch (NumberFormatException numberFormatException) {
            weapon.setWeight(0);
        }

        //save into database
//        getDatabase().child("characters").child(charUid).child("items").setValue(item);
//        Toast.makeText(this, String.format("%s added", item.getName()), Toast.LENGTH_LONG).show();

        Intent data = new Intent();
        //set the data to pass back
        data.putExtra(EXTRA_WEAPON, weapon);
        setResult(RESULT_OK, data);

        this.finish();
    }
}
