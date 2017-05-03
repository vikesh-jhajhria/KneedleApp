package com.kneedleapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kneedleapp.MainActivity;
import com.kneedleapp.R;
import com.kneedleapp.fragment.FeedDetailFragment;
import com.kneedleapp.fragment.ProfileFragment;

import static com.kneedleapp.utils.Config.fragmentManager;

/**
 * Created by Vikesh on 03-05-2017.
 */

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("Broadcast","broadcast");
        if(MainActivity.getInstance() != null){
            if(intent.getStringExtra("userId") != null) {
                MainActivity.getInstance().addFragment(R.id.main_frame,
                        ProfileFragment.newInstance(intent.getStringExtra("userId"),
                                ""), "PROFILE_FRAGMENT", true);
            } else if(intent.getStringExtra("feedId") != null) {
                FeedDetailFragment fragment = FeedDetailFragment.newInstance(intent.getStringExtra("feedId"));
                fragmentManager.beginTransaction()
                        .add(R.id.main_frame, fragment, "FEED_DETAIL_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            if(intent.getStringExtra("userId") != null) {
                context.startActivity(new Intent(context,MainActivity.class).putExtra("Notification","True")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("userId",intent.getStringExtra("userId")));
            } else if(intent.getStringExtra("feedId") != null) {
                context.startActivity(new Intent(context,MainActivity.class).putExtra("Notification","True")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("feedId",intent.getStringExtra("feedId")));
            }

        }
    }
}
