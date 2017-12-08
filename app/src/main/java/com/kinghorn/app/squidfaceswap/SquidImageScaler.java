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


}