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
    String mCurrentPhotoPath;

//    private LocalBroadcastManager mBroadcastMgr;
//    DailySelfieReceiver receiver;
//    private final IntentFilter intentFilter = new IntentFilter(CUSTOM_INTENT);

    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);


        listView = (ListView) findViewById(R.id.listView);

        //setup arrayadaptor
        dailySelfieItems = new ArrayList<>();
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




        Long set = System.currentTimeMillis()+ALARM_DELAY;
        Log.i(TAG, "Sys time: " + System.currentTimeMillis());
        Log.i(TAG, "set for : " + set);


        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                set,
                ALARM_DELAY,
                mNotificationReceiverPendingIntent);




        Log.i(TAG, "Alarm set");


        // reload previously taken selfies
        SharedPreferences settings = getSharedPreferences(PREFS ,MODE_PRIVATE);

        //returns -1 if there are no size saved.
        int size = settings.getInt("size", -1);

        //Only loads if atleast one is saved
        if (size > 0){
            for (int i = 0; i<size; i++)
                dailySelfieItems.add(new DailySelfieItem(settings.getString(""+i, "")));
            adapter.notifyDataSetChanged();
        }


    }

    public void onPause(){
        super.onPause();

        //Save into prefs the paths of all of the selfies, and also how many are saved.
        SharedPreferences settings = getSharedPreferences(PREFS ,MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("size", listView.getCount());
        TextView pathView;
        for (int i = 0; i < listView.getCount(); i++){
            pathView = (TextView) listView.getChildAt(i).findViewById(R.id.path);

            editor.putString("" + i, (String) pathView.getText());
        }
        //Commit the save now
        editor.commit();
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
            Log.i(TAG, mCurrentPhotoPath);

            //Add that photo to the android gallery
            galleryAddPic();

            //Make new model item for this photo
            DailySelfieItem item = new DailySelfieItem(mCurrentPhotoPath);

            //Add item to the arraylist
            dailySelfieItems.add(item);

            //Update the UI
            adapter.notifyDataSetChanged();

        }
    }



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);

        return true;
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
