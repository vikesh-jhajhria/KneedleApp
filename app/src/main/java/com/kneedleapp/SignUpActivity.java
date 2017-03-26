package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.FBLoginClass;
import com.kneedleapp.utils.Utils;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;



public class SignUpActivity extends BaseActivity implements FBLoginClass.OnFBResultListener {
    private FBLoginClass fbLoginClass;
    public static final int TWITTER_LOGIN = 1;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    TwitterAuthClient mTwitterAuthClient;


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mTwitterAuthClient = new TwitterAuthClient();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(SignUpActivity.this,user.getUid()+" "+user.getDisplayName()+" "+user.getEmail(),Toast.LENGTH_LONG).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        applyFonts();
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            mAuth.addAuthStateListener(mAuthListener);
                        }

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fbLoginClass != null) {
            fbLoginClass.stopTracking();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG,"responsecode:"+requestCode);
        if (requestCode == 140) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        } else {
            fbLoginClass.callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                break;
            case R.id.btn_facebook:
                fbLoginClass = new FBLoginClass();
                fbLoginClass.setFBResultListener(this);
                fbLoginClass.sdkInitialize(getApplicationContext());
                fbLoginClass.login(this);
                break;
            case R.id.btn_twitter:
                loginWithTwitter();
                break;
        }
    }

    private void applyFonts() {
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_facebook), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_twitter), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(this, (TextView) findViewById(R.id.btn_register), Config.CENTURY_GOTHIC_REGULAR);

    }

    @Override
    public void onFBResult(JSONObject result) {
        Toast.makeText(SignUpActivity.this, result.toString(), Toast.LENGTH_LONG).show();
    }


    /* ************************************
    *               TWITTER              *
    **************************************
    */
    private void loginWithTwitter() {

        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth.getCurrentUser() == null) {
            mTwitterAuthClient.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> twitterSessionResult) {
                    // Success
                    handleTwitterSession(twitterSessionResult.data);
                }

                @Override
                public void failure(TwitterException e) {
                    e.printStackTrace();
                }
            });

        }
    }
}
