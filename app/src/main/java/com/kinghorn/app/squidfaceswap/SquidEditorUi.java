package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

//Class that handles button clicks etc.
public class SquidEditorUi {

    private static SquidBitmapData dat;
    private static SquidSelector sel;
    private static SquidSwapEditor ed;
    private static SquidFileService fil;
    private static SquidCanvas can;
    private static Context c;

    //UI Elements that we will be using below here.
    public ImageButton crop_veri,crop_canc,close_editor,zoom_in,zoom_out,placement_suc,placement_can;
    public LinearLayout crop_btns,plac_btns;
    public SeekBar fade_seek;

    //Constructor.
    public SquidEditorUi(Context con,View mainview,SquidBitmapData d,SquidSelector s,SquidSwapEditor e,SquidFileService f,SquidCanvas vas){
        dat = d;
        c = con;
        sel = s;
        ed = e;
        fil = f;
        can = vas;

        //Inflate the layout we are referring to.
        crop_btns = (LinearLayout) mainview.findViewById(R.id.crop_btns);
        plac_btns = (LinearLayout) mainview.findViewById(R.id.placement_btns);

        //Grab the crop buttons and set the click events.
        crop_veri = (ImageButton) mainview.findViewById(R.id.acc_button);
        crop_canc = (ImageButton) mainview.findViewById(R.id.can_btn);
        close_editor = (ImageButton) mainview.findViewById(R.id.editor_cancel);
        zoom_in = (ImageButton) mainview.findViewById(R.id.img_scale_inc);
        zoom_out = (ImageButton) mainview.findViewById(R.id.img_scale_dec);
        placement_suc = (ImageButton) mainview.findViewById(R.id.placement_success);
        placement_can = (ImageButton) mainview.findViewById(R.id.placement_cancel);

        fade_seek = (SeekBar) mainview.findViewById(R.id.fading_seeker);

        //Set the listeners
        init_btn_listen();
        editor_listeners();
        set_scaling();
    }

    //Toggle the display of the crop buttons based on the value given.
    public void toggle_crop_btn_display(int val){
        crop_btns.setVisibility(val);
    }

    public void toggle_plac_btn_display(int val){
        plac_btns.setVisibility(val);
    }

    //General button touch listeners.
    public void editor_listeners(){
        close_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel.has_data = false;
                main_menu();
            }
        });
    }

    //Cropping phase button listeners.
    public void init_btn_listen(){
        crop_canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel.has_data = false;
                dat.set_bitmap(dat.undo_bit);
                dat.scale_x = 1;
                dat.scale_y = 1;
                toggle_crop_btn_display(View.GONE);
            }
        });

        crop_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                ed.start_gal(intent);
            }
        });

        //Once the user has verified where they want the placement to be, we need to combine
        //the bitmaps for the back and from layers and then give them a preview.
        placement_suc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fil.generate_preview()){
                    Toast.makeText(c,"Saved Image",Toast.LENGTH_SHORT);
                }

            }
        });

        placement_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Fading seekbar event listener.
        fade_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i > 0){
                    can.fade_val = i;
                    can.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Returns to the main menu of the application.
    public void main_menu(){
        Intent in = new Intent(c,SquidSwapMain.class);
        c.startActivity(in);
    }

    //Logic for the scaling buttons.
    public void set_scaling(){
        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dat.scale_x < 5 && dat.scale_y < 5){
                    dat.scale_x += .5;
                    dat.scale_y += .5;
                    can.invalidate();
                }
            }
        });

        zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dat.scale_x > 1 && dat.scale_y > 1){
                    dat.scale_x -= .5;
                    dat.scale_y -= .5;
                    can.invalidate();
                }
            }
        });
    }

}
