package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SquidSelector extends View {

    private Paint selection_paint;
    public SquidSelectorRectangle rect;
    public boolean drawing,has_data;

    public SquidSelector(Context con){
        super(con);

        drawing = false;
        has_data = false;

        rect = new SquidSelectorRectangle();
        selection_paint = new Paint();
        selection_paint.setStrokeWidth(6f);
        selection_paint.setStyle(Paint.Style.STROKE);
        selection_paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(drawing && !has_data){
            canvas.drawRect(rect.start_x,rect.start_y,rect.end_x,rect.end_y,selection_paint);
        }else{
            System.out.println("Not drawing cropping selector since the tool already has data being cached...");
        }
    }

    //Funtion that will render the classic white square with corners for selecting data from within in an image.
    private void draw_selection(){

    }
}
