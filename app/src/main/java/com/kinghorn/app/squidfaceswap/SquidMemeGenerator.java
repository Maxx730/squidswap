package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.Toast;

//Class that will inherit from the squidcanvas but will also generate meme stuff over it
//rather than just be a standard squid canvas.
public class SquidMemeGenerator extends SquidCanvas {

    private Paint meme_paint,text_paint;
    private Bitmap meme_img;
    private String meme_text;
    private Context c;

    //Constructor.
    public SquidMemeGenerator(Context con, SquidBitmapData f) {
        super(con, f);

        //Initialize the color for the background of the memed image.
        meme_paint = new Paint();
        meme_paint.setColor(Color.TRANSPARENT);
        meme_paint.setStyle(Paint.Style.FILL);

        text_paint = new Paint();
        text_paint.setColor(Color.WHITE);
        text_paint.setStyle(Paint.Style.FILL);
        text_paint.setTextSize(40);
        text_paint.setAntiAlias(true);

        c = con;
    }

    //Override our ondraw method.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_meme_back(canvas);
        Bitmap scaled = scale_to_size(meme_img);
        canvas.drawBitmap(scaled,(getWidth() - scaled.getWidth()) / 2,((getHeight() - scaled.getHeight()) / 2) - 20,null);

    }

    //Getter and setter methods here.
    public void set_meme_img(Bitmap b){meme_img = b;}
    public Bitmap get_meme_img(){return meme_img;}
    public void set_meme_text(String t){meme_text = t;}
    public String get_meme_text(){return meme_text;}

    private void draw_meme_back(Canvas c){
        c.drawRect(new Rect(((c.getWidth() - meme_img.getWidth()) / 2) - 20,((c.getHeight() - meme_img.getHeight()) / 2) - 40,((c.getWidth() + meme_img.getWidth()) / 2) + 20,c.getHeight() - (meme_img.getHeight() / 2) + 320),meme_paint);
    }

    //Takes in a bitmap and scales to the correct width of the meme border using a matrix.
    private Bitmap scale_to_size(Bitmap b){
        Matrix m = new Matrix();
        int rat = (getWidth() / b.getWidth());
        m.postScale(rat,rat);
        Bitmap bit = Bitmap.createBitmap(meme_img,0,0,meme_img.getWidth(),meme_img.getHeight(),m,true);
        return bit;
    }
}
