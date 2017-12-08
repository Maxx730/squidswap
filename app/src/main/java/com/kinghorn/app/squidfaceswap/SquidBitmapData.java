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
    public float x;
    public float y;
    public float scale_x;
    public float scale_y;
    public float width,height;

    //Booleans that keep track of the fading and the scaling of the image,
    //
    public boolean is_scaled = false;
    public boolean is_fade = true;

    //The actual bitmap data for the image.
    public Bitmap bit,undo_bit;

    //Getters and setters here.
    public void set_undo(Bitmap b){
        undo_bit = b;
    }
    public Bitmap get_undo(){
        return undo_bit;
    }
    public Bitmap get_bit(){return bit;}

    //Constructor.
    public SquidBitmapData(Context con) {
        context = con;
        this.scale_x = 1;
        this.scale_y = 1;
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
    }

    public void set_loc(float x,float y){
        this.x = x;
        this.y = y;
    }
}
