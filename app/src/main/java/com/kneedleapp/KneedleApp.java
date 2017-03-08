package com.kneedleapp;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.utils.VolleySingleton;

/**
 * Created by vikesh.kumar on 08-Mar-17.
 */

public class KneedleApp extends MultiDexApplication {
    public static final String TAG = KneedleApp.class.getSimpleName();
    public static VolleySingleton volleyQueueInstance;
    private static KneedleApp mInstance;
    private RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();
        mInstance = this;
        instantiateVolleyQueue();
    }

    public void instantiateVolleyQueue() {
        volleyQueueInstance = VolleySingleton.getInstance(getApplicationContext());
    }

    public static synchronized KneedleApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        Log.e(TAG, req.toString());
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        Log.v(TAG,"URL>>"+req.getUrl());
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
