package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

//Class that gives the user the ability to draw onto a canvas as well as erase.
public class SquidPainter extends View {

    private SquidBitmapData foc_btn;
    private Bitmap canvas_bmp;
    private Paint brush_paint,change_paint,erase_paint;
    private SquidPath pat,erase_path;
    private float stroke_width;
    private ArrayList<SquidPath> paths;
    private Context con;
    private ArrayList<RelativeLayout> colors;

    //Boolean that tracks if the eraser is being used or not.
    public boolean erasing = false;

    public boolean drawing,width_change;

    public SquidPainter(Context context) {
        super(context);

        con = context;

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_AUTO);

        brush_paint = new Paint();
        brush_paint.setStyle(Paint.Style.STROKE);
        brush_paint.setAntiAlias(true);
        brush_paint.setStrokeWidth(10f);
        brush_paint.setStrokeCap(Paint.Cap.ROUND);
        brush_paint.setStrokeJoin(Paint.Join.ROUND);
        brush_paint.setColor(Color.parseColor("#FFFFFFFF"));

        change_paint = new Paint();
        change_paint.setStyle(Paint.Style.STROKE);
        change_paint.setAntiAlias(true);
        change_paint.setStrokeWidth(5f);
        change_paint.setColor(Color.WHITE);
        change_paint.setTextSize(40);
        change_paint.setAlpha(80);

        erase_paint = new Paint();
        erase_paint.setStyle(Paint.Style.STROKE);
        erase_paint.setAntiAlias(true);
        erase_paint.setStrokeCap(Paint.Cap.ROUND);
        erase_paint.setStrokeJoin(Paint.Join.ROUND);
        erase_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        erase_paint.setColor(Color.BLACK);

        erase_path = new SquidPath(Color.BLACK,10);
        pat = new SquidPath(Color.parseColor("#FFFFFFFF"),10);

        paths = new ArrayList<SquidPath>();

        drawing = false;
        width_change = false;

        colors = new ArrayList<RelativeLayout>();
    }

    //Getters and setters start here.
    public void set_foc(SquidBitmapData b){foc_btn = b;}
    public SquidBitmapData get_foc(){return foc_btn;}
    public void set_paint(Paint p){brush_paint = p;}
    public Paint get_paint(){return brush_paint;}
    public void set_stroke_width(int width){pat.setBrushsize(width);}
    public void set_brush_color(int col){pat.setColor(col);}
    public int path_length(){return paths.size();}
    public void clear_paint(){paths.clear();
        Toast.makeText(con,"Canvas Cleared",Toast.LENGTH_SHORT).show();invalidate();}
    public ArrayList<SquidPath> get_paths(){return paths;}
    public void add_to_colors(RelativeLayout r){colors.add(r);}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Now we need to draw the canvas bitmap to the canvas and
        //draw a path over it.
        for(SquidPath p : paths){
            canvas.drawPath(p,p.getPaint());
        }

        canvas.drawPath(pat,pat.getPaint());

        //When the stroke size is changed we want to indicate the size changing below.
        if(width_change){
            canvas.drawCircle(getWidth()/2,getHeight()/2,pat.getBrushSize(),change_paint);
        }
    }

    //Functions that will handle starting and moving the drawing path to
    //Different locations based on the user input.
    public void set_path_start(float x,float y){
        pat.moveTo(x,y);
    }

    public void move_path_to(float x,float y){
        pat.lineTo(x,y);
    }

    public void end_path(){
        SquidPath p = new SquidPath(pat.getColor(),pat.getBrushSize());
        p.set(pat);
        paths.add(p);
        pat.reset();
    }

    public boolean undo_last_paint(){
        if(paths.size() > 0){
            paths.remove(paths.size() - 1);
            invalidate();

            if(paths.size() == 0){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    //Returns a bitmap of what was painted with regards to the size of the actual
    //base image so that we are not saving a gigantic image with black background.
    private Bitmap get_painted_size(Bitmap org){
        Bitmap b = null;

        return b;
    }

    //Applies the paint layer on top of the normal canvas that is holding the
    //image.
    public Bitmap apply_paint(SquidCanvas orig){
        Bitmap b = Bitmap.createBitmap(orig.getWidth(),orig.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas fin = new Canvas(b);

        fin.drawBitmap(orig.getDrawingCache(),0,0,null);
        fin.drawBitmap(getDrawingCache(),0,0,null);

        return b;
    }

    public void reset_color_alpha(){
        for(RelativeLayout r : colors){
            r.setAlpha(.5f);
        }
    }

    
}
