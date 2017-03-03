package com.kneedleapp;

import android.os.Bundle;
import android.view.View;

import com.kneedleapp.fragment.FeedFragment;
import com.kneedleapp.fragment.HomeFragment;
import com.kneedleapp.fragment.NewPostFragment;
import com.kneedleapp.fragment.NotificationFragment;
import com.kneedleapp.fragment.ProfileFragment;
import com.kneedleapp.utils.Config;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Config.fragmentManager == null){
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
            case R.id.rl_feed:
                selectTab(BottomBarTab.FEEd);
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

    private void selectTab(BottomBarTab selectedTab) {
        findViewById(R.id.rl_home_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_feed_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_post_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_notification_selected).setVisibility(View.INVISIBLE);
        findViewById(R.id.rl_profile_selected).setVisibility(View.INVISIBLE);


        switch (selectedTab) {
            case HOME:
                findViewById(R.id.rl_home_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, HomeFragment.newInstance("", ""), "HOME_FRAGMENT");
                break;
            case FEEd:
                findViewById(R.id.rl_feed_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, FeedFragment.newInstance("", ""), "FEED_FRAGMENT");

                break;
            case POST:
                findViewById(R.id.rl_post_selected).setVisibility(View.VISIBLE);
                //    showFragment(R.id.main_frame, PostFragment.newInstance("", ""), "POST_FRAGMENT");
                showFragment(R.id.main_frame, NewPostFragment.newInstance("", ""), "NEWPOST_FRAGMENT");
                break;
            case NOTIFICATION:
                findViewById(R.id.rl_notification_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, NotificationFragment.newInstance("", ""), "NOTIFICATION_FRAGMENT");
                break;
            case PROFILE:
                findViewById(R.id.rl_profile_selected).setVisibility(View.VISIBLE);
                showFragment(R.id.main_frame, ProfileFragment.newInstance("", ""), "PROFILE_FRAGMENT");
                break;
        }
    }
}
