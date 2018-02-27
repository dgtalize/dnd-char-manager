package com.dgtalize.dndcharmanager.ui.fragment;

import android.support.v4.app.Fragment;

import com.dgtalize.dndcharmanager.data.DataUtils;
import com.dgtalize.dndcharmanager.ui.BaseRestrictedActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragment that is placed inside the MainActivity
 */

public class RestrictedFragment extends Fragment {

    protected BaseRestrictedActivity getParentActivity() {
        return (BaseRestrictedActivity) getActivity();
    }

    /**
     * Gets the current logged-in firebase user, from the parent Activity
     *
     * @return
     */
    protected FirebaseUser getFirebaseUser() {
        return getParentActivity().getFirebaseUser();
    }

    protected DatabaseReference getDatabase() {
        return DataUtils.getDatabase();
    }

    protected void showProgressDialog() {
        if (getParentActivity() != null) {
            getParentActivity().showProgressDialog();
        }
    }

    protected void hideProgressDialog() {
        if (getParentActivity() != null) {
            getParentActivity().hideProgressDialog();
        }
    }

}
