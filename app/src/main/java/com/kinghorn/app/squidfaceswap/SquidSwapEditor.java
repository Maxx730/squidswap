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
    public SquidBitmapData focused;
    public SquidCanvas can;
    public SquidSelector sel;
    public SquidBaseImage bas;
    public SquidMovementHandler mov;
    public RelativeLayout window;
    public SquidEditorUi edit;
    public SquidFileService fil;

    //Keeps track of which part of the app we are currently working with.
    public String editor_status;

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

                bas.set_image(b);
                mov.setVisibility(View.VISIBLE);
                edit.toggle_crop_btn_display(View.GONE);
                edit.toggle_plac_btn_display(View.VISIBLE);
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
        //Initialize our focused object with our canvas tools needed.
        focused = new SquidBitmapData(getApplicationContext());
        bas = new SquidBaseImage(getApplicationContext());
        //Now that it has been initialized we want to set the first image to focus.

        //Make sure we can get to the file with a try catch.
        try{
            focused.set_bitmap(open_first());

            //Once we have the file, then we want to send it into the first canvas.
            can = new SquidCanvas(getApplicationContext(),focused);
            sel = new SquidSelector(getApplicationContext());
            fil = new SquidFileService(can,bas,sel);
            mov = new SquidMovementHandler(getApplicationContext(),can,focused);
            edit = new SquidEditorUi(getApplicationContext(),getWindow().getDecorView(),focused,sel,this,fil);

            //Add the canvas view to the window.
            window.addView(bas);
            window.addView(can);
            window.addView(sel);
            window.addView(mov);

            //Hide the movement canvas that will be the top most, but we need to be able to select.
            mov.setVisibility(View.GONE);

            //Initialize selection touch events.
            init_selector(sel);
        }catch(FileNotFoundException e){
            System.out.println("ERROR: Chosen file does not seem to exist!");
        }
    }

    //Function will check and return a bitmap if the image was sent along with the intent.
    private Bitmap open_first() throws FileNotFoundException {
        Intent in = getIntent();
        String path = in.getStringExtra("chosen_uri");
        InputStream i = getContentResolver().openInputStream(Uri.parse(path));
        Bitmap b = BitmapFactory.decodeStream(i);

        return b;
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
                            focused.set_bitmap(can.select_data(s.select_values()));
                            s.has_data = true;
                            //Now we want the middle canvas to redraw with the new image.
                            can.invalidate();
                            edit.toggle_crop_btn_display(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!s.has_data){
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
