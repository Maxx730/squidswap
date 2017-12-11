package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
//Class that gives the user the ability to draw onto a canvas as well as erase.
public class SquidPainter extends View {

    private SquidBitmapData foc_btn;
    private Paint brush_paint,change_paint;
    private Path pat;
    private float stroke_width;

    public boolean drawing,width_change;

    public SquidPainter(Context context) {
        super(context);

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_AUTO);

        brush_paint = new Paint();
        brush_paint.setStyle(Paint.Style.STROKE);
        brush_paint.setAntiAlias(true);
        brush_paint.setStrokeWidth(10f);
        brush_paint.setColor(Color.BLUE);

        change_paint = new Paint();
        change_paint.setStyle(Paint.Style.STROKE);
        change_paint.setAntiAlias(true);
        change_paint.setStrokeWidth(5f);
        change_paint.setColor(Color.WHITE);
        change_paint.setTextSize(40);
        change_paint.setAlpha(80);

        drawing = false;
        width_change = false;
    }

    //Getters and setters start here.
    public void set_foc(SquidBitmapData b){foc_btn = b;}
    public SquidBitmapData get_foc(){return foc_btn;}
    public void set_paint(Paint p){brush_paint = p;}
    public Paint get_paint(){return brush_paint;}
    public void set_stroke_width(float width){brush_paint.setStrokeWidth(width);}
    public void set_brush_color(int col){brush_paint.setColor(col);}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(drawing){
            canvas.drawPath(pat,brush_paint);
        }else{
            //If we are not currently drawing we want to grab the drawing cache to keep
            //track of what has already been drawn.
        }

        if(width_change){
            canvas.drawCircle(getWidth()/2,getHeight()/2,brush_paint.getStrokeWidth(),change_paint);
            //canvas.drawText(Integer.toString((int) brush_paint.getStrokeWidth()),getWidth()/2,getHeight()/2,change_paint);
        }

    }

    //Functions that will handle starting and moving the drawing path to
    //Different locations based on the user input.
    public void set_path_start(float x,float y){
        pat = new Path();
        pat.moveTo(x,y);
    }

    public void move_path_to(float x,float y){
        pat.lineTo(x,y);
    }
}
