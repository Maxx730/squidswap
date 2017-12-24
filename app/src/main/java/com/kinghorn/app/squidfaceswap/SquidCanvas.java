package com.kinghorn.app.squidfaceswap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.RelativeLayout;

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
    public int fade_val = 5;
    public boolean CENTER_IMAGE = true;
    public boolean DEBUG_CAN = true;

    //Check to see if the user is drawing to the canvas or not.
    public boolean drawing = false;
    public boolean cropping = false;
    public boolean draw_paint = true;
    public boolean can_select = true;

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
        foc.set_undo(foc.bit);
        foc.set_bitmap(b);
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
            Bitmap scale = Bitmap.createScaledBitmap(foc.bit,Math.round(foc.bit.getWidth() * foc.scale_x),Math.round(foc.bit.getHeight() * foc.scale_y),true);
            //Use a hashmap to send our data to a function to return the values needed to place
            //the image in the center.
            HashMap vals = new HashMap();

            canvas.scale(foc.scale_x,foc.scale_y);

            vals.put("width",(float) foc.bit.getWidth() * foc.scale_x);
            vals.put("height",(float) foc.bit.getHeight() * foc.scale_y);
            vals.put("canvas",canvas);

            if(CENTER_IMAGE){
                HashMap cent = return_center(vals);

                foc.x = (float) cent.get("x");
                foc.y = (float) cent.get("y");

                canvas.drawBitmap(matrix_rotate(foc.bit),((getWidth() * foc.scale_x) - (foc.bit.getWidth() * foc.scale_x)) / 2,(getHeight() - foc.bit.getHeight()) / 2,null);
            }else{
               if(foc.is_fade){
                   canvas.drawBitmap(matrix_rotate(get_faded_img("square")),foc.x - (foc.bit.getWidth() / 4),foc.y - (foc.bit.getHeight() / 4),null);
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
            if(drawing && check_select_size()){
                select_paint.setStrokeWidth(6 / foc.scale_x);

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

        canvas.rotate(foc.rotation_angle);
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

        select_paint.setColor(ContextCompat.getColor(this.cn,R.color.colorPrimary));
        return cropped;
    }

    //Returns the image after the fading on the edge differences have been applied.
    //As of now we are still working on the circular duffer fade functionality.
    public Bitmap get_faded_img(String type){
        Bitmap orig = foc.bit;
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        Paint p = new Paint();
        Paint p_top = new Paint();

        c.drawBitmap(orig,0,0,p);

        p.setAntiAlias(true);
        p_top.setAntiAlias(true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        p_top.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if(type == "circle"){
            p.setShader(new RadialGradient(foc.bit.getWidth() / 2,foc.bit.getHeight() / 2,5,new int[]{Color.BLACK,Color.TRANSPARENT},new float[]{0.0f,1f}, Shader.TileMode.CLAMP));
            c.drawCircle(0,0,1,p);
        }else{
            p.setShader(new LinearGradient(0,0,foc.bit.getWidth(),0,new int[]{Color.BLACK,Color.TRANSPARENT,Color.TRANSPARENT,Color.BLACK},new float[]{0.0f,(float)0.6f/fade_val,(1f - ((float)0.8f/fade_val)),1f}, Shader.TileMode.CLAMP));
            p_top.setShader(new LinearGradient(0,0,0,foc.bit.getHeight(),new int[]{Color.BLACK,Color.TRANSPARENT,Color.TRANSPARENT,Color.BLACK},new float[]{0.0f,(float)0.6f/fade_val,(1f - ((float)0.8f/fade_val)),1f}, Shader.TileMode.CLAMP));

            c.drawRect(0,0,foc.bit.getWidth(),foc.bit.getHeight(),p);
            c.drawRect(0,0,foc.bit.getWidth(),foc.bit.getHeight(),p_top);
        }

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

    //Scales the image based on a matrix rather than scaling the canvas, this will probably
    //solve the issues we have with scaling and selection on a bitmap.
    private Bitmap matrix_scale(Bitmap orig,float scale_x,float scale_y){
        Matrix m = new Matrix();
        m.setScale(scale_x,scale_y);

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
}