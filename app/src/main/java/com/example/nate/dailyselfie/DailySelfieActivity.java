package com.example.nate.dailyselfie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DailySelfieActivity extends ActionBarActivity {

    private static final String TAG = "DailySelfieAcitivty";
    private static final String CUSTOM_INTENT = "com.example.nate.dailyselfie.alarm";

    static final int MY_NOTIFICATION_ID = 1;
    private static final long ALARM_DELAY = 2*60*1000L;
    private Context mContext;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AlarmManager mAlarmManager;

//    private LocalBroadcastManager mBroadcastMgr;
//    DailySelfieReceiver receiver;
//    private final IntentFilter intentFilter = new IntentFilter(CUSTOM_INTENT);

    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);



        mContext = getApplicationContext();

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(mContext, DailySelfieReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, mNotificationReceiverIntent, 0);

//        Calendar time = Calendar.getInstance();
//        time.setTimeInMillis(System.currentTimeMillis());
//        //Add 2 minutes
//        time.add(Calendar.MINUTE, 2);


        Long set = System.currentTimeMillis()+ALARM_DELAY;
        Log.i(TAG, "Sys time: " + System.currentTimeMillis());
        Log.i(TAG, "set for : " + set);

        //TODO Disabled alarm
        /*
        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                set,
                ALARM_DELAY,
                mNotificationReceiverPendingIntent);
*/



        Log.i(TAG, "Alarm set");




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);

        return true;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i(TAG,"Error occurred while creating the File");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.i(TAG, "Received result from image capture");

            //Gets thumbnail from the intent
/*
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(imageBitmap);
*/


            //Add that photo to the android gallery
            //galleryAddPic();

            Log.i(TAG, mCurrentPhotoPath);

            //Set image to the imageview
            setPic((ImageView) findViewById(R.id.imageView), mCurrentPhotoPath);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /*
    Managing multiple full-sized images can be tricky with limited memory. If you find
    your application running out of memory after displaying just a few images, you can
    dramatically reduce the amount of dynamic heap used by expanding the JPEG into a memory
    array that's already scaled to match the size of the destination view. The following
    example method demonstrates this technique.
    */

    private void setPic(ImageView imageView, String photoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        //TODO can't find the file to open
//        BitmapFactory.decodeFile(photoPath, bmOptions);

        try {
            BitmapFactory.decodeStream(new FileInputStream(photoPath), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.i(TAG, "photoH: "+photoH + " photoW: "+photoW);
        Log.i(TAG, "targetH: "+targetH + " targetW: "+targetW);

        if (photoH == 0 ||photoW == 0) {

            return;
        }

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        //Set the bitmap to the ImageView
        imageView.setImageBitmap(bitmap);
    }


    public void sendNotification(){

        // : If not, create a PendingIntent using
        // the
        // restartMainActivityIntent and set its flags
        // to FLAG_UPDATE_CURRENT

        Intent restartDailySelfieIntent = new Intent(this, DailySelfieActivity.class);
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
            //Take photo
            dispatchTakePictureIntent();



        }

        return super.onOptionsItemSelected(item);
    }
}
