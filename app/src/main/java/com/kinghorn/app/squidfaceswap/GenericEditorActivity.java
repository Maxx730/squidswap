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
    private ImageButton suc_btn,can_btn;
    private Button front_foc,back_foc;
    private LinearLayout meme_layout,upper_layout;
    private SeekBar fade_seek,rotate_seek,crop_scale;
    private Uri focusedUri;
    private Bitmap focusedBitmap,frontImage,backImage;
    private SquidSettingsHandler settings;
    private SquidFileService fil;
    private SquidCanvas c,b;
    private SquidPainter p;
    private Intent i;
    private EditText meme_text;

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
        //Grab the layout of where different tools will be going.
        upper_layout = (LinearLayout)findViewById(R.id.upper_tool_layout);
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
                System.out.println(focusedUri.getPath());
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
                case 4:

                    break;
                case 5:
                    break;
                case 6:
                    init_meme_gen();
                    break;
            }

            //Initialize the elements we need.
            init_bottom_btns();
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

        suc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(context){
                    case 1:
                        //Creates the swapped bitmap and tmp cached file and redirects back to
                        //the main menu.
                        Bitmap bp = Bitmap.createBitmap(b.getWidth(),b.getHeight(), Bitmap.Config.ARGB_8888);
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
                    case 5:
                        Toast.makeText(getApplicationContext(),"made it to the swapper",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        can_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bac = new Intent(getApplicationContext(),SquidSwapMain.class);

                //Change the name of the extra based on the context of where we are going back
                //from.
                if(context == 1 || context == 3){
                    bac.putExtra("tmp",true);
                    bac.putExtra("FocusedFileName",focusedUri.toString());
                }else{
                    bac.putExtra("FocusedUri",focusedUri.toString());
                }

                startActivity(bac);
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
        this.upper_layout.addView(tools);

        handle_hints("paint");

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

        crop_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int scal_max = 5;
                float scal = (float)(1 + (float)(i/100f));
                c.foc.set_scale_x(scal);
                c.foc.set_scale_y(scal);
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
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);

        c.set_img(focusedBitmap);
        c.invalidate();

        r.addView(c);

        handle_hints("crop");

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
                        //We need to write logic to check if there even is a value for when
                        //just in case they dragged to off of the screen.
                        if(c.can_select){
                            int xUpCheck,yUpCheck;

                            System.out.println("UPX: "+motionEvent.getX()+" UPY: "+motionEvent.getY());

                            if((int) motionEvent.getY() > 0){
                                yUpCheck = (int) motionEvent.getY();
                            }else{
                                yUpCheck = 0;
                            }

                            if((int) motionEvent.getX() > 0){
                                xUpCheck = (int) motionEvent.getX();
                            }else{
                                xUpCheck = 0;
                            }

                            c.set_end(xUpCheck,yUpCheck);

                            Bitmap b = c.select_data();

                            focusedBitmap = b;
                            c.set_img(focusedBitmap);
                            c.reset_vals();
                            c.get_foc().set_scale_x(1);
                            c.get_foc().set_scale_y(1);
                            c.drawing = false;
                        }
                        break;
                }

                c.invalidate();
                return true;
            }
        });

        crop_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int scal_max = 5;
                float scal = (float)(1 + (float)(i/100f));
                c.foc.set_scale_x(scal);
                c.foc.set_scale_y(scal);
                System.out.println(i);
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

    //Initializes the face swapping tool.
    private void init_swapper(){
        c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        b = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));

        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LayoutInflater inflate = getLayoutInflater();
        LinearLayout l = (LinearLayout) inflate.inflate(R.layout.swap_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);

        front_foc = (Button) l.findViewById(R.id.foreground_toggle);
        back_foc = (Button) l.findViewById(R.id.background_toggle);
        rotate_seek = (SeekBar) l.findViewById(R.id.rotation_seek);

        b.set_img(backImage);
        c.set_img(frontImage);

        b.CENTER_IMAGE = false;
        c.CENTER_IMAGE = false;
        c.get_foc().is_fade = true;
        b.get_foc().is_fade = false;

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
                front_foc.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        back_foc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focused_layer = 2;
                c.setAlpha(.3f);
                front_foc.setTextColor(Color.WHITE);
                back_foc.setTextColor(getResources().getColor(R.color.colorAccent));
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

        c.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(focused_layer == 2) {
                            b.get_foc().x = motionEvent.getX() - (b.get_foc().bit.getWidth() / 2);
                            b.get_foc().y = motionEvent.getY() - (b.get_foc().bit.getHeight() / 2);
                        }else{
                            c.get_foc().x = motionEvent.getX() - (c.get_foc().bit.getWidth() / 2);
                            c.get_foc().y = motionEvent.getY() - (c.get_foc().bit.getHeight() / 2);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(focused_layer == 2) {
                            b.get_foc().x = motionEvent.getX() - (b.get_foc().bit.getWidth() / 2);
                            b.get_foc().y = motionEvent.getY() - (b.get_foc().bit.getHeight() / 2);
                        }else{
                            c.get_foc().x = motionEvent.getX() - (c.get_foc().bit.getWidth() / 2);
                            c.get_foc().y = motionEvent.getY() - (c.get_foc().bit.getHeight() / 2);
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

        crop_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bol) {
                float scal = (float)(1 + (float)(i/100f));

                if(focused_layer == 2){
                    b.foc.set_scale_x(scal);
                    b.foc.set_scale_y(scal);
                }else{
                    c.foc.set_scale_x(scal);
                    c.foc.set_scale_y(scal);
                }

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
                c.get_foc().rotation_angle = i;
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

    //Creates the meme generator superclass of
    private void init_meme_gen(){
        final SquidMemeGenerator c = new SquidMemeGenerator(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        LayoutInflater inflate = getLayoutInflater();
        meme_layout = (LinearLayout) inflate.inflate(R.layout.meme_gen_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        meme_layout.setLayoutParams(par);
        meme_text = (EditText) meme_layout.findViewById(R.id.meme_gen_txt);

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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Hides/shows hints based on the context and if the saved pref for if they have been seen
    //or not is set to true.
    private void handle_hints(String context){
        scal_hint = (TextView)findViewById(R.id.scaling_hint);
        crop_hint = (TextView) findViewById(R.id.cropping_hint);
        got_it = (Button) findViewById(R.id.got_it_btn);
        hints = (FrameLayout) findViewById(R.id.hint_layout);

        switch(context){
            case "crop":
                if(this.settings.load_pref("hint_crop") == 1){
                    got_it.setVisibility(View.VISIBLE);
                    hints.setVisibility(View.VISIBLE);
                    crop_hint.setVisibility(View.VISIBLE);
                    scal_hint.setVisibility(View.VISIBLE);

                    got_it.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hints.setVisibility(View.GONE);
                            crop_hint.setVisibility(View.GONE);
                            scal_hint.setVisibility(View.GONE);
                            got_it.setVisibility(View.GONE);
                            settings.save_pref("hint_crop",0);
                        }
                    });
                }
                break;
            case "paint":
                if(this.settings.load_pref("hint_paint") == 1){
                    got_it.setVisibility(View.VISIBLE);
                    hints.setVisibility(View.VISIBLE);
                    paint_hint = (TextView) findViewById(R.id.painting_hint);
                    paint_hint.setVisibility(View.VISIBLE);
                    scal_hint.setVisibility(View.VISIBLE);

                    got_it.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hints.setVisibility(View.GONE);
                            paint_hint.setVisibility(View.GONE);
                            scal_hint.setVisibility(View.GONE);
                            got_it.setVisibility(View.GONE);
                            settings.save_pref("hint_paint",0);
                        }
                    });
                }
                break;
            case "swap":
                if(this.settings.load_pref("hint_swap") == 1){

                }
                break;
        }
    }
}
