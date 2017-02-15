package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startTimer();
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
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finishAffinity();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                }
            }
        }, Config.SPLASH_TIME);
    }
}
