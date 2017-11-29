package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.HashMap;


//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    private Context cn;
    private SquidBitmapData foc;

    public SquidCanvas(Context con,SquidBitmapData f){
        super(con);
        cn = con;
        foc = f;

        //Grab caching information here.
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //If the focused image has any data then write the data to the canvas.
        if (foc.bit != null) {
            //Use a hashmap to send our data to a function to return the values needed to place
            //the image in the center.
            HashMap vals = new HashMap();

            vals.put("width",foc.width);
            vals.put("height",foc.height);
            vals.put("canvas",canvas);

            HashMap dat = return_center(vals);

            canvas.drawBitmap(foc.bit, (float) dat.get("x"), (float) dat.get("y"), null);
        }
    }

    //Returns where the values to place the image in the center should be.
    private HashMap return_center(HashMap source){
        Canvas c = (Canvas) source.get("canvas");

        float w = (float) source.get("width");
        float h = (float) source.get("height");

        HashMap re = new HashMap();

        re.put("x",(c.getWidth() - w) / 2);
        re.put("y",(c.getHeight() - h) / 2);

        return re;
    }

    //Returns the bitmap data that was obtained from the image selection based
    //on the given values in the hashmap.
    public Bitmap select_data(HashMap vals){
        Bitmap orig = getDrawingCache();
        Bitmap cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) vals.get("start_x")),(Integer) Math.round((float) vals.get("start_y")),(Integer) Math.round((float) vals.get("end_x")) - (Integer) Math.round((float) vals.get("start_x")),(Integer) Math.round((float) vals.get("end_y")) - (Integer) Math.round((float) vals.get("start_y")));

        return cropped;
    }
}