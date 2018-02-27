package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.CharClass;
import com.dgtalize.dndcharmanager.model.Character;
import com.dgtalize.dndcharmanager.model.Item;
import com.dgtalize.dndcharmanager.model.Race;

public class ItemAddActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "ItemAddActivity";

    public static final String EXTRA_ITEM = "item";

    private EditText nameText;
    private EditText weightText;
    private CheckBox magicalCheckBox;
    private EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        nameText = (EditText) findViewById(R.id.nameText);
        weightText = (EditText) findViewById(R.id.weightText);
        magicalCheckBox = (CheckBox) findViewById(R.id.magicalCheckBox);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_add, menu);
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

        return true;
    }

    private void save() {
        if (validateForm()) {
//        String newUid = getDatabase().child("characters").child(charUid).child("items").push().getKey();

            Item item = new Item();
            item.setName(nameText.getText().toString());
            item.setDescription(descriptionText.getText().toString());
            item.setMagical(magicalCheckBox.isChecked());
            String weightStr = weightText.getText().toString();
            try {
                item.setWeight(Double.parseDouble(weightStr));
            } catch (NumberFormatException numberFormatException) {
                item.setWeight(0);
            }

            //save into database
//        getDatabase().child("characters").child(charUid).child("items").setValue(item);
//        Toast.makeText(this, String.format("%s added", item.getName()), Toast.LENGTH_LONG).show();

            Intent data = new Intent();
            //set the data to pass back
            data.putExtra(EXTRA_ITEM, item);
            setResult(RESULT_OK, data);

            this.finish();
        }
    }
}
