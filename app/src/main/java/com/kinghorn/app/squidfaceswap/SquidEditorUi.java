package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private SquidImageScaler scaler;
    private SquidPainter pain;
    private static Context c;
    private boolean painter = false;
    private boolean eraser = false;

    //UI Elements that we will be using below here.
    public ImageButton revert_btn,crop_veri,crop_canc,close_editor,zoom_in,zoom_out,placement_suc,placement_can,final_crop_suc,final_crop_can,toggle_fade,resize_btn,toggle_painter,eraser_toggle,draw_back;
    public TextView hint_text,zoom_am;
    public LinearLayout crop_btns,plac_btns,fade_layout,final_crop,sav_img,layer_tools,scale_slider,brush_size,color_choices,buttons_below;
    public SeekBar fade_seek,zoom_seek,paint_size_seek;
    public ToggleButton layer_toggle;
    public Button clear_btn,apply_paint;

    //Constructor.
    public SquidEditorUi(Context con,View mainview,SquidSelector s,SquidSwapEditor e,SquidFileService f,SquidCanvas vas,SquidCanvas p,SquidMovementHandler m,SquidCanvas b,SquidImageScaler scal,SquidPainter pan){
        c = con;
        sel = s;
        ed = e;
        fil = f;
        can = vas;
        pre = p;
        mov = m;
        bas = b;
        scaler = scal;
        pain = pan;

        //Inflate the layout we are referring to.
        crop_btns = (LinearLayout) mainview.findViewById(R.id.crop_btns);
        plac_btns = (LinearLayout) mainview.findViewById(R.id.placement_btns);
        fade_layout = (LinearLayout) mainview.findViewById(R.id.fading_slider);
        final_crop = (LinearLayout) mainview.findViewById(R.id.final_crop_btns);
        sav_img = (LinearLayout) mainview.findViewById(R.id.save_image_btn);
        layer_tools = (LinearLayout) mainview.findViewById(R.id.layer_tools);
        scale_slider = (LinearLayout) mainview.findViewById(R.id.sizing_slider);
        brush_size = (LinearLayout) mainview.findViewById(R.id.brush_size);
        color_choices = (LinearLayout) mainview.findViewById(R.id.color_choices);
        buttons_below = (LinearLayout) mainview.findViewById(R.id.buttons_below);

        //Grab the crop buttons and set the click events.
        crop_veri = (ImageButton) mainview.findViewById(R.id.acc_button);
        crop_canc = (ImageButton) mainview.findViewById(R.id.can_btn);
        close_editor = (ImageButton) mainview.findViewById(R.id.editor_cancel);
        placement_suc = (ImageButton) mainview.findViewById(R.id.placement_success);
        placement_can = (ImageButton) mainview.findViewById(R.id.placement_cancel);
        final_crop_suc = (ImageButton) mainview.findViewById(R.id.final_crop_suc);
        final_crop_can = (ImageButton) mainview.findViewById(R.id.final_crop_can);
        toggle_fade = (ImageButton) mainview.findViewById(R.id.layer_edge_fade);
        resize_btn = (ImageButton) mainview.findViewById(R.id.layer_resize);
        toggle_painter = (ImageButton) mainview.findViewById(R.id.squid_painter);
        eraser_toggle = (ImageButton) mainview.findViewById(R.id.eraser_toggle);
        draw_back = (ImageButton) mainview.findViewById(R.id.revert_back);
        revert_btn = (ImageButton) mainview.findViewById(R.id.revert_back);

        clear_btn = (Button) mainview.findViewById(R.id.clear_paint);
        apply_paint = (Button) mainview.findViewById(R.id.apply_paint);

        fade_seek = (SeekBar) mainview.findViewById(R.id.fading_seeker);
        zoom_seek = (SeekBar) mainview.findViewById(R.id.size_slider);
        paint_size_seek = (SeekBar) mainview.findViewById(R.id.brush_size_seeker);

        hint_text = (TextView) mainview.findViewById(R.id.hintText);

        layer_toggle = (ToggleButton)mainview.findViewById(R.id.layer_toggle);

        //Set the listeners
        init_btn_listen();
        editor_listeners();

        //Set our default values down here.
        zoom_seek.setProgress(0);
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

        for(int i = 0;i < color_choices.getChildCount();i++){
            final RelativeLayout lay = (RelativeLayout) color_choices.getChildAt(i);

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drawable bak = (Drawable) lay.getBackground();

                    if(bak instanceof ColorDrawable){
                        int col = ((ColorDrawable) bak).getColor();
                        pain.set_brush_color(col);
                    }
                }
            });
        }

        crop_canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel.has_data = false;
                hint_text.setText("Please select what to copy...");
                can.set_img(can.foc.get_undo());
                can.draw_paint = true;
                can.can_select = true;
                toggle_crop_btn_display(View.GONE);
            }
        });

        crop_veri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");


            }
        });

        layer_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!layer_toggle.isChecked()){
                    //Toggle the focus onto the foreground image.
                    mov.set_foc(can.get_foc());
                    fade_layout.setVisibility(View.VISIBLE);
                    mov.invalidate();
                    scaler.set_focused(can);
                    zoom_seek.setProgress(Math.round(scaler.get_focused().getScaleX() - 1));

                }else{
                    //Toggle the focus onto the background image.
                    mov.set_foc(bas.get_foc());
                    fade_layout.setVisibility(View.GONE);
                    mov.invalidate();

                    //Reset the value of the scaling ui to the value of the new
                    //canvas scaling size and then set the focused canvas to the
                    //given canvas.
                    scaler.set_focused(bas);
                    zoom_seek.setProgress(Math.round(scaler.get_focused().getScaleX() - 1));
                }
            }
        });

        //Turns on the resizing tool which will disable moving the image for the focused layer.
        resize_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Once00457210 the user has verified where they want the placement to be, we need to combine
        //the bitmaps for the back and from layers and then give them a preview.
        placement_suc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Now we want to send the values of both the canvases onto one
                //and give the option to crop.
                can.set_img(fil.generate_preview());
                can.get_foc().is_fade = false;
                can.invalidate();
                can.setVisibility(View.VISIBLE);
                can.cropping = true;
                can.can_select = true;

                plac_btns.setVisibility(View.GONE);
                mov.setVisibility(View.GONE);
                bas.setVisibility(View.GONE);
                fade_layout.setVisibility(View.GONE);
                scale_slider.setVisibility(View.GONE);
            }
        });

        eraser_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eraser){
                    eraser_toggle.setBackgroundColor(ContextCompat.getColor(c,R.color.black_back));

                }else{
                    eraser_toggle.setBackgroundColor(ContextCompat.getColor(c,R.color.colorPrimary));

                }
            }
        });

        toggle_fade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(can.get_foc().is_fade){
                    can.get_foc().is_fade = false;
                    fade_layout.setVisibility(View.GONE);
                }else{
                    can.get_foc().is_fade = true;
                    fade_layout.setVisibility(View.VISIBLE);
                }

                can.invalidate();
            }
        });

        revert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pain.undo_last_paint()){
                    revert_btn.getBackground().setAlpha(50);
                }else{
                    revert_btn.getBackground().setAlpha(100);
                }
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

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pain.clear_paint();
            }
        });

        toggle_painter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(painter){
                    pain.setVisibility(View.GONE);
                    toggle_painter.setBackgroundColor(ContextCompat.getColor(c,R.color.black_back));
                    eraser_toggle.setVisibility(View.GONE);
                    brush_size.setVisibility(View.GONE);
                    color_choices.setVisibility(View.GONE);
                    buttons_below.setVisibility(View.VISIBLE);
                    painter = false;

                    Toast.makeText(c,"Painting Mode Inactive",Toast.LENGTH_SHORT).show();
                }else{
                    pain.setVisibility(View.VISIBLE);
                    toggle_painter.setBackgroundColor(ContextCompat.getColor(c,R.color.colorPrimary));
                    eraser_toggle.setVisibility(View.VISIBLE);
                    brush_size.setVisibility(View.VISIBLE);
                    color_choices.setVisibility(View.VISIBLE);
                    buttons_below.setVisibility(View.GONE);
                    painter = true;

                    Toast.makeText(c,"Painting Mode Active",Toast.LENGTH_SHORT).show();
                }
            }
        });

        paint_size_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pain.set_stroke_width((int) i);
                pain.width_change = true;
                pain.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pain.width_change = false;
                pain.invalidate();
            }
        });

        apply_paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pain.setVisibility(View.GONE);
                toggle_painter.setBackgroundColor(ContextCompat.getColor(c,R.color.black_back));
                eraser_toggle.setVisibility(View.GONE);
                brush_size.setVisibility(View.GONE);
                color_choices.setVisibility(View.GONE);
                buttons_below.setVisibility(View.VISIBLE);
                painter = false;

                can.set_paint_paths(pain.get_paths());
                can.invalidate();

                pain.setVisibility(View.GONE);
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

        zoom_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scaler.set_scale(i);
                scaler.focused_can.invalidate();
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
