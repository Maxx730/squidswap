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
    public boolean drawing,cropping;
    public boolean has_data;

    public SquidSelector(Context con){
        super(con);

        selection_data = new HashMap();
        drawing = false;
        has_data = false;
        cropping = false;

        set_start_values(0,0);
        set_end_values(0,0);

        selection_paint = new Paint();
        selection_paint.setStrokeWidth(6f);
        selection_paint.setStyle(Paint.Style.STROKE);
        selection_paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //If the user does have their finger down then we want to paint to the screen.
        if(drawing || cropping){
            canvas.drawRect((float) selection_data.get("start_x"),(float) selection_data.get("start_y"),(float) selection_data.get("end_x"),(float) selection_data.get("end_y"),selection_paint);
        }
    }

    //Resets the values of the selector to 0;
    public void zero_values(){
        set_start_values(0,0);
        set_end_values(0,0);
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
    public void convert_direction(){
        HashMap hash = new HashMap();

        float start_x = (float) selection_data.get("start_x");
        float start_y = (float) selection_data.get("start_y");
        float end_x = (float) selection_data.get("end_x");
        float end_y = (float) selection_data.get("end_y");

        if(end_x < start_x){
            float tmp = start_x;

            start_x = end_x;
            end_x = tmp;
        }

        if(end_y < start_y){
            float tmp = start_y;

            start_y = end_y;
            end_y = tmp;
        }

        hash.put("start_x",start_x);
        hash.put("start_y",start_y);
        hash.put("end_x",end_x);
        hash.put("end_y",end_y);

        selection_data = hash;
    }

    //Check the size of the selction area and deny it if it is
    //too small to even display anything.
    public boolean check_size(){
        if(((float) selection_data.get("end_x") - (float) selection_data.get("start_x") > 5) && ((float) selection_data.get("end_y") - (float) selection_data.get("start_y") > 5)){
            return true;
        }else{
            return false;
        }
    }

}
