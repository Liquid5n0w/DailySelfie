package com.example.nate.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class DailySelfieActivity extends ActionBarActivity {

    private static final int MY_NOTIFICATION_ID = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
        mContext = getApplicationContext();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);

        return true;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
        }
    }

    public void sendNotifcation(){

        // : If not, create a PendingIntent using
        // the
        // restartMainActivityIntent and set its flags
        // to FLAG_UPDATE_CURRENT
        //TODO setup intent
        Intent restartDailySelfieIntent = new Intent();
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
                //.setContent(mContentView)
                .setTicker("Take your daily Selfie");

        // : Send the notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID,notificationBuilder.build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_take) {
            dispatchTakePictureIntent();
        }

        return super.onOptionsItemSelected(item);
    }
}
