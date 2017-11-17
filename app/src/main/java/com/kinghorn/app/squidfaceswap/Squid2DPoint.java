package com.kinghorn.app.squidfaceswap;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */

//SIMPLE CLASS FOR HANDLING 2D POINTS SINCE I CANNOT SEEM TO FIND
    //AN OBJECT THAT COULD HANDLE IT DECENTLY, NOT SURE IF THIS IS
    //GOING TO BE POOR ON MEMORY.
public class Squid2DPoint {
    private int x,y;

    public int get_x(){
     return this.x;
    }

    public int get_y(){
        return this.y;
    }

    public void set_x(int x){
        this.x = x;
    }

    public void set_y(int y){
        this.y = y;
    }
}
