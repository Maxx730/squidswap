package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

//Class that will preview and be able to crop the  final image after everything has been placed.
public class SquidCropPreview extends View {

    private Context c;
    private Bitmap pre;

    public SquidCropPreview(Context con){
        super(con);

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
}
