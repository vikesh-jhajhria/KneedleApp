package com.kneedleapp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.CheckGPSSetting;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.LocationTracker;
import com.kneedleapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kneedleapp.utils.Config.CENTURY_GOTHIC_REGULAR;
import static com.kneedleapp.utils.Config.fragmentManager;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, Utils.LocationFoundListener, Utils.GPSSettingListener {

    private boolean isExit;
    private ProgressDialog dialog;
    public String TAG = "KNEEDLE";
    private Context mContext;
    AppPreferences preferences;


    public static enum BottomBarTab {
        HOME, SEARCH, POST, NOTIFICATION, PROFILE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        preferences = AppPreferences.getAppPreferences(this);
       /* dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
*/
        dialog = new ProgressDialog(this);
       /* try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }*/

        //    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


    }

    public void showProgessDialog() {
        if (dialog != null && !dialog.isShowing() && !this.isFinishing())
            try {
                dialog.show();
                dialog.setContentView(R.layout.progress_dialogue);
            } catch (WindowManager.BadTokenException e) {

            }
    }

    public void showProgessDialog(String message) {
        if (dialog != null && !dialog.isShowing() && !this.isFinishing()) {
            dialog.setMessage(message);
            try {
                dialog.show();
                dialog.setContentView(R.layout.progress_dialogue);
            } catch (WindowManager.BadTokenException e) {

            }
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing() && !this.isFinishing())
            try {
                dialog.dismiss();
            } catch (WindowManager.BadTokenException e) {

            }
    }


    @Override
    public void onBackPressed() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);
        if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            if (isExit) {
                finish();
            } else {
                Toast.makeText(this, "Press back again to Exit !", Toast.LENGTH_SHORT).show();
                isExit = true;
            }
        } else {
            super.onBackPressed();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        fragmentManager
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        fragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    protected void showFragment(@IdRes int containerViewId,
                                @NonNull Fragment fragment,
                                @NonNull String fragmentTag) {
        if (!showFragment(fragmentTag)) {
            addFragment(containerViewId, fragment, fragmentTag);
        }
    }

    protected boolean showFragment(@NonNull String tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            hideAllFragment();
            fragmentManager
                    .beginTransaction()
                    .show(fragment)
                    .commit();
            return true;
        }
        return false;
    }

    protected void hideFragment(@NonNull Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .hide(fragment)
                .commit();
    }

    protected void removeFragment(@NonNull Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    protected void hideAllFragment() {
        try {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment != null) {
                    hideFragment(fragment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard() {
        View v = getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public boolean hasPermission(int permissionType) {
        if (Build.VERSION.SDK_INT >= 23) {
            switch (permissionType) {
                case Config.LOCATION_PERMISSION:
                    int hasLocationPermission = ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION);
                    if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                        showPermissionSnakeBar();
                        return false;
                    } else {
                        return true;
                    }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Config.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    CheckGPSSetting.getInstance(this, this);
                } else {
                    fetchUserLocation();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        onLocationSettingStatus(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        onLocationSettingStatus(false);
                        break;
                }
                break;
        }
    }

    public void fetchUserLocation() {
        if (hasPermission(Config.LOCATION_PERMISSION)) {
            CheckGPSSetting.getInstance(this, this);
        }
    }

    @Override
    public void onLocationFoundStatus(double latitude, double longitude, String message) {
        if (message.equalsIgnoreCase("1")) {
            preferences.setLatitude(String.valueOf(latitude));
            preferences.setLongitude(String.valueOf(longitude));
            Utils.showSnakeBar(findViewById(R.id.top_layout), "Location found.");
        } else {
            Utils.showSnakeBar(findViewById(R.id.top_layout), "Location not found !");
        }
    }


    @Override
    public void onLocationSettingStatus(boolean status) {
        if (status) {
            new LocationTracker(this, this);
        } else {
            showSettingSnakeBar();
        }
    }

    private void showPermissionSnakeBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.top_layout), "Please allow location permission", Snackbar.LENGTH_INDEFINITE)
                .setAction("ALLOW", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(BaseActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                Config.LOCATION_PERMISSION);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        Utils.setTypeface(this, textView, CENTURY_GOTHIC_REGULAR);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void showSettingSnakeBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.top_layout), "Please turn on GPS", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckGPSSetting.getInstance(BaseActivity.this, BaseActivity.this);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        Utils.setTypeface(this, textView, CENTURY_GOTHIC_REGULAR);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
