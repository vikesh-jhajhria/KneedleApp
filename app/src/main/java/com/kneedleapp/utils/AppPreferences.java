package com.kneedleapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class AppPreferences {

    public static final String API_KEY = "APIKEY";
    public static final String USER_ID = "USERID";
    public static final String USER_NAME = "USERNAME";
    public static final String USER_PHONE = "USERPHONE";
    public static final String USER_IMAGE = "USERIMAGE";
    public static final String USER_IMAGE_URL = "USERIMAGEURL";
    private static final String SHARED_PREFERENCE_NAME = "OCTAGON";
    private SharedPreferences mPrefs;

    private AppPreferences(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    public static AppPreferences getAppPreferences(Context context) {
        return new AppPreferences(context);

    }

    public String getStringValue(String Key) {
        return mPrefs.getString(Key, "");
    }

    public void putStringValue(String Key, String value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Key, value);
        editor.commit();
    }

    public void putImage(String key, Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, encodedImage);
        editor.commit();
    }

    public Bitmap getImage(String key) {
        Bitmap bitmap = null;
        String previouslyEncodedImage = mPrefs.getString(key, "");

        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

    public int getIntValue(String key) {
        return mPrefs.getInt(key, 0);
    }

    public void putIntValue(String key, int value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBooleanValue(String key) {

        return mPrefs.getBoolean(key, false);
    }

    public void putBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void removeFromPreferences(String key) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(key);
        editor.commit();
    }
}
