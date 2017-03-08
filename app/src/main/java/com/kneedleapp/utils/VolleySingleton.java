package com.kneedleapp.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private RequestQueue mRequestQueuePatch;

    private VolleySingleton(Context context) {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        this.mCtx = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(this.mRequestQueue,new LruBitmapCache());
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public RequestQueue getRequestQueuePatch() {
        if (mRequestQueuePatch == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueuePatch = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueuePatch;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueuePatch(Request<T> req) {
        getRequestQueuePatch().add(req);
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
