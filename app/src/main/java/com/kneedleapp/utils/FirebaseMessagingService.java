package com.kneedleapp.utils;

/**
 * Created by hitesh.singh on 10/19/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.kneedleapp.FeedDetailActivity;
import com.kneedleapp.ProfileActivity;
import com.kneedleapp.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "StartingAndroid";
    private String message, title, user_id, feed_id, user_name;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Data Message Body: " + remoteMessage.getData());
        //Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //String body = remoteMessage.getNotification().getBody();
        //Log.e(TAG, "Data  Body: " + body);
        message = remoteMessage.getData().get("body");
        title = remoteMessage.getData().get("title");
        user_id = remoteMessage.getData().get("user_id");
        feed_id = remoteMessage.getData().get("feed_id");
        user_name = remoteMessage.getData().get("body").split(" ")[0];

        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getTitle(), activityType, message);
        sendNotification(title, message);
    }

    //This method is only generating push notification
    private void sendNotification(String title, String message) {

        Intent intent = null;
        if (user_id != null && !user_id.isEmpty()) {
            intent = new Intent(getApplicationContext(), ProfileActivity.class)
                    .putExtra("USER_ID",user_id)
                    .putExtra("USER_NAME",user_name);
        } else if (feed_id != null && !feed_id.isEmpty()) {
            intent = new Intent(getApplicationContext(), FeedDetailActivity.class)
                    .putExtra("FEEDID",feed_id);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}