package com.kneedleapp;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isExit;
    private ProgressDialog dialog;
    public String TAG = "KNEEDLE";

    public enum BottomBarTab {
        HOME, FEEd, POST, NOTIFICATION, PROFILE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
    }

    public void showProgessDialog() {
        if (dialog != null && !dialog.isShowing() && !this.isFinishing())
            dialog.show();
    }

    public void showProgessDialog(String message) {
        if (dialog != null && !dialog.isShowing() && !this.isFinishing()) {
            dialog.setMessage(message);
            dialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing() && !this.isFinishing())
            dialog.dismiss();
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
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
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
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            hideAllFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(fragment)
                    .commit();
            return true;
        }
        return false;
    }

    protected void hideFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commit();
    }

    protected void removeFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    protected void hideAllFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            hideFragment(fragment);
        }
    }
}
