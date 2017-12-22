package com.kinghorn.app.squidfaceswap;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

//Generic activity class that will be loaded everytime a tool to
//manipulate the image is clicked, the images that will be shown
//are based on the tool they want to use.
public class GenericEditorActivity extends AppCompatActivity{

    private static int context;
    private ImageButton suc_btn,can_btn,scale_btn;
    private LinearLayout scal_layout;
    private SeekBar scal_seek,fade_seek;
    private Uri focusedUri;
    private Bitmap focusedBitmap,frontImage,backImage;
    private SquidFileService fil;
    private SquidCanvas c,b;
    private SquidPainter p;
    private Intent i;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Switch different layouts based on the chosen activity.
        setContentView(R.layout.generic_editor_layout);

        //Init the objects we need.
        fil = new SquidFileService(getApplicationContext());

        Intent prev = getIntent();
        //Get the context of why this activity was opened and react accordingly to the
        //context integer.
        context = prev.getExtras().getInt("SquidContext");

        if(prev.hasExtra("FocusedBitmap")){
            focusedUri = Uri.parse(prev.getExtras().getString("FocusedBitmap"));
        }

        try {
            if(prev.hasExtra("tmp")){
                focusedBitmap = fil.load_cached_file();
            }else if(prev.hasExtra("BackgroundImage") && prev.hasExtra("FrontImage")){
                backImage = fil.open_first(Uri.parse(prev.getStringExtra("BackgroundImage")));
                frontImage = fil.load_cached_file();
            }else{
                focusedBitmap = fil.open_first(focusedUri);
            }

            //Initialize the elements we need.
            init_bottom_btns();

            //Initialize the rest of the editor if the file that was sent has been found.
            switch(context){
                case 1:
                    init_swapper();
                    break;
                case 2:
                    init_painter();
                    break;
                case 3:
                    init_cropper();
                    break;
                case 4:
                    init_scaler();
                    break;
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Error opening chosen file...",Toast.LENGTH_SHORT).show();

            Intent bac = new Intent(getApplicationContext(),SquidSwapMain.class);
            startActivity(bac);
        }
    }

    //Initializes the bottom buttons based on the context of the editor.
    private void init_bottom_btns(){
        suc_btn = (ImageButton) findViewById(R.id.editor_apply);
        can_btn = (ImageButton) findViewById(R.id.editor_cancel);
        scale_btn = (ImageButton) findViewById(R.id.toggle_scaling);

        scal_layout = (LinearLayout) findViewById(R.id.scale_layout);
        scal_seek = (SeekBar) findViewById(R.id.scale_bar);

        suc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(context){
                    case 1:
                        //Creates the swapped bitmap and tmp cached file and redirects back to
                        //the main menu.
                        Bitmap bp = Bitmap.createBitmap(backImage.getWidth(),backImage.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas en = new Canvas(bp);

                        en.drawBitmap(b.getDrawingCache(),0,0,null);
                        en.drawBitmap(c.getDrawingCache(),0,0,null);

                        Intent in = new Intent(getApplicationContext(),SquidSwapMain.class);

                        in.putExtra("FocusedFileName",fil.save_tmp(bp));
                        in.putExtra("tmp",true);

                        startActivity(in);
                        break;
                    case 2:
                        //Here we want to apply the paint paths from the squid painter and
                        //apply them over the bitmap, then we want to convert the bitmap
                        //to a byte array and send it back to the main activity.
                        i = new Intent(getApplicationContext(),SquidSwapMain.class);

                        i.putExtra("FocusedFileName",fil.save_tmp(p.apply_paint(c)));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);

                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(getApplicationContext(),SquidSwapMain.class);

                        i.putExtra("FocusedFileName",fil.save_tmp(focusedBitmap));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);

                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(getApplicationContext(),SquidSwapMain.class);

                        focusedBitmap = c.getDrawingCache();

                        i.putExtra("FocusedFileName",fil.save_tmp(focusedBitmap));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);

