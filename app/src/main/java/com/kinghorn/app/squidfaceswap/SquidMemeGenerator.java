package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//Class that will inherit from the squidcanvas but will also generate meme stuff over it
//rather than just be a standard squid canvas.
public class SquidMemeGenerator extends SquidCanvas {

    private Paint meme_paint;
    private Bitmap meme_img;

    //Constructor.
    public SquidMemeGenerator(Context con, SquidBitmapData f) {
        super(con, f);

        //Initialize the color for the background of the memed image.
        meme_paint = new Paint();
        meme_paint.setColor(Color.BLACK);
        meme_paint.setStyle(Paint.Style.FILL);
    }

    //Override our ondraw method.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_meme_back(canvas);
        canvas.drawBitmap(meme_img,20,20,null);
    }

    //Getter and setter methods here.
    public void set_meme_img(Bitmap b){meme_img = b;}
    public Bitmap get_meme_img(){return meme_img;}

    private void draw_meme_back(Canvas c){
        c.drawRect(new Rect(0,0,meme_img.getWidth() + 40,meme_img.getHeight() + 240),meme_paint);
    }

    //Takes in a bitmap and scales to the correct width of the meme border using a matrix.
    private Bitmap scale_to_size(Bitmap b){
        return null;
    }
}
