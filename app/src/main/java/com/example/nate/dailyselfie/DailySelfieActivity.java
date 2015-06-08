package com.example.nate.dailyselfie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class DailySelfieActivity extends ActionBarActivity {

    private static final String TAG = "DailySelfieAcitivty";
    private static final String CUSTOM_INTENT = "com.example.nate.dailyselfie.alarm";

    static final int MY_NOTIFICATION_ID = 1;
    private static final long ALARM_DELAY = 2*60*1000L;
    private static final String PREFS = "prefs";
    private Context mContext;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AlarmManager mAlarmManager;
    ArrayList<DailySelfieItem> dailySelfieItems;
    DailySelfieAdaptor adapter;
    ListView listView;

//    private LocalBroadcastManager mBroadcastMgr;
//    DailySelfieReceiver receiver;
//    private final IntentFilter intentFilter = new IntentFilter(CUSTOM_INTENT);

    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);

        //setup arrayadaptor


        listView = (ListView) findViewById(R.id.listView);
/*
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };
        */

        dailySelfieItems = new ArrayList<>();





        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data


        adapter = new DailySelfieAdaptor(this, dailySelfieItems);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                String filepath = (String) ((TextView) view.findViewById(R.id.path)).getText();

                Intent i = new Intent(DailySelfieActivity.this, DailySelfiePhoto.class);
                i.putExtra("data",filepath);
                Log.i(TAG, "Launching intent");
                startActivity(i);


                // Show Alert
                /*
                Toast.makeText(getApplicationContext(),
                        "Position :" + position + "  ListItem : " , Toast.LENGTH_LONG)
                        .show();
*/
            }

        });


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


        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                set,
                ALARM_DELAY,
                mNotificationReceiverPendingIntent);




        Log.i(TAG, "Alarm set");



        SharedPreferences settings = getSharedPreferences(PREFS ,MODE_PRIVATE);
        int size = settings.getInt("size", -1);
        if (size > 0){
            for (int i = 0; i<size; i++)
                dailySelfieItems.add(new DailySelfieItem(settings.getString(""+i, "")));
            adapter.notifyDataSetChanged();
        }


    }

    public void onPause(){
        super.onPause();

        SharedPreferences settings = getSharedPreferences(PREFS ,MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("size", listView.getCount());
        TextView pathView;
        for (int i = 0; i < listView.getCount(); i++){
            pathView = (TextView) listView.getChildAt(i).findViewById(R.id.path);

            editor.putString("" + i, (String) pathView.getText());
        }
        editor.commit();
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


            //setPic((ImageView) findViewById(R.id.imageView), mCurrentPhotoPath);



//            DailySelfieItem item = new DailySelfieItem(mCurrentPhotoPath, (ImageView) selfieItemView.findViewById(R.id.thumbnail));
            DailySelfieItem item = new DailySelfieItem(mCurrentPhotoPath);


            dailySelfieItems.add(item);
//            int index = (dailySelfieItems.lastIndexOf(item));


            adapter.notifyDataSetChanged();


            //item.setPic((ImageView) listView.findViewById(R.id.thumbnail));

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
        mCurrentPhotoPath = image.getAbsolutePath();
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

    public static void setPic(ImageView imageView, String photoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        //BitmapFactory.decodeFile(photoPath, bmOptions);

        try {
            BitmapFactory.decodeStream(new FileInputStream(photoPath), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        Log.i(TAG, "photoH: "+photoH + " photoW: "+photoW);
        Log.i(TAG, "targetH: "+targetH + " targetW: "+targetW);

        if (photoH == 0 ||photoW == 0 || targetH==0 || targetW ==0) {

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
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
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
