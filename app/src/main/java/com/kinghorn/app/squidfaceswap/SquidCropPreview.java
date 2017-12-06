package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.util.HashMap;

//Class that will preview and be able to crop the  final image after everything has been placed.
public class SquidCropPreview extends View {

    private Context c;
    private Bitmap pre;

    public SquidCropPreview(Context con){
        super(con);

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);

        c = con;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(pre,0,0,null);
    }

    public void set_preview(Bitmap b){
        pre = b;
    }

    //Returns the bitmap data that was obtained from the image selection based
    //on the given values in the hashmap.
    public Bitmap select_data(HashMap vals){
        Bitmap orig = getDrawingCache();
        Bitmap cropped = Bitmap.createBitmap(orig,(Integer) Math.round((float) vals.get("start_x")),(Integer) Math.round((float) vals.get("start_y")),(Integer) Math.round((float) vals.get("end_x")) - (Integer) Math.round((float) vals.get("start_x")),(Integer) Math.round((float) vals.get("end_y")) - (Integer) Math.round((float) vals.get("start_y")));

        return cropped;
    }
}
