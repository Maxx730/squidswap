package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.util.HashMap;

//Class that extends a View to show the base image that the
//swap is going to be on.
public class SquidBaseImage extends View {

    private Bitmap base_image;
    public SquidBitmapData bit;

    public int orig_width,orig_height;

    public SquidBaseImage(Context context,SquidBitmapData b) {
        super(context);

        bit = b;

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    //Create optional constructor.
    public SquidBaseImage(Context context,Bitmap base){
        super(context);

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        set_image(base);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(base_image != null){
            bit.x = (canvas.getWidth() - base_image.getWidth())/2;
            bit.y = (canvas.getHeight() - base_image.getHeight())/2;
            bit.width = base_image.getWidth();
            bit.height = base_image.getHeight();
            bit.set_bitmap(base_image);

            canvas.drawBitmap(base_image,(canvas.getWidth() - base_image.getWidth())/2,(canvas.getHeight() - base_image.getHeight())/2,null);
        }
    }

    public void set_image(Bitmap img){
        base_image = img;
        invalidate();
    }

    public Bitmap get_base(){
        Bitmap b = getDrawingCache();
        return getDrawingCache();
    }

    //Returns the points to draw selection indicators around the given bitmap.
    public HashMap return_points(){
        HashMap m = new HashMap();

        return m;
    }
}
