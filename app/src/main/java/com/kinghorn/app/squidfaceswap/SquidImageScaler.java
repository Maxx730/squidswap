package com.kinghorn.app.squidfaceswap;

public class SquidImageScaler {

    public SquidCanvas focused_can;

    //Constructor.
    public SquidImageScaler(){

    }

    //Getter and setter methods begin here.
    public void set_focused(SquidCanvas c){
        focused_can = c;
    }
    public SquidCanvas get_focused(){return focused_can;}

    //Sets the scale for the image drawn to the selected canvas.
    public void set_scale(int s){
        focused_can.get_foc().scale_x = s + 1;
        focused_can.get_foc().scale_y = s + 1;
    }
}