                        startActivity(i);
                        break;
                }
            }
        });

        can_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bac = new Intent(getApplicationContext(),SquidSwapMain.class);
                bac.putExtra("FocusedUri",focusedUri.toString());
                startActivity(bac);
            }
        });

        scale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scal_layout.getVisibility() == View.GONE){
                    scal_layout.setVisibility(View.VISIBLE);
                }else{
                    scal_layout.setVisibility(View.GONE);
                }
            }
        });

        scal_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                c.get_foc().scale_x = i + 1;
                c.get_foc().scale_y = i + 1;

                c.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Initializes the painting tool within this layout.
    private void init_painter(){
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        p = new SquidPainter(getApplicationContext());
        LayoutInflater l = getLayoutInflater();
        LinearLayout tools = (LinearLayout) l.inflate(R.layout.squid_paint_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        SeekBar bar = tools.findViewById(R.id.brush_size);
        ImageButton revert = (ImageButton) tools.findViewById(R.id.paint_back);
        LinearLayout choice = tools.findViewById(R.id.color_choices);

        tools.setLayoutParams(par);

        c.set_img(focusedBitmap);
        c.invalidate();

        //Add our canvases to the layout.
        r.addView(c);
        r.addView(p);
        r.addView(tools);

        //Set the touch events for the paint canvas.
        p.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        p.set_path_start(motionEvent.getX(),motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        p.move_path_to(motionEvent.getX(),motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        p.move_path_to(motionEvent.getX(),motionEvent.getY());
                        p.end_path();
                        break;
                }

                p.invalidate();
                return true;
            }
        });

        //Brush size events are listened to here.
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                p.set_stroke_width(i);
                p.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                p.width_change = true;
                p.invalidate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                p.width_change = false;
                p.invalidate();
            }
        });

        //Loop through the color choices and set onclick listeners to change the color of
        //the painting canvas.
        for(int i = 0;i < choice.getChildCount();i++){
            RelativeLayout rel = (RelativeLayout) choice.getChildAt(i);

            rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                p.set_brush_color(Color.parseColor((String) view.getTag()));
                }
            });
        }

        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p.undo_last_paint();
            }
        });
    }

    //Initializes the cropping tool
    private void init_cropper(){
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        LayoutInflater inflate = getLayoutInflater();
        final LinearLayout l = (LinearLayout) inflate.inflate(R.layout.cropping_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        ImageButton bac = l.findViewById(R.id.crop_back);


        c.set_img(focusedBitmap);
        c.invalidate();

        r.addView(c);
        r.addView(l);

        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        c.set_start((int) motionEvent.getX(),(int) motionEvent.getY());
                        c.drawing = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        c.set_end((int) motionEvent.getX(),(int) motionEvent.getY());
                        c.drawing = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        c.set_end((int) motionEvent.getX(),(int) motionEvent.getY());

                        Bitmap b = c.select_data();

                        focusedBitmap = b;
                        c.set_img(focusedBitmap);

                        c.reset_vals();
                        c.drawing = false;
                        break;
                }

                c.invalidate();
                return true;
            }
        });

        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.set_img(c.get_foc().get_undo());
                c.invalidate();
            }
        });
    }

    //Initializes the scaling tool.
    private void init_scaler(){
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);

        scal_layout.setVisibility(View.VISIBLE);

        c.set_img(focusedBitmap);
        c.CENTER_IMAGE = false;
        c.get_foc().is_fade = false;
        c.invalidate();

        r.addView(c);
    }

    //Initializes the face swapping tool.
    private void init_swapper(){
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        b = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));

        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LayoutInflater inflate = getLayoutInflater();
        LinearLayout l = (LinearLayout) inflate.inflate(R.layout.swap_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);

        b.set_img(backImage);
        c.set_img(frontImage);

        c.CENTER_IMAGE = false;
        c.get_foc().is_fade = true;

        b.invalidate();
        c.invalidate();

        r.addView(b);
        r.addView(c);
        r.addView(l);

        fade_seek = (SeekBar) l.findViewById(R.id.fade_seeker);

        fade_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                c.fade_val = i;
                c.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        c.get_foc().x = motionEvent.getX() - (c.get_foc().bit.getWidth() / 2);
                        c.get_foc().y = motionEvent.getY() - (c.get_foc().bit.getHeight() / 2);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        c.get_foc().x = motionEvent.getX() - (c.get_foc().bit.getWidth() / 2);
                        c.get_foc().y = motionEvent.getY() - (c.get_foc().bit.getHeight() / 2);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                c.invalidate();
                return true;
            }
        });
    }
}
