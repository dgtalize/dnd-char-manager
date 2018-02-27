package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharacterArmor;

public class ArmorAddActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "ArmorAddActivity";

    public static final String EXTRA_WEAPON = "armor";

    private EditText nameText;
    private EditText armorBonusEditText;
    private EditText maxDexBonusEditText;
    private EditText weightText;

    CharacterArmor armor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armor_add);

        nameText = (EditText) findViewById(R.id.nameText);
        armorBonusEditText = (EditText) findViewById(R.id.armorBonusEditText);
        maxDexBonusEditText = (EditText) findViewById(R.id.maxDexBonusEditText);
        weightText = (EditText) findViewById(R.id.weightText);

        //initialize armor
        Intent intent = getIntent();
        armor = intent.getParcelableExtra(EXTRA_WEAPON);
        if (armor == null) {
            armor = new CharacterArmor();
        } else {
            //if it's not null then load view
            initializeView(armor);
        }
    }

    private void initializeView(CharacterArmor armor) {
        nameText.setText(armor.getName());
        armorBonusEditText.setText(String.valueOf(armor.getArmorBonus()));
        maxDexBonusEditText.setText(String.valueOf(armor.getMaxDexBonus()));
        weightText.setText(String.valueOf(armor.getWeight()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.armor_add, menu);
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

    /**
     * Validates the form
     *
     * @return A boolean indicating if the form passed validation
     */
    private boolean validateForm() {
        //Name
        nameText.setError(null);
        if (TextUtils.isEmpty(nameText.getText())) {
            nameText.setError(getString(R.string.validation_field_cannot_empty));
            return false;
        }
        //Armor bonus
        if (TextUtils.isEmpty(armorBonusEditText.getText())) {
            armorBonusEditText.setError(getString(R.string.validation_field_cannot_empty));
            return false;
        }
        //Max Dex
        if (TextUtils.isEmpty(maxDexBonusEditText.getText())) {
            maxDexBonusEditText.setError(getString(R.string.validation_field_cannot_empty));
            return false;
        }

        return true;
    }

    private void save() {
        if (validateForm()) {
            armor.setName(nameText.getText().toString());
            armor.setArmorBonus(Integer.valueOf(armorBonusEditText.getText().toString()));
            armor.setMaxDexBonus(Integer.valueOf(maxDexBonusEditText.getText().toString()));
            String weightStr = weightText.getText().toString();
            try {
                armor.setWeight(Double.parseDouble(weightStr));
            } catch (NumberFormatException numberFormatException) {
                armor.setWeight(0);
            }

            //save into database
//        getDatabase().child("characters").child(charUid).child("items").setValue(item);
//        Toast.makeText(this, String.format("%s added", item.getName()), Toast.LENGTH_LONG).show();

            Intent data = new Intent();
            //set the data to pass back
            data.putExtra(EXTRA_WEAPON, armor);
            setResult(RESULT_OK, data);

            this.finish();
        }
    }
}
