package com.example.nate.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DailySelfieReceiver extends BroadcastReceiver{

    private static final String TAG = "DailySelfieReceiver";

    @Override
    public void onReceive(Context mContext, Intent intent){
        Log.i(TAG, "received alarm");

        //Make the intent for notification
        //FLAG_UPDATE_CURRENT so it only makes one

        Intent restartDailySelfieIntent = new Intent(mContext, DailySelfieActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                restartDailySelfieIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        //Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_face)
                .setContentIntent(pendingIntent)
                .setContentText("Take your daily selfie!")
                .setTicker("Take your daily Selfie");

        // : Send the notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DailySelfieActivity.MY_NOTIFICATION_ID,notificationBuilder.build());
    }
}
