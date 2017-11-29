package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;

//Class that stores data on the bitmaps that are added to the different
//canvases, this will primarily be used within the context of the cropped image.
//
//This is the object that will handle telling the image what to do by keeping track,
//of placement and width/height to telling canvas to do something.
public class SquidBitmapData {

    private Context context;

    //Data directly related to the bitmap object.
    public int x,y,scale_x,scale_y;
    public float width,height;

    //The actual bitmap data for the image.
    public Bitmap bit,undo_bit;

    //Constructor.
    public SquidBitmapData(Context con) {
        context = con;
    }

    //Set new values for the bitmap data object.
    public void set_values(int x,int y,float width,float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Setter for setting the bitmap that we can use to draw etc.
    public void set_bitmap(Bitmap b){
        bit = b;

        width = b.getWidth();
        height = b.getHeight();

        scale_x = 1;
        scale_y = 1;
    }
}
