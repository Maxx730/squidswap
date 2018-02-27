package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    //Private variables.
    protected Context cn;
    protected Bitmap focused,last;
    protected SeekBar scaling_bar;
    protected boolean overwrite_focused = false;
    protected RelativeLayout success_layout;
    protected SquidFileService files;
    protected Matrix image_matrix;
    //Public variables that can be edited from outsite the
    //object.
    public float fade_val = .025f,scale_factor = 1,rotation = 0;
    public boolean CENTER_IMAGE = true;
    public boolean AUTOSCALE = false;

    //Constructor
    public SquidCanvas(Context con){
        super(con);
        //Set our variables.
        cn = con;
        files = new SquidFileService(cn);
        image_matrix = new Matrix();
        //Grab caching information here.
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    //Getters and setters are below.
    public void set_img(Bitmap b){
        last = focused;
        focused = b;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //If the focused image has any data then write the data to the canvas.
        if (this.focused != null) {
            if(CENTER_IMAGE){
                canvas.drawBitmap(this.focused,image_matrix,null);
            }else{

            }
        }
    }

    //Returns the image after the fading on the edge differences have been applied.
    //As of now we are still working on the circular duffer fade functionality.
    public Bitmap get_faded_img(String type){
        Bitmap orig = matrix_scale(focused,scale_factor,scale_factor,false);
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(),Bitmap.Config.ARGB_8888);

        //Create a new transparent bitmap to start drawing multiple layers onto.
        Canvas c = new Canvas(b);
        Paint p = new Paint();
        //DST out layer mode to hide black and show trans.
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        //Color array of the shader. also with the anchor points for when to start and stop colors.
        final int[] cols = {Color.TRANSPARENT,Color.parseColor("#00000055"),Color.parseColor("#00000077"),Color.BLACK};
        final float[] ancs = {.3f,.5f,.6f,1f};

        //Create the gradient shader using the color and anchor arrays.
        RadialGradient r = new RadialGradient((orig.getWidth()) / 2,orig.getHeight() / 2,(orig.getHeight() / fade_val),cols,ancs, Shader.TileMode.CLAMP);
        p.setShader(r);

        //Draw the original bitmap then draw the shader over it.
        c.drawBitmap(orig,0,0,null);
        c.drawCircle(orig.getWidth() / 2f,orig.getHeight() / 2f,orig.getHeight() / .7f,p);

        //Here we need to apply any rotation to the given image.
        return matrix_rotate(b);
    }

    //Scales the image based on a matrix rather than scaling the canvas, this will probably
    //solve the issues we have with scaling and selection on a bitmap.
    private Bitmap matrix_scale(Bitmap orig,float scale_x,float scale_y,boolean overwrite){
        Matrix m = new Matrix();
        m.setScale(scale_x,scale_y);
        Bitmap b = Bitmap.createBitmap(orig,0,0,orig.getWidth(),orig.getHeight(),m,true);

        return b;
    }

    //Returns the original focused image but rotation based on its rotation angle.
    private Bitmap matrix_rotate(Bitmap orig){
        Matrix m = new Matrix();
        Bitmap b = null;
        m.setRotate(rotation);
        b = Bitmap.createBitmap(orig,0,0,orig.getWidth(),orig.getHeight(),m,true);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("SQUID CANVAS DOWN EVENT");

                //Here we need to set the
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("SQUID CANVAS UP EVENT");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("SQUID CANVAS MOVE EVENT");
                break;
        }
        return true;
    }

    //When we want to set a scaling bar, just send a seekbar ui widget into this function and it will set
    //the correct listenters.
    public void set_scaling_bar(SeekBar bar){
        this.scaling_bar = bar;

        this.scaling_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                image_matrix.setScale((float) i / 100,(float) i / 100);
                invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Show the confirm layout change button.
                success_layout.setVisibility(View.VISIBLE);
                invalidate();
            }
        });
    }

    //Generates the success layout so that we can
    public void set_success_layout(RelativeLayout lay){
        this.success_layout = lay;

        ImageButton success_btn = lay.findViewById(R.id.scaling_confirm_button);

        if(success_btn != null){
            success_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Now we want to change the focused image to a new scaled version.

                    invalidate();
                    success_layout.setVisibility(View.GONE);
                }
            });
        }
    }
}