package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class LandingActivity extends BaseActivity {


    //This is your KEY and SECRET
    //And it would be added automatically while the configuration
    private static final String TWITTER_KEY = "pmvSTKaPNNXZDgvx4gqAlPhhb";
    private static final String TWITTER_SECRET = "7l2CV6w2H06yxsvjCj6OcfUfZ3JRF3dnGpx0AF43rteGTTYjrn";

    //Tags to send the username and image url to next activity using intent
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE_URL = "image_url";
    TwitterAuthConfig authConfig;

    //Twitter Login Button
    public static TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_landing);
        applyFonts();
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLogin);

        //Adding callback to the button

        TwitterAuthClient client = new TwitterAuthClient();

        client.authorize(LandingActivity.this,new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                Log.e(TAG, "Logged with twitter");
                TwitterSession session = twitterSessionResult.data;
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException e) {
                Log.e(TAG, "Failed login with twitter");
                e.printStackTrace();
            }
        } );

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                login(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("TwitterKit", "Login with Twitter failure", exception);
            }
        });
        twitterLoginButton.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                break;
            case R.id.twitterLogin:
                Log.e("TAG", "twitter button");
                break;
        }
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_login), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_register), Config.CENTURY_GOTHIC_REGULAR);

    }

    public void login(Result<TwitterSession> result) {

        //Creating a twitter session with result's data
        TwitterSession session = result.data;

        //Getting the username from session
        final String username = session.getUserName();

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
        Call<User> call = Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false);
        call.enqueue(new Callback<User>() {
            @Override
            public void failure(TwitterException e) {
                Log.e("error::__>>", e.toString());
            }

            @Override
            public void success(Result<User> userResult) {
                //If it succeeds creating a User object from userResult.data
                User user = userResult.data;
                String twitterImage = user.profileImageUrl;

                try {
                    Log.e("imageurl", user.profileImageUrl);
                    Log.e("name", user.name);
                    Log.e("email", user.email);
                    Log.e("des", user.description);
                    Log.e("followers ", String.valueOf(user.followersCount));
                    Log.e("createdAt", user.createdAt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}