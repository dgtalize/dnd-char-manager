package com.dgtalize.dndcharmanager.ui.fragment;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dgtalize.dndcharmanager.BuildConfig;
import com.dgtalize.dndcharmanager.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Information about the app
 */
public class AboutFragment extends RestrictedFragment {
    private static final String LOG_TAG = "AboutFragment";

	private TextView appVersionTextView;
	private TextView appBuildDateTextView;
	private TextView privacyPolicyTextView;
	
    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
		
		appVersionTextView = (TextView) view.findViewById(R.id.appVersionTextView);
		appBuildDateTextView = (TextView) view.findViewById(R.id.appBuildDateTextView);
		privacyPolicyTextView = (TextView) view.findViewById(R.id.privacyPolicyTextView);

		privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
				startActivity(browser);
			}
		});

		try {
			PackageInfo pkgInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			appVersionTextView.setText(pkgInfo.versionName);

			Date buildDate = new Date(BuildConfig.TIMESTAMP);
			appBuildDateTextView.setText(android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", buildDate));
		} catch (PackageManager.NameNotFoundException nnfex) {
			Log.e(LOG_TAG, "Error trying to get Package info", nnfex);
		}
		
		return view;
    }

}
