package com.kinghorn.app.squidfaceswap;

import android.graphics.Bitmap;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */

//Class that will be used to hold data about each of the images, such as
    //the original bitmap, where the user highlighted to switch
    //and the actual data to switch.
public class SquidImageData {
    private Bitmap orig_img;
    private Bitmap data_change;
    private SquidSelectorRectangle rect_data;

    public SquidImageData(Bitmap o, Bitmap c, SquidSelectorRectangle r){

    }
}
