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
    private SquidBitmapData dat;
    private Paint brush;

    public SquidMovementHandler(Context context,SquidCanvas can) {
        super(context);
        canvas = can;

        //Paint object that will handle painting the movement indicators to the
        //image in certain colors etc.
        brush = new Paint();
        brush.setColor(Color.parseColor("#B2FC05"));
        brush.setStrokeWidth(3);
    }

    //Getters and setters below.
    public void set_foc(SquidBitmapData d){
        System.out.println("Image Scale:"+d.get_scale_x());
        dat = d;
    }
    public SquidBitmapData get_foc(){
        return dat;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(dat.can_move){
            draw_movement_indicator(canvas);
        }

        invalidate();
    }

    //Draws the movement indicator onto a canvas.
    public void draw_movement_indicator(Canvas c){
        //We need to take a point such as a mouse event and them
        //calculate the corners based on the point that is touched.
        float width = dat.width;
        float height = dat.height;
        float cwidth = c.getWidth();
        float cheight = c.getHeight();

        float startx = dat.x;
        float starty = dat.y;

        if(check_canvas_bitmap_width(c)){
            //If the image scaling is larger,
            //then we just want to draw the indicator to the edges of
            //the movement canvas.

        }else{
            //Draws top left
            c.drawLine(startx,starty,startx + (width / 4),starty,brush);
            c.drawLine(startx,starty,startx,starty + (height / 4),brush);

            //Draws bottom left
            c.drawLine(startx,starty + height,startx + (width / 4),starty + height,brush);
            c.drawLine(startx,starty + height,startx,(starty + height) - (height / 4),brush);

            //Draws top right
            c.drawLine(startx + width,starty,(startx + width) - (width / 4),starty,brush);
            c.drawLine(startx + width,starty,startx + width,starty + (height / 4),brush);

            //Draws bottom right
            c.drawLine(startx + width,starty + height,(startx + width) - (width / 4),starty + height,brush);
            c.drawLine(startx + width,starty + height,(startx + width),(starty + height) - (height / 4),brush);

            //Draw center cross
            c.drawLine((startx + (width / 2)) + (width / 8),starty + (height / 2),(startx + (width / 2)) - (width / 8),starty + (height / 2),brush);
            c.drawLine(startx + (width / 2),((starty + (height / 2)) + (height / 8)),startx + (width / 2),((starty + (height / 2)) - (height / 8)),brush);
        }
    }

    //Function will return a boolean if the image is larger or smaller than the given canvas
    //width/height
    public boolean check_canvas_bitmap_width(Canvas c){
        float size_width = dat.width * dat.scale_x;
        float size_height = dat.height * dat.scale_y;

        if(size_width > c.getWidth()){
            return true;
        }else{
            return false;
        }
    }
}
