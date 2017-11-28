package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */


//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    private Bitmap bit,last_bit;
    private BitmapFactory.Options op;
    private Context cn;
    private int x_scale;
    private int y_scale;
    private SquidSelector selection;

    public SquidCanvas(Context con,Bitmap img,BitmapFactory.Options opt,SquidSelector select){
        super(con);
        set_scales(1,1);
        cn = con;
        bit = img;
        op = opt;
        selection = select;

        //Grab caching information here.
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        System.out.println("SquidCanvas Initialized...");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.drawColor(Color.BLACK);
        System.out.println(bit.getWidth());

        BitmapFactory.Options opt = new BitmapFactory.Options();


        Bitmap scaled = Bitmap.createScaledBitmap(bit,bit.getWidth() * x_scale,bit.getHeight() * y_scale,true);
        canvas.drawBitmap(scaled,(canvas.getWidth() - (bit.getWidth() * x_scale)) / 2,(canvas.getHeight() - (bit.getHeight() * y_scale)) / 2,null);
    }

    //Returns data pulled from the drawing cache and returns a bitmap object.
    public Bitmap return_bmp_data(){
        System.out.println("Drawing Cache: " + isDrawingCacheEnabled());
        Bitmap data = getDrawingCache();

        //Save the last image before it was altered to revert the image if needed.
        last_bit = bit;

        Bitmap cropped = convert_points(data);

        System.out.println("Image Cached Size: " + (getDrawingCache().getByteCount() / 1024) + "Kb");

        return cropped;
    }

    //We need to convert the points based where they were dragged from and ended etc.
    private Bitmap convert_points(Bitmap data){
        float conv_start_x,conv_end_x,conv_start_y,conv_end_y;

        //Check the values of the start and end values and convert based on them.
        if(selection.rect.start_x > selection.rect.end_x){
            System.out.println("Swapping start and end points for X value...");
            conv_end_x = selection.rect.start_x;
            conv_start_x = selection.rect.end_x;
        }else{
            conv_end_x = selection.rect.end_x;
            conv_start_x = selection.rect.start_x;
        }

        if(selection.rect.start_y > selection.rect.end_y){
            System.out.println("Swapping start and end points for Y value...");
            conv_end_y = selection.rect.start_y;
            conv_start_y = selection.rect.end_y;
        }else{
            conv_end_y = selection.rect.end_y;
            conv_start_y = selection.rect.start_y;
        }

        //So now we want to try and resize the image based on the starting and stopping locations.
        //
        //We need to calculate the size of the image regardless of what direction the drag started at
        //all the way to when it ends.
        return Bitmap.createBitmap(data,(int) Math.round(conv_start_x),(int) Math.round(conv_start_y),(int) Math.round(conv_end_x - conv_start_x),(int) Math.round(conv_end_y - conv_start_y));
    }

    //Reverts the drawn image back to what is was originally.
    public void image_back(){
        if(last_bit != null){
            bit = last_bit;
            invalidate();
        }else{
            System.out.println("There is no data to revert back to...");
        }
    }

    //Sets the scale values for the selected image.
    public void set_scales(int x,int y){
        this.x_scale = x;
        this.y_scale = y;
    }

    //Scale getters.
    public int get_scale_x(){
        return this.x_scale;
    }
    public int get_scale_y(){
        return this.y_scale;
    }

    //Getter/Setters for the image that the canvas is currently using.]
    public void set_img(Bitmap img){
        bit = img;


        invalidate();
    }

    //Rotates the Bitmap that is drawn onto the canvas.
    public void rotate_image(float ang){
        float rot = getRotation();

        setRotation(rot + ang);
        invalidate();
    }

    public float get_center_x(Bitmap img){

        return img.getWidth() / 2;
    }

    public float get_center_y(Bitmap img){
        return img.getHeight() / 2;
    }
}