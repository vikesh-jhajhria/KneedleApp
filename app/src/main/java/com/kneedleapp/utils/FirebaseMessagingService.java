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
import com.kneedleapp.R;

import org.json.JSONException;
import org.json.JSONObject;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "StartingAndroid";
    private String message, tasteEncryptedID, activityType, reviewEncryptedID, postby;
    private AppPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        preferences = AppPreferences.getAppPreferences(this);
        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Data Message Body: " + remoteMessage.getData());
        //Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //String body = remoteMessage.getNotification().getBody();
        String body = remoteMessage.getData().get("body");
        try {
            JSONObject obj = new JSONObject(body);
            message = obj.getString("message");
            tasteEncryptedID = obj.getString("TasteEncryptedID");
            activityType = obj.getString("activitytype");
            reviewEncryptedID = obj.getString("ReviewEncryptedID");
            postby = obj.getString("postby");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getTitle(), activityType, message);
        sendNotification(remoteMessage.getData().get("title"), activityType, message);
    }

    //This method is only generating push notification
    private void sendNotification(String title, String activityType, String message) {

        Intent intent = null;
       /* if (activityType.equalsIgnoreCase("liked") || activityType.equalsIgnoreCase("commented")) {
            intent = new Intent(this, UserReviewsActivity.class).putExtra("userID", preferences.getUserEncryptedId());
        } else if (activityType.equalsIgnoreCase("followed")) {
            intent = new Intent(this, Profile_Activity.class).putExtra("userID", postby).putExtra("user_type", "other");
        }*/
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