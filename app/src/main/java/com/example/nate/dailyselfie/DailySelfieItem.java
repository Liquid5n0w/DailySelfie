package com.example.nate.dailyselfie;

import android.graphics.Bitmap;
import android.widget.ImageView;


public class DailySelfieItem {

    private String path = "";
    private ImageView thumbnail;

    //Model item for the array list, holds the path and the imageview

    public DailySelfieItem(String p){
        path = p;

    }

    public void setPath(String p)
    {
        this.path = p;
    }

    public void setImageView(ImageView imageView)
    {
        this.thumbnail = imageView;
    }

    public String getPath()
    {
        return this.path;
    }

    public ImageView getImageView()
    {
        return this.thumbnail;
    }


}
