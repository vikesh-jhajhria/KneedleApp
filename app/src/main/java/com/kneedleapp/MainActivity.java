package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.kneedleapp.fragment.EditProfileFragment;
import com.kneedleapp.fragment.HomeFragment;
import com.kneedleapp.fragment.SearchFragment;
import com.kneedleapp.fragment.PostFragment;
import com.kneedleapp.fragment.NotificationFragment;
import com.kneedleapp.fragment.ProfileFragment;
import com.kneedleapp.utils.Config;


public class MainActivity extends BaseActivity {
    Fragment fragment;
    public static boolean isPost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Config.fragmentManager == null || getSupportFragmentManager().getFragments() == null) {
            Config.fragmentManager = getSupportFragmentManager();
        }
        selectTab(BottomBarTab.HOME);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.rl_home:
                selectTab(BottomBarTab.HOME);
                break;
            case R.id.rl_search:
                selectTab(BottomBarTab.SEARCH);
                break;
            case R.id.rl_post:
                selectTab(BottomBarTab.POST);
                break;
            case R.id.rl_notification:
                selectTab(BottomBarTab.NOTIFICATION);
                break;
            case R.id.rl_profile:
                selectTab(BottomBarTab.PROFILE);
                break;

        }
    }

    public void selectTab(BottomBarTab selectedTab) {
        findViewById(R.id.rl_home_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_search_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_post_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_notification_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_profile_selected).setVisibility(View.INVISIBLE);


        switch (selectedTab) {
            case HOME:
                findViewById(R.id.rl_home_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, HomeFragment.newInstance(), "HOME_FRAGMENT");
                break;
            case SEARCH:
                findViewById(R.id.rl_search_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, SearchFragment.newInstance(), "SEARCH_FRAGMENT");
                break;
            case POST:
                findViewById(R.id.rl_post_selected).setVisibility(View.VISIBLE);
                fragment = PostFragment.newInstance();
                isPost = true;
                showFragment(R.id.main_frame, fragment, "NEWPOST_FRAGMENT");
                break;
            case NOTIFICATION:
                findViewById(R.id.rl_notification_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, NotificationFragment.newInstance(), "NOTIFICATION_FRAGMENT");
                break;
            case PROFILE:
                findViewById(R.id.rl_profile_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, ProfileFragment.newInstance(), "PROFILE_FRAGMENT");
                break;
        }
    }

    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            if(!isPost){
                fragment = getSupportFragmentManager().findFragmentByTag("EDITPROFILE");
            }


            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).onActivityResult(requestCode, resultCode, data);
            } else if (fragment != null && fragment instanceof EditProfileFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }


        }

    }
}