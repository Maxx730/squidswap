package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

//Canvas class that we will use in this view to draw on top of the given image.
public class SquidCanvas extends View{
    //Private variables.
    private Context cn;
    public SquidBitmapData foc;
    private Paint select_paint;
    private ArrayList<SquidPath> paths;

    //Public variables that can be edited from outsite the
    //object.
    public float fade_val = .5f;
    public boolean CENTER_IMAGE = true;
    public boolean DEBUG_CAN = true;
    public boolean WATERMARK = false;

    //Check to see if the user is drawing to the canvas or not.
    public boolean drawing = false;
    public boolean cropping = false;
    public boolean draw_paint = true;
    public boolean can_select = true;
    public boolean scaling = false;

    //Selection data points.
    private int start_x,start_y,end_x,end_y;

    public SquidCanvas(Context con, SquidBitmapData f){
        super(con);
        //Set our variables.
        cn = con;
        foc = f;

        select_paint = new Paint();
        select_paint.setAntiAlias(true);
        select_paint.setStyle(Paint.Style.STROKE);
        select_paint.setColor(Color.WHITE);
        select_paint.setStrokeWidth(6);

        paths = new ArrayList<SquidPath>();

        //Grab caching information here.
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    //Getters and setters are below.
    public void set_img(Bitmap b){
        Bitmap final_bit = null;
        //Here we are going to want to check if we need to scale the image to the size of the canvas, if it is a background image
        //that is.
        DisplayMetrics d = cn.getResources().getDisplayMetrics();

        //We are going to want to check the width of the chosen image compared to the
        //width of the given squid canvas.
        if(b.getWidth() > d.widthPixels){
            double scale = (double) d.widthPixels / (double) b.getWidth();
            //Now we need to check how much to scale the image down from its original size
            //to fit within the canvas.
            Matrix m = new Matrix();
            m.setScale((float) scale,(float) scale);
            final_bit = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);
        }else{
            //Otherwise keep the size of the bitmap the same as it was.
            final_bit = b;
        }

        foc.set_undo(foc.bit);
        foc.set_bitmap(final_bit);
        invalidate();
    }

    public Bitmap get_img(){
        return foc.bit;
    }
    public SquidBitmapData get_foc(){return foc;};
    public void set_start(int x,int y){this.start_x = x;this.start_y = y;}
    public void set_end(int x,int y){this.end_x = x;this.end_y = y;}
    public void reset_vals(){this.start_x = 0;this.end_x = 0;this.start_y =0;this.end_y = 0;}
    public void set_paint_paths(ArrayList<SquidPath> p){paths = p;}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //If the focused image has any data then write the data to the canvas.
        if (foc.bit != null) {
            Bitmap scale = matrix_scale(foc.bit,foc.get_scale_x(),foc.get_scale_y());

            if(CENTER_IMAGE){
                canvas.drawBitmap(scale,((getWidth() - scale.getWidth()) / 2),((getHeight() - scale.getHeight()) / 2),null);
            }else{
               if(foc.is_fade){
                   canvas.drawBitmap(matrix_rotate(get_faded_img("circle")),foc.x,foc.y,null);
               }else {
                   canvas.drawBitmap(matrix_rotate(foc.bit),0,0, null);
               }
            }
        }

        //Logic that draws the paint to the canvas if we are using the canvas as a painting
        //tool.
        if(draw_paint){
            for(SquidPath p : paths){
                canvas.drawPath(p,p.getPaint());
            }
        }

