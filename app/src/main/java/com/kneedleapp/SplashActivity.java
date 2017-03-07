package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;

public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTimer();

        if (preferences.getFirebaseId().isEmpty()) {
            String firebase_id = FirebaseInstanceId.getInstance().getToken();
            if (firebase_id != null && !firebase_id.isEmpty()) {
                preferences.setFirebaseId(firebase_id);
            }
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
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                }
            }
        }, Config.SPLASH_TIME);
    }
}
