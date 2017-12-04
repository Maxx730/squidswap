package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

import java.util.HashMap;


//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    private Context cn;
    private SquidBitmapData foc;
    public int fade_val = 50;

    public boolean CENTER_IMAGE = true;
    public boolean DEBUG_CAN = true;

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
            Bitmap scale = Bitmap.createScaledBitmap(foc.bit,Math.round(foc.bit.getWidth() * foc.scale_x),Math.round(foc.bit.getHeight() * foc.scale_y),true);
            //Use a hashmap to send our data to a function to return the values needed to place
            //the image in the center.
            HashMap vals = new HashMap();

            vals.put("width",(float) foc.bit.getWidth() * foc.scale_x);
            vals.put("height",(float) foc.bit.getHeight() * foc.scale_y);
            vals.put("canvas",canvas);

            if(CENTER_IMAGE){
                HashMap cent = return_center(vals);
                canvas.drawBitmap(foc.bit,(float) cent.get("x"),(float) cent.get("y"),null);
            }else{
                canvas.drawBitmap(get_faded_img(),foc.x,foc.y,null);
            }
        }
    }

    //Returns where the values to place the image in the center should be.
    private HashMap return_center(HashMap source){
        Canvas c = (Canvas) source.get("canvas");

        float w = (float) source.get("width");
        float h = (float) source.get("height");

        HashMap re = new HashMap<String,Float>();

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

    private Bitmap get_faded_img(){
        Bitmap orig = foc.bit;
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        Paint p = new Paint();

        c.drawBitmap(orig,0,0,p);

        p.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        p.setShader(new RadialGradient(foc.bit.getWidth()/2,foc.bit.getHeight()/2,(foc.bit.getWidth()/4) + (fade_val * 4),Color.TRANSPARENT,Color.BLACK,Shader.TileMode.CLAMP));

        c.drawCircle(foc.bit.getWidth() / 2,foc.bit.getHeight() / 2,foc.bit.getWidth(),p);

        return b;
    }

}