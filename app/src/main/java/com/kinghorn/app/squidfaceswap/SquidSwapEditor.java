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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class SquidSwapEditor extends AppCompatActivity{
    public SquidCanvas can,pre,bas;
    public SquidSelector sel;
    public SquidMovementHandler mov;
    public RelativeLayout window;
    public SquidEditorUi edit;
    public SquidFileService fil;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Set the selected image from the gallery and set it to the background image canvas.
        if(resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            //Make sure the file exists.
            try {
                InputStream in = getContentResolver().openInputStream(path);
                Bitmap b = BitmapFactory.decodeStream(in);

                bas.set_img(b);

                mov.setVisibility(View.VISIBLE);
                edit.toggle_crop_btn_display(View.GONE);
                edit.toggle_plac_btn_display(View.VISIBLE);
                edit.toggle_seek_display(View.VISIBLE);
                edit.zoom_out.setVisibility(View.GONE);
                edit.zoom_in.setVisibility(View.GONE);
                edit.zoom_am.setVisibility(View.GONE);
                edit.layer_toggle.setVisibility(View.VISIBLE);

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

            //Three different squid canvases that are going to handle different parts of
            //the final image.
            //
            //Each of the canvases has its own focused bitmap img.
            can = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()),sel);
            pre = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()),sel);
            bas = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()),sel);
            //Init our file service.
            fil = new SquidFileService(can,bas,sel);

            //Set the first image that was chosen from the gallery.
            can.set_img(fil.open_first(getIntent()));

            mov = new SquidMovementHandler(getApplicationContext(),can,focused);
            edit = new SquidEditorUi(getApplicationContext(),getWindow().getDecorView(),focused,sel,this,fil,can,pre,mov,bas);

            //Add the canvas view to the window.
            //ORDER OF ADDING THESE IS IMPORTANT FOR CAPTURING USER INPUT.
            window.addView(bas);
            window.addView(can);
            window.addView(pre);
            window.addView(sel);
            window.addView(mov);

            //Hide the movement canvas that will be the top most, but we need to be able to select.
            mov.setVisibility(View.GONE);
            pre.setVisibility(View.GONE);

            //Initialize selection touch events.
            init_selector(sel);
        }catch(FileNotFoundException e){
            System.out.println("ERROR: Chosen file does not seem to exist!");
        }
    }

    //Initialize the on touch listener for the selection object.
    private void init_selector(final SquidSelector s){
        s.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(!s.has_data){
                            s.drawing = true;
                            s.set_start_values(motionEvent.getX(),motionEvent.getY());
                            s.set_end_values(motionEvent.getX(),motionEvent.getY());
                        }else if(s.cropping){
                            s.drawing = true;
                            s.set_start_values(motionEvent.getX(),motionEvent.getY());
                            s.set_end_values(motionEvent.getX(),motionEvent.getY());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!s.has_data){
                            s.drawing = false;
                            s.set_end_values(motionEvent.getX(),motionEvent.getY());
                            //Logic goes here for grabing the bitmap from the canvas.
                            //Send the hashmap from the selection over to the canvas.
                            s.convert_direction();
                            focused.undo_bit = focused.bit;
                            s.has_data = true;

                            if(sel.check_size()){
                                focused.set_bitmap(can.select_data(s.select_values()));
                                //Now we want the middle canvas to redraw with the new image.
                                can.invalidate();
                                edit.hint_text.setText("Does this look ok?");
                                edit.toggle_crop_btn_display(View.VISIBLE);
                            }
                        }else if(s.cropping){
                            //Now we want the middle canvas to redraw with the new image.
                            pre.set_img(pre.select_data(sel.select_values()));
                            sel.zero_values();
                            sel.invalidate();
                            pre.invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!s.has_data || s.cropping){
                            s.set_end_values(motionEvent.getX(),motionEvent.getY());
                        }
                        break;
                }

                s.invalidate();
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
                            focused.x = motionEvent.getX() - (focused.width / 2);
                            focused.y = motionEvent.getY() - (focused.height / 2);
                        break;
                    case MotionEvent.ACTION_MOVE:
                            focused.x = motionEvent.getX() - (focused.width / 2);
                            focused.y = motionEvent.getY() - (focused.height / 2);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }

                mov.invalidate();
                can.invalidate();
                return true;
            }
        });
    }

    //Takes an  intent to choose from the gallery and starts the intent.
    public void start_gal(Intent in){
        startActivityForResult(in,1);
    }
}
