package com.kneedleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.kneedleapp.fragment.EditProfileFragment;
import com.kneedleapp.fragment.FeedDetailFragment;
import com.kneedleapp.fragment.HomeFragment;
import com.kneedleapp.fragment.NotificationFragment;
import com.kneedleapp.fragment.PostFragment;
import com.kneedleapp.fragment.ProfileFragment;
import com.kneedleapp.fragment.SearchFragment;
import com.kneedleapp.utils.Config;

import static com.kneedleapp.utils.Config.fragmentManager;


public class MainActivity extends BaseActivity {
    Fragment fragment;
    public static boolean isPost = false;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fragmentManager == null || getSupportFragmentManager().getFragments() == null) {
            fragmentManager = getSupportFragmentManager();
        }
        if(getIntent().getExtras() != null && getIntent().getExtras().get("Notification") != null){
            if(getIntent().getStringExtra("userId") != null) {
                addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(getIntent().getStringExtra("userId"),
                                ""), "PROFILE_FRAGMENT", true);
            } else if(getIntent().getStringExtra("feedId") != null) {
                FeedDetailFragment fragment = FeedDetailFragment.newInstance(getIntent().getStringExtra("feedId"));
                fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "FEED_DETAIL_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            selectTab(BottomBarTab.HOME);
        }
    }

    public static MainActivity getInstance(){
        return instance;
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
        Config.LAST_PAGE = "HOME";
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
                ProfileFragment fragment = (ProfileFragment) fragmentManager.findFragmentByTag("PROFILE_FRAGMENT");
                showFragment(R.id.main_frame, ProfileFragment.newInstance(preferences.getUserId(),
                        preferences.getUserName()), "PROFILE_FRAGMENT");

                if (fragment != null && !fragment.getUserId().equalsIgnoreCase(preferences.getUserId())) {
                    fragment.loadMyProfile();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        instance = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
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
            if (!isPost) {
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