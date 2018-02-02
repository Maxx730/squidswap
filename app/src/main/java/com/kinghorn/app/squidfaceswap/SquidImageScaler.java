package com.kinghorn.app.squidfaceswap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class SquidImageScaler {

    public SquidCanvas focused_can;

    //Needs to scale images based on the context the image is in.
    public SquidImageScaler(){

    }

    //Pulls in the canvas and scales the image to the size of he canvas.
    public Bitmap scale_image(Canvas c){
        Bitmap b = null;

        //Get the size of the canvas of the x and y.
        int scale_x = c.getWidth();
        int scale_y = c.getHeight();

        //Create our matrix of the scaling.
        Matrix m = new Matrix();


        return b;
    }

}