package com.dgtalize.dndcharmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.model.Feat;

public class FeatAddActivity extends BaseRestrictedActivity {
    private static final String LOG_TAG = "FeatAddActivity";

    public static final String EXTRA_FEAT = "feat";

    private EditText nameText;
    private EditText shortDescEditText;
    private EditText longDescEditText;

    Feat feat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feat_add);

        nameText = (EditText) findViewById(R.id.nameText);
        shortDescEditText = (EditText) findViewById(R.id.shortDescEditText);
        longDescEditText = (EditText) findViewById(R.id.longDescEditText);

        //initialize feat
        Intent intent = getIntent();
        feat = intent.getParcelableExtra(EXTRA_FEAT);
        if (feat == null) {
            feat = new Feat();
        } else {
            //if it's not null then load view
            initializeView(feat);
        }
    }

    private void initializeView(Feat feat) {
        nameText.setText(feat.getName());
        shortDescEditText.setText(String.valueOf(feat.getShortDescription()));
        longDescEditText.setText(String.valueOf(feat.getDescription()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feat_add, menu);
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
            feat.setName(nameText.getText().toString());
            feat.setShortDescription(shortDescEditText.getText().toString());
            feat.setDescription(longDescEditText.getText().toString());

            Intent data = new Intent();
            //set the data to pass back
            data.putExtra(EXTRA_FEAT, feat);
            setResult(RESULT_OK, data);

            this.finish();
        }
    }
}
