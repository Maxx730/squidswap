package com.kinghorn.app.squidfaceswap;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private ImageButton suc_btn,can_btn;
    private Uri focusedUri;
    private Bitmap focusedBitmap;
    private SquidFileService fil;
    private SquidCanvas c;
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
        focusedUri = Uri.parse(prev.getExtras().getString("FocusedBitmap"));

        try {
            if(prev.hasExtra("tmp")){
                focusedBitmap = fil.load_cached_file();
            }else{
                focusedBitmap = fil.open_first(focusedUri);
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
                    init_scaler();
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
        final SquidCanvas crop = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        LayoutInflater inflate = getLayoutInflater();
        final LinearLayout l = (LinearLayout) inflate.inflate(R.layout.cropping_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        l.setLayoutParams(par);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        ImageButton bac = l.findViewById(R.id.crop_back);


        crop.set_img(focusedBitmap);
        crop.invalidate();

        r.addView(crop);
        r.addView(l);

        crop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        crop.set_start((int) motionEvent.getX(),(int) motionEvent.getY());
                        crop.drawing = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        crop.set_end((int) motionEvent.getX(),(int) motionEvent.getY());
                        crop.drawing = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        crop.set_end((int) motionEvent.getX(),(int) motionEvent.getY());

                        Bitmap b = crop.select_data();

                        focusedBitmap = b;
                        crop.set_img(focusedBitmap);

                        crop.reset_vals();
                        crop.drawing = false;
                        break;
                }

                crop.invalidate();
                return true;
            }
        });

        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crop.set_img(crop.get_foc().get_undo());
                crop.invalidate();
            }
        });
    }

    //Initializes the scaling tool.
    private void init_scaler(){
        SquidCanvas scal = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);

        scal.set_img(focusedBitmap);
        scal.invalidate();

        r.addView(scal);
    }

    //Initializes the face swapping tool.
    private void init_swapper(){

    }
}
