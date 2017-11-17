package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */


//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    private Bitmap bit;
    private BitmapFactory.Options op;
    private DisplayMetrics met;
    private Context cn;
    private Paint select_paint;

    private int x_scale;
    private int y_scale;

    public SquidSelectorRectangle rect;

    public SquidCanvas(Context con){
        super(con);

        this.set_scales(4,4);
        select_paint = new Paint();
        select_paint.setColor(Color.parseColor("#00FF00"));
        select_paint.setAlpha(50);
        rect = new SquidSelectorRectangle();
        cn = con;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        Bitmap scaled = Bitmap.createScaledBitmap(bit,op.outWidth * x_scale,op.outHeight * y_scale,false);
        canvas.drawBitmap(scaled,(canvas.getWidth() - (op.outWidth * x_scale)) / 2,(canvas.getHeight() - (op.outHeight * y_scale)) / 2,null);
        canvas.drawRect(rect.start_x,rect.start_y,rect.end_x,rect.end_y,select_paint);
    }

    public void set_img(Bitmap b, BitmapFactory.Options o){
        bit = b;
        op = o;
    }

    public void set_scales(int x,int y){
        this.x_scale = x;
        this.y_scale = y;
    }

    public int get_scale_x(){
        return this.x_scale;
    }

    public int get_scale_y(){
        return this.y_scale;
    }
}