package com.example.nate.dailyselfie;

import android.graphics.Bitmap;
import android.widget.ImageView;


public class DailySelfieItem {

    private String path = "";
    private ImageView thumbnail;




    public DailySelfieItem(String p){
        path = p;


    }
    public void setPic(ImageView i){
        thumbnail = i;
        DailySelfieActivity.setPic(thumbnail, path);
    }


    public void setPath(String CompanyName)
    {
        this.path = CompanyName;
    }

    public void setImageView(ImageView Bitmap)
    {
        this.thumbnail = Bitmap;
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
