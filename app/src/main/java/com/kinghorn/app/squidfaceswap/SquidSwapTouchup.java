package com.kinghorn.app.squidfaceswap;


import android.content.Context;
import android.graphics.Canvas;
import android.widget.Toast;

import java.util.ArrayList;

//Class that will be used as a canvas to touch up photos by painting and porterduff
//on a canvas and applying it to the foreground canvas.
public class SquidSwapTouchup extends SquidCanvas{
    private Context cont;
    //Can be null
    private SquidBitmapData bit;
    private ArrayList<SquidPath> paths = new ArrayList<SquidPath>();
    public SquidSwapTouchup(Context con, SquidBitmapData f) {
        super(con, f);

        this.cont = con;
        this.bit = f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Toast.makeText(this.cont,"drawing on canvas",Toast.LENGTH_SHORT).show();
    }
}
