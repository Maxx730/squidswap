package com.kinghorn.app.squidfaceswap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileNotFoundException;

//Generic activity class that will be loaded everytime a tool to
//manipulate the image is clicked, the images that will be shown
//are based on the tool they want to use.
public class GenericEditorActivity extends AppCompatActivity{

    private static int context;
    private Button suc_btn,can_btn;
    private Uri focusedUri;
    private Bitmap focusedBitmap;
    private SquidFileService fil;

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
            focusedBitmap = fil.open_first(focusedUri);

            //Initialize the rest of the editor if the file that was sent has been found.
            init_painter();

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
        suc_btn = (Button) findViewById(R.id.editor_apply);
        can_btn = (Button) findViewById(R.id.editor_cancel);

        suc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        SquidCanvas c = new SquidCanvas(getApplicationContext(),new SquidBitmapData(getApplicationContext()));
        final SquidPainter p = new SquidPainter(getApplicationContext());
        LayoutInflater l = getLayoutInflater();
        LinearLayout tools = (LinearLayout) l.inflate(R.layout.squid_paint_tools,null);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.canvas_layout);
        SeekBar bar = tools.findViewById(R.id.brush_size);

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
    }
}
