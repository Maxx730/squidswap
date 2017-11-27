package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapEditor extends AppCompatActivity{
    private Bitmap bit;
    private String chosen;
    private Uri path;
    private InputStream stream;
    private SquidCanvas can;
    private RelativeLayout main;
    private Button image_apply;
    private ImageButton can_btn,acc_btn,zoom_in,zoom_out,reset_btn;
    private TextView zoom_indi;
    private SquidSelector select;
    private SquidFileService file_serv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_squid_swap_editor);
        //Create all of the elements and views from the layout xml.
        main = (RelativeLayout) findViewById(R.id.canvas_window);
        zoom_in = (ImageButton)findViewById(R.id.img_scale_inc);
        zoom_out = (ImageButton)findViewById(R.id.img_scale_dec);
        reset_btn = (ImageButton)findViewById(R.id.img_selection_reset);
        zoom_indi = (TextView) findViewById(R.id.zoom_indication);
        file_serv = new SquidFileService();
        acc_btn = (ImageButton) findViewById(R.id.acc_button);
        can_btn = (ImageButton) findViewById(R.id.can_btn);

        //Grab info from the selected image.
        Intent src = getIntent();
        chosen = src.getStringExtra("chosen_uri");
        path = Uri.parse(chosen);

        try{
            //Create the bitmap image that was chosen to put into the
            //image drawing canvas.
            BitmapFactory.Options op = new BitmapFactory.Options();
            stream = getContentResolver().openInputStream(path);
            bit = BitmapFactory.decodeStream(stream,null,op);

            //Create both of our canvases.
            select = new SquidSelector(getApplicationContext());
            can = new SquidCanvas(getApplicationContext(),bit,op,select);

            //Save the image to the gallery (for not) and then open up the next image to swap something to.
            acc_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Saving cropped image...");


                }
            });

            //Cancel logic if the user does not want to keep the data for the
            //cropped or worked image etc.
            can_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Canceling selection...");
                    can_btn.setVisibility(View.GONE);
                    acc_btn.setVisibility(View.GONE);

                    can.image_back();
                    select.has_data = false;
                }
            });

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
                            zoom_in.setAlpha(.5f);
                        }else{
                            zoom_out.setEnabled(true);
                            zoom_out.setAlpha(1f);
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
                            zoom_out.setAlpha(.5f);
                        }else{
                            zoom_in.setEnabled(true);
                            zoom_in.setAlpha(1f);
                        }
                    }
                }
            });

            reset_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    can.set_scales(1,1);
                    zoom_out.setEnabled(false);
                    zoom_in.setEnabled(true);
                    zoom_indi.setText(can.get_scale_x()+"X");
                    can.invalidate();
                }
            });

            main.addView(can);
            main.addView(select);

            select.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch(motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            select.drawing = true;
                            select.rect.set_start(motionEvent.getX(),motionEvent.getY());
                            select.rect.set_end(motionEvent.getX(),motionEvent.getY());
                            break;
                        case MotionEvent.ACTION_UP:
                            select.drawing = false;
                            System.out.println("Beggining of saving image.");
                            //file_serv.save_image(can.return_bmp_data());

                            select.rect.set_end(motionEvent.getX(),motionEvent.getY());

                            //Only redraw the new image if the the user hasnt already selected something.
                            if(!select.has_data){
                                //First we should display the data in the canvas to make sure this is the image the user wanted to capture.
                                can.set_img(can.return_bmp_data());
                                //After the initial selection set the has data value to true.
                                select.has_data = true;

                                can_btn.setVisibility(View.VISIBLE);
                                acc_btn.setVisibility(View.VISIBLE);
                            }else{
                                //If the user has already selected something then we want to ask if this is what they wanted to save
                                //if they say no we want to revert the cached data and go back to the main scene.

                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(!select.has_data){
                                select.rect.set_end(motionEvent.getX(),motionEvent.getY());
                                System.out.println("Start X:"+select.rect.start_x+" End X:"+select.rect.end_x+" Start Y:"+select.rect.start_y+" End Y:"+select.rect.end_y);
                            }else{

                            }

                            break;
                    }

                    select.invalidate();
                    return true;
                }
            });

        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Function that will run a check everytime this activity is loaded to check if there has
    //be data already cached to be used on a different image.
    private boolean check_cached_data(){

        return true;
    }
}