        //Selection tool logic that will draw the selection box to the screen.
        if(can_select){
            boolean x_check = check_x();
            boolean y_check = check_y();

            //Always draw the selection here.
            //We also want to draw a crosshair when
            //selecting to indicate that we are selecting.
            if(drawing){
                if(x_check && y_check){
                    canvas.drawRect(end_x,end_y,start_x,start_y,select_paint);
                }else if(x_check && !y_check){
                    canvas.drawRect(end_x,start_y,start_x,end_y,select_paint);
                }else if(!x_check && y_check){
                    canvas.drawRect(start_x,end_y,end_x,start_y,select_paint);
                }else{
                    canvas.drawRect(start_x,start_y,end_x,end_y,select_paint);
                }
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

    public int center_x(Canvas c){
        return (getWidth() - foc.bit.getWidth()) / 2;
    }
    public int center_y(Canvas c){
        return (getHeight() - foc.bit.getHeight()) / 2;
    }

    //Returns the bitmap data that was obtained from the image selection based
    //on the given values in the hashmap.
    public Bitmap select_data(){
        select_paint.setColor(Color.TRANSPARENT);
        Bitmap orig = getDrawingCache();

        boolean x_check,y_check;

        x_check = check_x();
        y_check = check_y();
        Bitmap cropped;

        if(x_check && y_check){
            cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) end_x),(Integer) Math.round((float) end_y),(Integer) Math.round((float) start_x) - (Integer) Math.round((float) end_x),(Integer) Math.round((float) start_y) - (Integer) Math.round((float) end_y));
        }else if(x_check && !y_check){
            cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) end_x),(Integer) Math.round((float) start_y),(Integer) Math.round((float) start_x) - (Integer) Math.round((float) end_x),(Integer) Math.round((float) end_y) - (Integer) Math.round((float) start_y));
        }else if(!x_check && y_check){
            cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) start_x),(Integer) Math.round((float) end_y),(Integer) Math.round((float) end_x) - (Integer) Math.round((float) start_x),(Integer) Math.round((float) start_y) - (Integer) Math.round((float) end_y));
        }else{
            cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) start_x),(Integer) Math.round((float) start_y),(Integer) Math.round((float) end_x) - (Integer) Math.round((float) start_x),(Integer) Math.round((float) end_y) - (Integer) Math.round((float) start_y));
        }

        select_paint.setColor(Color.WHITE);
        can_select = false;
        return cropped;
    }

    //Returns the image after the fading on the edge differences have been applied.
    //As of now we are still working on the circular duffer fade functionality.
    public Bitmap get_faded_img(String type){
        Bitmap orig = foc.bit;
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(),Bitmap.Config.ARGB_8888);

        //Create a new transparent bitmap to start drawing multiple layers onto.
        Canvas c = new Canvas(b);
        Paint p = new Paint();
        //DST out layer mode to hide black and show trans.
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        //Color array of the shader. also with the anchor points for when to start and stop colors.
        final int[] cols = {Color.TRANSPARENT,Color.BLACK,Color.BLACK};
        final float[] ancs = {.6f,.8f,1f};
        //Create the gradient shader using the color and anchor arrays.
        RadialGradient r = new RadialGradient((orig.getWidth()) / 2,orig.getHeight() / 2,orig.getWidth() / fade_val,cols,ancs, Shader.TileMode.CLAMP);
        p.setShader(r);
        //Draw the original bitmap then draw the shader over it.
        c.drawBitmap(orig,0,0,null);
        //c.drawCircle(orig.getWidth() / 2f,orig.getHeight() / 2f,larger_dimen(orig.getWidth(),orig.getHeight()) / .4f,p);
        c.drawOval(new RectF((orig.getWidth() / 2) * -1,(orig.getHeight()) * -1,orig.getWidth() + (orig.getWidth() / 2),orig.getHeight() + (orig.getHeight() / 2)),p);
        return b;
    }

    //Checks the size of the selection.  If large enough then it returns true.
    private boolean check_select_size(){
        //Make sure we have values.
        if(this.start_x > 0 && this.start_y > 0 && this.end_x > 0 && this.end_y > 0){
            if(this.end_x - this.start_x > 10 && this.end_y - this.start_y > 10){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    //Checks the values of the start and end points and converts them based on
    //what they are.
    private boolean check_x(){
        if(start_x > end_x){
            return true;
        }else{
            return false;
        }
    }
    private boolean check_y(){
        if(start_y > end_y){
            return true;
        }else{
            return false;
        }
    }

    private float larger_dimen(float x, float y){
        if(x > y){
            return x;
        }else{
            return y;
        }
    }

    //Scales the image based on a matrix rather than scaling the canvas, this will probably
    //solve the issues we have with scaling and selection on a bitmap.
    private Bitmap matrix_scale(Bitmap orig,float scale_x,float scale_y){
        Matrix m = new Matrix();
        m.postScale(foc.get_scale_x(),foc.get_scale_y());

        Bitmap b = Bitmap.createBitmap(orig,0,0,orig.getWidth(),orig.getHeight(),m,true);

        return b;
    }

    //Returns the original focused image but rotation based on its rotation angle.
    private Bitmap matrix_rotate(Bitmap orig){
        Matrix m = new Matrix();
        Bitmap b = null;

        m.setRotate(foc.rotation_angle);
        b = Bitmap.createBitmap(orig,0,0,orig.getWidth(),orig.getHeight(),m,true);

        return b;
    }

    //Takes in the image, scales it to the size of the canvas (Rounds to the best it can) and also
    //applies
    private Bitmap return_matrixed(Bitmap b,Canvas c){
        Matrix m = new Matrix();

        m.setScale(foc.get_scale_x(),foc.get_scale_y());
        m.setRotate(foc.rotation_angle);

        Bitmap bmp = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);

        return bmp;
    }

    //Takes a bitmap and scales it down if it is larger than the given canvas size.
    private Bitmap scale_large_image(Bitmap b){

        return null;
    }
}