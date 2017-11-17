package com.kinghorn.app.squidfaceswap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapEditor extends AppCompatActivity{
    private Canvas draw_can;
    private Bitmap bit;
    private String chosen;
    private Uri path;
    private InputStream stream;
    private RelativeLayout edit;
    private SquidCanvas can;
    private LayoutInflater inflate;
    private LinearLayout main;
    private Button zoom_in,zoom_out,image_apply,reset_btn;
    private TextView zoom_indi;
    private boolean is_highlight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_squid_swap_editor);

        edit = (RelativeLayout) findViewById(R.id.editor_window);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        main = (LinearLayout) findViewById(R.id.canvas_window);
        zoom_in = (Button)findViewById(R.id.img_scale_inc);
        zoom_out = (Button)findViewById(R.id.img_scale_dec);
        image_apply = (Button)findViewById(R.id.apply_changes);
        reset_btn = (Button)findViewById(R.id.img_selection_reset);
        zoom_indi = (TextView) findViewById(R.id.zoom_indication);
        is_highlight = false;


        Intent src = getIntent();
        chosen = src.getStringExtra("chosen_uri");
        path = Uri.parse(chosen);

        try{
            BitmapFactory.Options op = new BitmapFactory.Options();
            stream = getContentResolver().openInputStream(path);
            bit = BitmapFactory.decodeStream(stream,null,op);
            can = new SquidCanvas(getApplicationContext());
            can.set_img(bit,op);

            zoom_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(can.get_scale_x() < 5 && can.get_scale_y() < 5){
                        int old_x = can.get_scale_x();
                        int old_y = can.get_scale_y();

                        can.set_scales(old_x + 1,old_y + 1);
                        zoom_indi.setText(can.get_scale_x()+"X");
                        can.invalidate();

                        if(can.get_scale_x() == 5){
                            zoom_in.setEnabled(false);
                        }else{
                            zoom_out.setEnabled(true);
                        }
                    }
                }
            });

            zoom_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(can.get_scale_x() > 1 && can.get_scale_y() > 1){
                        int old_x = can.get_scale_x();
                        int old_y = can.get_scale_y();

                        can.set_scales(old_x - 1,old_y - 1);
                        zoom_indi.setText(can.get_scale_x()+"X");
                        can.invalidate();

                        if(can.get_scale_x() == 1){
                            zoom_out.setEnabled(false);
                        }else{
                            zoom_in.setEnabled(true);
                        }
                    }
                }
            });

            image_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            reset_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    can.set_scales(1,1);
                    zoom_out.setEnabled(false);
                    zoom_indi.setText(can.get_scale_x()+"X");
                    can.invalidate();
                }
            });

            main.addView(can);

            can.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Long time = motionEvent.getEventTime() - motionEvent.getDownTime();

                    if(time > 4000){
                        is_highlight = true;
                    }

                    System.out.println(is_highlight);

                    switch(motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                                can.rect.set_start(motionEvent.getX(),motionEvent.getY());
                            break;
                        case MotionEvent.ACTION_UP:
                                can.rect.set_end(motionEvent.getX(),motionEvent.getY());
                                is_highlight = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                                can.rect.set_end(motionEvent.getX(),motionEvent.getY());
                            break;
                    }

                    can.invalidate();
                    return true;
                }
            });
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
