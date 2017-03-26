package com.kneedleapp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vikesh on 29-04-2016.
 */
public class FBLoginClass implements FacebookCallback<LoginResult> {
    private Context context;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    public CallbackManager callbackManager;
    public OnFBResultListener resultListener;
    private AccessToken accessToken;


    public FBLoginClass() {
    }

    public void sdkInitialize(Context context) {
        this.context = context;
        FacebookSdk.sdkInitialize(context);
        prepare();
    }

    private void prepare() {
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        startTracking();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, this);


    }

    public void setFBResultListener(OnFBResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void login(Activity activity) {
        ArrayList<String> list = new ArrayList<>();
        list.add("public_profile");
        list.add("email");
        list.add("user_location");
        list.add("user_friends");
        LoginManager.getInstance().logInWithReadPermissions(activity, list);
    }

    public void startTracking() {
        if (accessTokenTracker != null && profileTracker != null) {
            accessTokenTracker.startTracking();
            profileTracker.startTracking();
        }
    }

    public void stopTracking() {
        if (accessTokenTracker != null && profileTracker != null) {
            accessTokenTracker.stopTracking();
            profileTracker.stopTracking();
        }
    }


    private void displayMessage(Profile profile) {
        if (profile != null) {
            //Toast.makeText(context, profile.getName(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        accessToken = loginResult.getAccessToken();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String facebookId = object.getString("id");
                            String firstName = object.getString("first_name");
                            String lastName = object.getString("last_name");
                            String email = object.has("email") ? object.getString("email") : "";
                            String imageURL = "http://graph.facebook.com/" + facebookId + "/picture?type=large";

                            Log.v("Facebook=", facebookId + " " + firstName + " " + lastName + " " + email + " " + imageURL );

                            JSONObject userData = new JSONObject();
                            userData.put("name", firstName + " " + lastName);
                            userData.put("email", email);
                            userData.put("id", facebookId);
                            userData.put("photo_url", imageURL);
                            userData.put("token",accessToken.getToken());

                            if (resultListener != null)
                                resultListener.onFBResult(userData);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email, first_name, last_name, gender, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    public interface OnFBResultListener {
        void onFBResult(JSONObject object);
    }


}
