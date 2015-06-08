package com.example.nate.dailyselfie;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


public class DailySelfiePhoto extends Activity {

    private static final String TAG = "DailySelfiePhoto";
    ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "Launched intent");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoView = (ImageView) findViewById(R.id.photoView);

        String path = this.getIntent().getStringExtra("data");



        //Set the bitmap to the ImageView
        photoView.setImageBitmap(BitmapFactory.decodeFile(path));

    }
}
