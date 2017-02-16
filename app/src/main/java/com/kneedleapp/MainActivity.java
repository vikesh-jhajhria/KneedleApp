package com.kneedleapp;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                break;
            case FEEd:
                findViewById(R.id.rl_feed_selected).setVisibility(View.VISIBLE);
                break;
            case POST:
                findViewById(R.id.rl_post_selected).setVisibility(View.VISIBLE);
                break;
            case NOTIFICATION:
                findViewById(R.id.rl_notification_selected).setVisibility(View.VISIBLE);
                break;
            case PROFILE:
                findViewById(R.id.rl_profile_selected).setVisibility(View.VISIBLE);
                break;
        }
    }
}
