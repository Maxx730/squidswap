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
import android.graphics.SweepGradient;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

import java.util.HashMap;


//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    private Context cn;
    private SquidBitmapData foc;
    public int fade_val = 5;

    public boolean CENTER_IMAGE = true;
    public boolean DEBUG_CAN = true;

    private SquidSelector sel;

    public SquidCanvas(Context con,SquidBitmapData f,SquidSelector s){
        super(con);
        cn = con;
        foc = f;
        sel = s;
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
                canvas.drawBitmap(scale,(float) cent.get("x"),(float) cent.get("y"),null);
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

    //Returns the image after the fading on the edge differences have been applied.
    public Bitmap get_faded_img(){
        Bitmap orig = foc.bit;
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        Paint p = new Paint();
        Paint p_top = new Paint();

        c.drawBitmap(orig,0,0,p);

        System.out.println((float)0.8f/fade_val);

        p.setAntiAlias(true);
        p_top.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        p_top.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        p.setShader(new LinearGradient(0,0,foc.bit.getWidth(),0,new int[]{Color.BLACK,Color.TRANSPARENT,Color.TRANSPARENT,Color.BLACK},new float[]{0.0f,(float)0.8f/fade_val,(1f - ((float)0.8f/fade_val)),1f}, Shader.TileMode.CLAMP));
        p_top.setShader(new LinearGradient(0,0,0,foc.bit.getHeight(),new int[]{Color.BLACK,Color.TRANSPARENT,Color.TRANSPARENT,Color.BLACK},new float[]{0.0f,(float)0.8f/fade_val,(1f - ((float)0.8f/fade_val)),1f}, Shader.TileMode.CLAMP));

        c.drawCircle(foc.bit.getWidth() / 4,foc.bit.getHeight() / 4,foc.bit.getWidth() + 200,p);
        c.drawCircle(foc.bit.getWidth() / 4,foc.bit.getHeight() / 4,foc.bit.getWidth() + 200,p_top);

        return b;
    }

    public void set_img(Bitmap b){
        foc.bit = b;
    }
}