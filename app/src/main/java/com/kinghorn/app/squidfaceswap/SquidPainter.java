package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
//Class that gives the user the ability to draw onto a canvas as well as erase.
public class SquidPainter extends View {

    private SquidBitmapData foc_btn;
    private Paint brush_paint;

    public SquidPainter(Context context) {
        super(context);

        brush_paint = new Paint();
        brush_paint.setStyle(Paint.Style.FILL);
        brush_paint.setColor(Color.RED);
    }

    //Getters and setters start here.
    public void set_foc(SquidBitmapData b){foc_btn = b;}
    public SquidBitmapData get_foc(){return foc_btn;}
    public void set_paint(Paint p){brush_paint = p;}
    public Paint get_paint(){return brush_paint;}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint(canvas);
    }

    private void paint(Canvas c){
        c.drawCircle(100f,100f,55f,brush_paint);
    }
}
