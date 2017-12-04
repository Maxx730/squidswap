package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

//Class that extends a View to show the base image that the
//swap is going to be on.
public class SquidBaseImage extends View {

    private Bitmap base_image;

    public int orig_width,orig_height;

    public SquidBaseImage(Context context) {
        super(context);

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
            canvas.drawBitmap(base_image,(canvas.getWidth() - base_image.getWidth())/2,(canvas.getHeight() - base_image.getHeight())/2,null);
        }
    }

    public void set_image(Bitmap img){
        base_image = img;
        invalidate();
    }

    public Bitmap get_base(){
        Bitmap b = getDrawingCache();
        System.out.println(b.toString());
        return getDrawingCache();
    }
}
