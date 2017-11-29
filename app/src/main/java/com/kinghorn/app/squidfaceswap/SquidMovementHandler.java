package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

//Class that will draw a movement indicator over the image and move  the image based on
//mouse events.
public class SquidMovementHandler extends View {

    private SquidCanvas canvas;
    private Paint brush;

    public SquidMovementHandler(Context context,SquidCanvas can) {
        super(context);

        canvas = can;

        brush = new Paint();
        brush.setColor(Color.WHITE);
        brush.setStrokeWidth(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        draw_movement_indicator(canvas);
        invalidate();
    }



    //Draws the movement indicator onto a canvas.
    public void draw_movement_indicator(Canvas c){
        //We need to take a point such as a mouse event and them
        //calculate the corners based on the point that is touched.
    }
}
