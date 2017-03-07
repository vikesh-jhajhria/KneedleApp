package com.kneedleapp.utils;

/**
 * Created by hitesh.singh on 10/19/2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;


public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token in logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        AppPreferences.getAppPreferences(getApplicationContext()).setFirebaseId(refreshedToken);
    }

}
