package com.kinghorn.app.squidfaceswap;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;

//Generic activity class that will be loaded everytime a tool to
//manipulate the image is clicked, the images that will be shown
//are based on the tool they want to use.
public class GenericEditorActivity extends AppCompatActivity {

    private static int context;
    private ImageButton tool_drawer,crop_back;
    private Button front_foc,back_foc,suc_btn,can_btn;
    private LinearLayout upper_layout,drawer_layout;
    private FrameLayout meme_layout;
    private SeekBar fade_seek,rotate_seek,crop_scale;
    private Uri focusedUri;
    private Bitmap focusedBitmap,frontImage,backImage;
    private SquidSettingsHandler settings;
    private SquidFileService fil;
    private SquidCanvas c,b;
    private SquidPainter p;
    private Intent i;
    private EditText meme_text;
    private boolean DRAWER_OPEN = false;
    private Bitmap meme_gen_tmp;
    //Hinting tool objects are intialized below.
    private Button got_it;
    private TextView scal_hint,paint_hint,crop_hint;
    private FrameLayout hints;
    //Integer that handles which layer we are focused on when using the swapping tool. 1 - front 2 - back
    private int focused_layer = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Switch different layouts based on the chosen activity.
        setContentView(R.layout.generic_editor_layout);

        //Init the objects we need.
        fil = new SquidFileService(getApplicationContext());
        settings = new SquidSettingsHandler(getApplicationContext());

        Intent prev = getIntent();

