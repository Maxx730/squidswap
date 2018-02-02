package com.kinghorn.app.squidfaceswap;

import android.graphics.Paint;
import android.graphics.Path;

//Simple class that uses the path class to store information about
//what the user is drawing on ze screen.
public class SquidPath extends Path{
    private Paint brushPaint;

    //Constructor
    public SquidPath(int color,float size){
        brushPaint = new Paint();

        brushPaint.setStrokeCap(Paint.Cap.ROUND);
        brushPaint.setStrokeJoin(Paint.Join.ROUND);
        brushPaint.setStrokeWidth(size);
        brushPaint.setAntiAlias(true);
        brushPaint.setStyle(Paint.Style.STROKE);
        brushPaint.setColor(color);
    }

    //Getters and Setters.
    public void setColor(int color){brushPaint.setColor(color);}
    public void setBrushsize(int size){brushPaint.setStrokeWidth(size);}
    public Paint getPaint(){return brushPaint;}
    public int getColor(){return brushPaint.getColor();}
    public float getBrushSize(){return brushPaint.getStrokeWidth();}
    public void setPaint(Paint p){brushPaint = p;}

}
