package com.kinghorn.app.squidfaceswap;

import java.util.Vector;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */

public class SquidSelectorRectangle {

    public float start_x;
    public float start_y;

    public float end_x;
    public float end_y;

    public void set_start(float x,float y){
        this.start_x = x;
        this.start_y = y;
    }

    public void set_end(float x,float y){
        this.end_x = x;
        this.end_y = y;
    }

    public void log_update(){
        System.out.println("STARTING POINT: "+this.start_x+" - "+this.start_y+"    ENDING POINT: "+this.end_x+" - "+this.end_y);
    }
}
