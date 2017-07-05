package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;

public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.printKeyHash(this);
        if(Utils.isNetworkConnected(this,true)) {
            Utils.getCategories(this);
        }
        startTimer();
        try {
            if (preferences.getFirebaseId().isEmpty()) {
                FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
                String firebase_id = instance.getToken();
                if (firebase_id != null && !firebase_id.isEmpty()) {
                    preferences.setFirebaseId(firebase_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppPreferences preferences = AppPreferences.getAppPreferences(getApplicationContext());
                String userName = preferences.getStringValue(AppPreferences.USER_NAME);
                Log.v(TAG, "userName=" + userName);
                if (userName.isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                    finishAffinity();
                } else {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finishAffinity();
                }
            }
        }, Config.SPLASH_TIME);
    }
}