        crop_scale = (SeekBar) findViewById(R.id.general_scaling_bar);
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
                backImage = fil.open_first(Uri.parse(prev.getStringExtra("BackgroundImage")),new BitmapFactory.Options());
                frontImage = fil.load_cached_file();
                focusedUri = Uri.parse(prev.getExtras().getString("FrontImage"));
            }else{
                focusedBitmap = fil.open_first(focusedUri,new BitmapFactory.Options());
            }

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
                case 6:
                    init_meme_gen();
                    break;
            }

            //Initialize the elements we need.
            init_bottom_btns();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Error opening chosen file...",Toast.LENGTH_SHORT).show();
        }
    }

    //Initializes the bottom buttons based on the context of the editor.
    private void init_bottom_btns(){
        suc_btn = (Button) findViewById(R.id.editor_apply);
        can_btn = (Button) findViewById(R.id.editor_cancel);
        tool_drawer = (ImageButton) findViewById(R.id.swap_drawer_toggle_);
        drawer_layout = (LinearLayout) findViewById(R.id.side_tools_layout);

        tool_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup.LayoutParams par = drawer_layout.getLayoutParams();

                if(DRAWER_OPEN){
                    par.width = 700;
                    tool_drawer.setImageResource(R.drawable.ic_chevron_right_black_24dp);
                    DRAWER_OPEN = false;
                }else{
                    par.width = 0;
                    tool_drawer.setImageResource(R.drawable.ic_chevron_left_black_24dp);
                    DRAWER_OPEN = true;
                }

                drawer_layout.setLayoutParams(par);
            }
        });

        suc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(context){
                    case 1:
                        //Creates the swapped bitmap and tmp cached file and redirects back to
                        //the main menu.
                        Bitmap bp = Bitmap.createBitmap(b.getWidth(),b.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas en = new Canvas(bp);
                        Intent in = new Intent(getApplicationContext(),SquidSwapStart.class);
                        //If scaled up to be larger then we want to just grab the drawing cache normally.
                        en.drawBitmap(b.getDrawingCache(),0,0,null);
                        en.drawBitmap(c.getDrawingCache(),0,0,null);
                        //Here we want to check the size of the image, if the image width is larger than the screen
                        //then we want to just use the drawing cache, otherwise we want to crop the image to the size of the back
                        //ground image.

                        in.putExtra("tmp",true);

                        startActivity(in);
                        break;
                    case 2:
                        //Here we want to apply the paint paths from the squid painter and
                        //apply them over the bitmap, then we want to convert the bitmap
                        //to a byte array and send it back to the main activity.
                        i = new Intent(getApplicationContext(),SquidSwapStart.class);
                        i.putExtra("FocusedFileName",fil.save_tmp(p.apply_paint(c)));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);
                        startActivity(i);
                        break;
                    case 3:
                        i = new Intent(getApplicationContext(),SquidSwapStart.class);

                        i.putExtra("FocusedFileName",fil.save_tmp(focusedBitmap));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);

                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(getApplicationContext(),SquidSwapStart.class);

                        focusedBitmap = c.getDrawingCache();

                        i.putExtra("FocusedFileName",fil.save_tmp(focusedBitmap));
                        //Set tmp to true if the image has been changed etc.
                        i.putExtra("tmp",true);

                        startActivity(i);
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(),"made it to the swapper",Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        if(meme_gen_tmp != null){
                            i = new Intent(getApplicationContext(),SquidSwapStart.class);

                            i.putExtra("FocusedFileName",fil.save_tmp(meme_gen_tmp));
                            //Set tmp to true if the image has been changed etc.
                            i.putExtra("tmp",true);

                            startActivity(i);
                        }
                        break;
                }
            }
        });


        can_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bac = new Intent(getApplicationContext(),SquidSwapStart.class);

                //Change the name of the extra based on the context of where we are going back
                //from.
                    bac.putExtra("tmp",true);

                startActivity(bac);
            }
        });
    }

    //Initializes the painting tool within this layout.
    private void init_painter(){
        c = new SquidCanvas(getApplicationContext());
        p = new SquidPainter(getApplicationContext());
        LayoutInflater l = getLayoutInflater();
        FrameLayout tools = (FrameLayout) l.inflate(R.layout.squid_paint_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        FloatingActionButton brush_up = (FloatingActionButton) tools.findViewById(R.id.brush_size_up);
        FloatingActionButton brush_down = (FloatingActionButton) tools.findViewById(R.id.brush_size_down);
        FloatingActionButton paint_back = (FloatingActionButton) tools.findViewById(R.id.paint_revert);

        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LinearLayout choice = tools.findViewById(R.id.color_choices);

        tools.setLayoutParams(par);
        //Determine if we need to scale up the image.
        if(settings.load_pref("autoscale_back") == 1){
            c.AUTOSCALE = true;
        }
        c.set_img(focusedBitmap);
        c.invalidate();

        p.set_stroke_width(20);

        //Add our canvases to the layout.
        r.addView(c);
        r.addView(p);
        this.upper_layout.addView(tools);

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

        //Select white to begin with.
        choice.getChildAt(0).setAlpha(1);

        //Loop through the color choices and set onclick listeners to change the color of
        //the painting canvas.
        for(int i = 0;i < choice.getChildCount();i++){
            RelativeLayout rel = (RelativeLayout) choice.getChildAt(i);
            p.add_to_colors(rel);

            rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p.reset_color_alpha();
                    view.setAlpha(1);
                    p.set_brush_color(Color.parseColor((String) view.getTag()));
                }
            });
        }

        //Increase and decrease the size of the paintbrush.
        brush_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p.set_stroke_width(Math.round(p.get_stroke_width() + 5));
                p.width_change = true;
            }
        });

        brush_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p.get_stroke_width() > 5){
                    p.set_stroke_width(Math.round(p.get_stroke_width() - 5));
                    p.width_change = true;
                }
            }
        });

        //Removes last painted object.
        paint_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p.undo_last_paint();
            }
        });

        crop_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int scal_max = 5;
                float scal = (float)(1 + (float)(i/100f));

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

    //Initializes the cropping tool
    private void init_cropper(){
        //Create a layout inflator to bring back the undo button if there is a miscrop.
        LayoutInflater inflate = getLayoutInflater();
        final LinearLayout crop_tools = (LinearLayout) inflate.inflate(R.layout.cropping_tools,null);
        LinearLayout.LayoutParams par =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        crop_tools.setLayoutParams(par);
        crop_back = (ImageButton) crop_tools.findViewById(R.id.crop_back);
        crop_back.setVisibility(View.GONE);
        c = new SquidCropper(getApplicationContext(),crop_back);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        RelativeLayout suc = (RelativeLayout) findViewById(R.id.scaling_confirm_overlay);

        //Determine if we need to scale up the image.
        if(settings.load_pref("autoscale_back") == 1){
            c.AUTOSCALE = true;
        }

        c.set_img(focusedBitmap);
        c.set_scaling_bar(crop_scale);
        c.set_success_layout(suc);

        c.invalidate();
        r.addView(c);
        r.addView(crop_tools);
    }

    //Initializes the face swapping tool.
    private void init_swapper(){
        c = new SquidCanvas(getApplicationContext());
        b = new SquidCanvas(getApplicationContext());

        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LayoutInflater inflate = getLayoutInflater();
        LinearLayout l = (LinearLayout) inflate.inflate(R.layout.swap_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);

        front_foc = (Button) findViewById(R.id.foreground_toggle);
        back_foc = (Button) findViewById(R.id.background_toggle);
        rotate_seek = (SeekBar) l.findViewById(R.id.rotation_seek);
        if(settings.load_pref("autoscale_back") == 1){
            b.AUTOSCALE = true;
        }
        b.set_img(backImage);
        c.set_img(frontImage);

        b.CENTER_IMAGE = true;

        c.CENTER_IMAGE = false;


        b.invalidate();
        c.invalidate();

        r.addView(b);
        r.addView(c);
        r.addView(l);

        fade_seek = (SeekBar) l.findViewById(R.id.fade_seeker);

        front_foc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focused_layer = 1;
                c.setAlpha(1f);
                back_foc.setTextColor(Color.WHITE);
                front_foc.setTextColor(getResources().getColor(R.color.green_alt));
                ViewGroup.LayoutParams par = drawer_layout.getLayoutParams();
                par.width = 0;
                tool_drawer.setImageResource(R.drawable.ic_chevron_left_black_24dp);
                DRAWER_OPEN = false;
                drawer_layout.setLayoutParams(par);
            }
        });

        back_foc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focused_layer = 2;
                c.setAlpha(.3f);
                front_foc.setTextColor(Color.WHITE);
                back_foc.setTextColor(getResources().getColor(R.color.green_alt));
                ViewGroup.LayoutParams par = drawer_layout.getLayoutParams();
                par.width = 0;
                tool_drawer.setImageResource(R.drawable.ic_chevron_left_black_24dp);
                DRAWER_OPEN = false;
                drawer_layout.setLayoutParams(par);
            }
        });

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

        c.fade_val = fade_seek.getProgress();

        /*
        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(focused_layer == 2) {
                            b.get_foc().x = motionEvent.getX();
                            b.get_foc().y = motionEvent.getY();
                        }else{
                            c.get_foc().x = motionEvent.getX();
                            c.get_foc().y = motionEvent.getY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(focused_layer == 2) {
                            b.get_foc().x = motionEvent.getX();
                            b.get_foc().y = motionEvent.getY();
                        }else{
                            c.get_foc().x = motionEvent.getX();
                            c.get_foc().y = motionEvent.getY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }

                b.invalidate();
                c.invalidate();
                return true;
            }
        });
        */

        crop_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bol) {
                float scal = (float)(1 + (float)(i/100f));

                b.invalidate();
                c.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Seekbar that will rotate the front image of the swapper when changed, we will need to
        //set the default to 90 or whatever the start position should be.
        rotate_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Creates the meme generator superclass of
    private void init_meme_gen(){
        final SquidMemeGenerator c = new SquidMemeGenerator(getApplicationContext());
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LayoutInflater inflate = getLayoutInflater();
        meme_layout = (FrameLayout) inflate.inflate(R.layout.meme_gen_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        meme_layout.setLayoutParams(par);
        meme_text = (EditText) meme_layout.findViewById(R.id.meme_gen_text);

        r.addView(c);
        r.addView(meme_layout);

        c.set_meme_img(focusedBitmap);
        c.invalidate();

        //Change listener for the textfield that will change the text outputted onto the canvas based on the users input.
        meme_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Change the value of what will be printed on the canvas.
                c.set_meme_text(charSequence.toString());
                c.invalidate();
                meme_gen_tmp = c.build_meme();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
