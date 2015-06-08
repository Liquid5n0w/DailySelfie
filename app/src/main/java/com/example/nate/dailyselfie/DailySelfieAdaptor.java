package com.example.nate.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DailySelfieAdaptor extends ArrayAdapter<DailySelfieItem> {
    public DailySelfieAdaptor(Context context, ArrayList<DailySelfieItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DailySelfieItem dailySelfieItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.selfieitem, parent, false);
        }
        // Lookup view for data population
        TextView textView = (TextView) convertView.findViewById(R.id.path);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
        // Populate the data into the template view using the data object
        textView.setText(dailySelfieItem.getPath());
        //bitmap.setImageBitmap(dailySelfieItem.getImage());

        dailySelfieItem.setImageView(imageView);

//        DailySelfieActivity.setPic(imageView,dailySelfieItem.getPath());

        Bitmap bitmap = BitmapFactory.decodeFile(dailySelfieItem.getPath());

        //Set the bitmap to the ImageView
        imageView.setImageBitmap(bitmap);

        //set image to the listview


        // Return the completed view to render on screen
        return convertView;
    }


}
