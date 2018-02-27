package com.dgtalize.dndcharmanager.ui;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.dgtalize.dndcharmanager.R;
import com.dgtalize.dndcharmanager.data.DataUtils;
import com.google.firebase.database.DatabaseReference;


public class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Gets the Firebase Database reference
     *
     * @return
     */
    public DatabaseReference getDatabase() {
        return DataUtils.getDatabase();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
