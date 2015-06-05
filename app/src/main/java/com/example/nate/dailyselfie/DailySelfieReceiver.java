package com.example.nate.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by nemberso on 6/5/2015.
 */
public class DailySelfieReceiver extends BroadcastReceiver{

    private static final String TAG = "DailySelfieReceiver";

    @Override
    public void onReceive(Context mContext, Intent intent){
        Log.i(TAG, "received alarm");
        // : If not, create a PendingIntent using
        // the
        // restartMainActivityIntent and set its flags
        // to FLAG_UPDATE_CURRENT

        Intent restartDailySelfieIntent = new Intent(mContext, DailySelfieActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                restartDailySelfieIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // : Use the Notification.Builder class to
        // create the Notification. You will have to set
        // several pieces of information. You can use
        // android.R.drawable.stat_sys_warning
        // for the small icon. You should also
        // setAutoCancel(true).

        Notification.Builder notificationBuilder = new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentIntent(pendingIntent)
                .setContentText("Take your daily selfie!")
                .setTicker("Take your daily Selfie");

        // : Send the notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(DailySelfieActivity.MY_NOTIFICATION_ID,notificationBuilder.build());
    }
}
