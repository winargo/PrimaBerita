package com.constantlab.wpconnect.views.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.constantlab.wpconnect.application.ApplicationMain;
import com.constantlab.wpconnect.core.data.Constants;

/**
 * Created by Constant-Lab LLP on 28-06-2016.
 */
public class BaseFragment extends Fragment {

    ApplicationMain applicationMain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationMain = (ApplicationMain) getContext().getApplicationContext();
    }

    protected boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        return sharedPreferences.getString(Constants.KEY_FOR_TOKEN, null) != null;
    }
}
