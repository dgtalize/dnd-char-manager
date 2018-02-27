package com.dgtalize.dndcharmanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dgtalize.dndcharmanager.ConvertUtils;
import com.dgtalize.dndcharmanager.R;

/**
 * Generic dialog that ask for points
 */

public class PointsDialog {

    private OnPointsListener pointsListener;

    public PointsDialog(OnPointsListener pointsListener) {
        this.pointsListener = pointsListener;
    }

    /**
     * Shows the Points dialog
     *
     * @param activity
     */
    public void show(final Activity activity, int titleString) {
        // get the Modify Points dialog view
        LayoutInflater li = LayoutInflater.from(activity);
        View dialogView = li.inflate(R.layout.dialog_modify_points, null);
        //start building the dialog with the view
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(dialogView);

        final EditText pointsEditText = (EditText) dialogView.findViewById(R.id.pointsEditText);

        // set dialog message
        alertDialogBuilder
                .setTitle(titleString)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String pointsText = pointsEditText.getText().toString();
                                if (ConvertUtils.isPoints(pointsText)) {
                                    if (pointsListener != null) {
                                        pointsListener.onPointsEntered((Integer.valueOf(pointsText)));
                                    }
                                }
                                hideKeyboard(activity);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        hideKeyboard(activity);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        // show keyboard
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    /**
     * Shows the Points dialog
     *
     * @param activity
     */
    public void show(final Activity activity) {
        this.show(activity, R.string.points);
    }

    public interface OnPointsListener {
        void onPointsEntered(int points);
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}
