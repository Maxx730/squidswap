package com.kinghorn.app.squidfaceswap;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.Toast;

public class SquidCropper extends SquidCanvas {
    //Points we use when cropping.
    private int start_x,end_x,start_y,end_y;
    private boolean can_crop = true,dragging = false;
    private ImageButton undo;
    private Paint select_paint;

    public SquidCropper(Context con) {
        super(con);
        //Initialize selection paint.
        select_paint = new Paint();
        select_paint.setAntiAlias(true);
        select_paint.setStyle(Paint.Style.FILL);
        select_paint.setColor(Color.parseColor("#800080"));
        select_paint.setAlpha(70);
    }

    //Constructor that includes an undo button, might as well have an option to not require it.
    public SquidCropper(Context con,ImageButton undo_crop) {
        super(con);
        //Initialize selection paint.
        select_paint = new Paint();
        select_paint.setAntiAlias(true);
        select_paint.setStyle(Paint.Style.FILL);
        select_paint.setColor(Color.parseColor("#800080"));
        select_paint.setAlpha(70);

        this.undo = undo_crop;
        this.undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                focused = last;
                invalidate();
                undo.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(this.dragging && selection_size_check()){
            canvas.drawRect(this.start_x,this.start_y,this.end_x,this.end_y,select_paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(this.can_crop){
                    set_start(Math.round(event.getX()),Math.round(event.getY()));
                    this.set_drag(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(this.undo != null){
                    this.undo.setVisibility(View.VISIBLE);
                }
                this.set_drag(false);
                this.set_img(this.select_data());
                this.reset_vals();
                break;
            case MotionEvent.ACTION_MOVE:
                if(this.dragging){
                    set_end(Math.round(event.getX()),Math.round(event.getY()));
                }
                break;
        }
        //Redraw our new canvas based on events.
        invalidate();
        return super.onTouchEvent(event);
    }

    //We want to check the points before we select the data from the parent
    //Squidcanvas class to make sure we are not trying to crop zero values.
    private Bitmap select_data(){
        //Now that we have checked our values we want to return a new value to the parent
        //Squidcanvas as a Bitmap based on these values.
        Bitmap orig = focused;

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

        return cropped;
    }

    private boolean selection_size_check(){
        //Set these variables based on the value we gathered from
        int sx,sy,ex,ey;

        if(this.start_x < this.end_x){
            sx = this.start_x;
            ex = this.end_x;
        }else{
            sx = this.end_x;
            ex = this.start_x;
        }

        if(this.start_y < this.end_y){
            sy = start_y;
            ey = this.end_y;
        }else{
            sy = this.end_y;
            ey = this.start_y;
        }

        int width = ex - sx;
        int height = ey - sy;

        if(width * height > 10){
            return true;
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

    //Getter and setter for points as well as a reset function.
    public void set_start(int x,int y){this.start_x = x;this.start_y = y;}
    public void set_end(int x,int y){this.end_x = x;this.end_y = y;}
    public void reset_vals(){this.start_x = 0;this.end_x = 0;this.start_y =0;this.end_y = 0;}
    public void set_crop(boolean val){this.can_crop = val;}
    public void set_drag(boolean val){this.dragging = val;}
}
