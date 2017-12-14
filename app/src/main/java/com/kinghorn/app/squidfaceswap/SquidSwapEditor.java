package com.kinghorn.app.squidfaceswap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SquidSwapEditor extends AppCompatActivity{
    public SquidCanvas can,pre,bas;
    public SquidPainter draw;
    public SquidSelector sel;
    public SquidMovementHandler mov;
    public RelativeLayout window;
    public SquidEditorUi edit;
    public SquidFileService fil;
    public SquidImageScaler scal;
    public SquidCropper crop;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Set the selected image from the gallery and set it to the background image canvas.
        if(resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            //Make sure the file exists.
            try {
                //Grab the chosen image from the gallery.
                InputStream in = getContentResolver().openInputStream(path);
                Bitmap b = BitmapFactory.decodeStream(in);
                //Set the base image canvas to the second chosen image.
                bas.set_img(b);
                mov.set_foc(can.get_foc());

                edit.layer_toggle.setVisibility(View.VISIBLE);
                mov.setVisibility(View.VISIBLE);
                edit.toggle_plac_btn_display(View.VISIBLE);
                edit.toggle_seek_display(View.VISIBLE);
                scal.set_focused(can);
                edit.zoom_seek.setProgress(0);

                edit.toggle_crop_btn_display(View.GONE);

                edit.hint_text.setText("Please place the swap image.");
                can.CENTER_IMAGE = false;
            } catch (FileNotFoundException e) {
                System.out.println("Sorry the selected file was not found.");
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squid_swap_editor);

        //Find the canvas window.
        window = (RelativeLayout) findViewById(R.id.canvas_window);

        //Make sure we can get to the file with a try catch.
        try{
            //Once we have the file, then we want to send it into the first canvas.
            sel = new SquidSelector(getApplicationContext());
            scal = new SquidImageScaler();

            //Three different squid canvases that are going to handle different parts of
            //the final image.
            //
            //Each of the canvases has its own focused bitmap img.
            can = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
            pre = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
            bas = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
            crop = new SquidCropper(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
            draw = new SquidPainter(getApplicationContext());
            //Init our file service.
            fil = new SquidFileService(getApplicationContext(),can,bas,sel);

            //Initialize the scaler with the initial squid canvas for cropping
            scal.set_focused(can);

            //Set the first image that was chosen from the gallery.
            can.set_img(fil.open_first(getIntent()));

            //Initialize the object that will handle moving the faded image.
            mov = new SquidMovementHandler(getApplicationContext(),can);
            //Object that handles most of the user interface interaction across the application.
            edit = new SquidEditorUi(getApplicationContext(),getWindow().getDecorView(),sel,this,fil,can,pre,mov,bas,scal,draw);

            //Add the canvas view to the window.
            //ORDER OF ADDING THESE IS IMPORTANT FOR CAPTURING USER INPUT.
            window.addView(bas);
            window.addView(can);
            window.addView(pre);
            window.addView(mov);
            window.addView(draw);
            window.addView(crop);

            //Hide the movement canvas that will be the top most, but we need to be able to select.
            mov.setVisibility(View.GONE);
            pre.setVisibility(View.GONE);
            draw.setVisibility(View.GONE);
            crop.setVisibility(View.GONE);

            //Initialize selection touch events.
            init_selector();
        }catch(FileNotFoundException e){
            //Unable to find the given file, we may want to setup some sort of fallback method to use as opposed to just throwing and
            //error.
            System.out.println("ERROR: Chosen file does not seem to exist!");
        }
    }

    //Initialize the on touch listener for the selection object.
    private void init_selector(){
        can.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        can.drawing = true;
                        can.set_start((int) motionEvent.getX(),(int) motionEvent.getY());
                        can.set_end((int) motionEvent.getX(),(int) motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        can.set_end((int) motionEvent.getX(),(int) motionEvent.getY());
                        can.set_img(can.select_data());
                        can.reset_vals();
                        edit.toggle_crop_btn_display(View.VISIBLE);
                        can.drawing = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        can.set_end((int) motionEvent.getX(),(int) motionEvent.getY());
                        break;
                }

                can.invalidate();
                return true;
            }
        });

        crop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                return true;
            }
        });

        //When the movement handler canvas is displaying, we want to capture events on where
        //to move the image, we also are going to want to tell the image canvas not to center the image.
        mov.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        SquidBitmapData b = mov.get_foc();
                        b.set_loc(motionEvent.getX() - (b.width / 2),motionEvent.getY() - (b.height / 2));
                        mov.set_foc(b);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        SquidBitmapData m = mov.get_foc();
                        m.set_loc(motionEvent.getX() - (m.width / 2),motionEvent.getY() - (m.height / 2));
                        mov.set_foc(m);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }

                mov.invalidate();
                can.invalidate();

                return true;
            }
        });

        draw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        draw.drawing = true;
                        draw.set_path_start(motionEvent.getX(),motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        draw.move_path_to(motionEvent.getX(),motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        draw.drawing = false;
                        draw.end_path();
                        break;
                }

                draw.invalidate();

                return true;
            }
        });
    }

    //Takes an  intent to choose from the gallery and starts the intent.
    public void start_gal(Intent in){
        startActivityForResult(in,1);
    }
}
