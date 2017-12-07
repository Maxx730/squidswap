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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//Class that handles button clicks etc.
public class SquidEditorUi {
    private static SquidSelector sel;
    private static SquidSwapEditor ed;
    private static SquidFileService fil;
    private static SquidCanvas can,pre,bas;
    private SquidMovementHandler mov;
    private static Context c;

    //UI Elements that we will be using below here.
    public ImageButton crop_veri,crop_canc,close_editor,zoom_in,zoom_out,placement_suc,placement_can,final_crop_suc,final_crop_can;
    public TextView hint_text,zoom_am;
    public LinearLayout crop_btns,plac_btns,fade_layout,final_crop,sav_img;
    public SeekBar fade_seek;
    public ToggleButton layer_toggle;

    //Constructor.
    public SquidEditorUi(Context con,View mainview,SquidSelector s,SquidSwapEditor e,SquidFileService f,SquidCanvas vas,SquidCanvas p,SquidMovementHandler m,SquidCanvas b){
        c = con;
        sel = s;
        ed = e;
        fil = f;
        can = vas;
        pre = p;
        mov = m;
        bas = b;

        //Inflate the layout we are referring to.
        crop_btns = (LinearLayout) mainview.findViewById(R.id.crop_btns);
        plac_btns = (LinearLayout) mainview.findViewById(R.id.placement_btns);
        fade_layout = (LinearLayout) mainview.findViewById(R.id.fading_slider);
        final_crop = (LinearLayout) mainview.findViewById(R.id.final_crop_btns);
        sav_img = (LinearLayout) mainview.findViewById(R.id.save_image_btn);

        //Grab the crop buttons and set the click events.
        crop_veri = (ImageButton) mainview.findViewById(R.id.acc_button);
        crop_canc = (ImageButton) mainview.findViewById(R.id.can_btn);
        close_editor = (ImageButton) mainview.findViewById(R.id.editor_cancel);
        placement_suc = (ImageButton) mainview.findViewById(R.id.placement_success);
        placement_can = (ImageButton) mainview.findViewById(R.id.placement_cancel);
        final_crop_suc = (ImageButton) mainview.findViewById(R.id.final_crop_suc);
        final_crop_can = (ImageButton) mainview.findViewById(R.id.final_crop_can);

        fade_seek = (SeekBar) mainview.findViewById(R.id.fading_seeker);

        hint_text = (TextView) mainview.findViewById(R.id.hintText);

        layer_toggle = (ToggleButton)mainview.findViewById(R.id.layer_toggle);

        //Set the listeners
        init_btn_listen();
        editor_listeners();
    }

    //Toggle the display of the crop buttons based on the value given.
    public void toggle_crop_btn_display(int val){
        crop_btns.setVisibility(val);
    }
    public void toggle_plac_btn_display(int val){
        plac_btns.setVisibility(val);
    }
    public void toggle_seek_display(int val){
        fade_layout.setVisibility(val);
    }

    //General button touch listeners.
    public void editor_listeners(){
        close_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel.has_data = false;
                Toast.makeText(c,"Closing Editor...",Toast.LENGTH_SHORT);
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
                hint_text.setText("Please select what to copy...");
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

        layer_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layer_toggle.isChecked()){
                    //Toggle the focus onto the foreground image.
                    mov.set_foc(can.get_foc());
                    fade_layout.setVisibility(View.VISIBLE);
                    mov.invalidate();
                }else{
                    //Toggle the focus onto the background image.
                    mov.set_foc(bas.get_foc());
                    fade_layout.setVisibility(View.INVISIBLE);
                    mov.invalidate();
                }
            }
        });

        //Once00457210 the user has verified where they want the placement to be, we need to combine
        //the bitmaps for the back and from layers and then give them a preview.
        placement_suc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Now we want to send the values of both the canvases onto one
                //and give the option to crop.
                pre.set_img(fil.generate_preview());
                pre.invalidate();
                pre.setVisibility(View.VISIBLE);
                sel.setVisibility(View.VISIBLE);
                final_crop.setVisibility(View.VISIBLE);


                hint_text.setText("Crop Final Image");

                sel.cropping = true;
                sel.zero_values();
                sel.invalidate();

                plac_btns.setVisibility(View.GONE);
                mov.setVisibility(View.GONE);
                can.setVisibility(View.GONE);
                bas.setVisibility(View.GONE);
                fade_layout.setVisibility(View.GONE);

            }
        });



        placement_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c,"Stopping Squidswap Editing...",Toast.LENGTH_SHORT).show();
                main_menu();
            }
        });

        final_crop_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(c,"Stopping Squidswap Editing...",Toast.LENGTH_SHORT).show();
                main_menu();
            }
        });

        final_crop_suc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final_crop.setVisibility(View.GONE);
                sav_img.setVisibility(View.VISIBLE);
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

}
