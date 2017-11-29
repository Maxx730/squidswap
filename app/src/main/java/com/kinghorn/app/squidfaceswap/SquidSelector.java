package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.HashMap;

public class SquidSelector extends View {

    private Paint selection_paint;
    //Hashmap of start and end points.
    private HashMap selection_data;
    //Boolean for when the user is or is not pressing the screen.
    public boolean drawing;
    public boolean has_data;

    public SquidSelector(Context con){
        super(con);

        selection_data = new HashMap();
        drawing = false;
        has_data = false;

        selection_paint = new Paint();
        selection_paint.setStrokeWidth(6f);
        selection_paint.setStyle(Paint.Style.STROKE);
        selection_paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //If the user does have their finger down then we want to paint to the screen.
        if(drawing){
            canvas.drawRect((float) selection_data.get("start_x"),(float) selection_data.get("start_y"),(float) selection_data.get("end_x"),(float) selection_data.get("end_y"),selection_paint);
        }
    }

    //Sets the values for the hashmap used for where to draw onto the canvas.
    public void set_start_values(float x,float y){
        selection_data.put("start_x",x);
        selection_data.put("start_y",y);
    }

    //Sets the values for the hashmap used for where to draw onto the canvas.
    public void set_end_values(float x,float y){
        selection_data.put("end_x",x);
        selection_data.put("end_y",y);
    }

    //Get the selection values after the selection has occured.
    public HashMap select_values(){
        return selection_data;
    }

    //Checks the values of the start and end points and reverses them if need be
    //to properly draw the rectangle.
    private void convert_direction(){

    }
}
