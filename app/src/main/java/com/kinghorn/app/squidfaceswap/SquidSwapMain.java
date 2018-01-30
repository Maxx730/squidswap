package com.kinghorn.app.squidfaceswap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapMain extends AppCompatActivity {

    private ImageButton  settings_close,paint,crop,swit,settings,save,open,restart,main_up,close_main,meme_men;
    private int SQUID_SWAP_PERMISIONS;
    private Intent sett_int,open_int,settings_int;
    private SquidFileService squidFiles;
    private SquidSettingsHandler squidsettings;
    private static final int PICK_IMAGE = 1;
    private static final int PAINT_INT = 2;
    private static final int CROP_INT = 3;
    private static final int PICK_SWAP_IMAGE = 5;
    private static final int MEME_GEN = 6;
    private static int HAS_IMAGE = 0;
    private ImageView focusedImage,tapImage;
    private TextView uri_path,tap_to_open;
    private Uri focusedUri;
    private Intent chec;
    private FrameLayout main_men,opening_layout,settings_lay;
    private Animation slide_up,slide_down;
    private ListView settings_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize our needed objects.
        squidFiles = new SquidFileService(getApplicationContext());
        squidsettings = new SquidSettingsHandler(getApplicationContext());

        //Check if there is already a focused button.
        chec = getIntent();

        setContentView(R.layout.activity_squid_swap_main);
        //Make sure the application has all the necessary
        //permisions to read and write to the phone.
        check_permissions();
        init_elements();
        init_buttons();

        //If we are coming back from canceling the image then we want to
        if(chec.getExtras() != null && chec.hasExtra("FocusedUri")){
            focusedUri = Uri.parse(chec.getExtras().getString("FocusedUri"));
            try {
                focusedImage.setImageBitmap(squidFiles.open_first(focusedUri));

                tapImage.setVisibility(View.GONE);
                tap_to_open.setVisibility(View.GONE);
            } catch (FileNotFoundException e) {

            }
        }else if(chec.getExtras() != null && chec.hasExtra("FocusedFileName")){
            Bitmap b = squidFiles.load_cached_file();

            focusedUri = Uri.parse(chec.getStringExtra("FocusedFileName"));
            focusedImage.setImageBitmap(b);

            //Hide the tap to open image.
            tapImage.setVisibility(View.GONE);
            tap_to_open.setVisibility(View.GONE);
        }

    }

    //We are going to do different things with this based on which tool the
    //user is coming from.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Make sure we grabbed a working result from the gallery.
        if(resultCode == Activity.RESULT_OK){
            //Figure out where this result is coming from, in most
            //cases the data coming back will be bitmat data.
            switch(requestCode){
                case PICK_IMAGE:
                    //We want to load the chosen image from the provided URI.
                    try {
                        //Here we are going to want to determine the size of the image and
                        //scaled it down / get a lower bitrate for lower end phones.


                        Bitmap b = squidFiles.open_first(data.getData());
                        focusedImage.setImageBitmap(b);
                        squidFiles.save_tmp(b);
                        focusedUri = data.getData();

                        //Hide the tap to open image.
                        tapImage.setVisibility(View.GONE);
                        tap_to_open.setVisibility(View.GONE);
                        //Set our has image variable to true so that the other buttons can be used.
                        HAS_IMAGE = 1;
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(),"There was an error opening the chosen file.",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PICK_SWAP_IMAGE:
                    //Once they have picked a swap image, we want to then send them to the generic activity
                    //to move around the swapping tool, we need to send the uri for the chosen image in the intent.
                    Uri u = data.getData();
                    Intent n = new Intent(getApplicationContext(),GenericEditorActivity.class);

                    n.putExtra("BackgroundImage",u.toString());
                    n.putExtra("FrontImage",focusedUri.toString());
                    n.putExtra("SquidContext",1);

                    startActivity(n);
                    break;
            }
        }
    }

    //Initializes all the buttons that will  be used in the main
    //menu to interact with the application.
    private void init_buttons(){
        open = (ImageButton) findViewById(R.id.image_btn_one);
        paint = (ImageButton) findViewById(R.id.main_paint);
        crop = (ImageButton) findViewById(R.id.main_crop);
        swit = (ImageButton) findViewById(R.id.main_swap);
        settings =  (ImageButton) findViewById(R.id.settings_open);
        save = (ImageButton) findViewById(R.id.save_changes_main);
        restart = (ImageButton) findViewById(R.id.restart_main);
        main_up = (ImageButton) findViewById(R.id.main_menu_up);
        close_main = (ImageButton) findViewById(R.id.close_main_menu);
        tapImage = (ImageView) findViewById(R.id.tap_image);
        settings_close = (ImageButton) findViewById(R.id.close_settings_slider);


        tapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Please choose a file to open...",Toast.LENGTH_SHORT).show();

                chec.removeExtra("tmp");

                open_int = new Intent();
                open_int.setType("image/*");
                open_int.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(open_int, "Select Picture"), PICK_IMAGE);
            }
        });

        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(focusedUri != null){
                    Intent edit = new Intent(getApplicationContext(),GenericEditorActivity.class);
                    edit.putExtra("SquidContext",PAINT_INT);
                    //Pass the focused image on to the next intent.
                    edit.putExtra("FocusedBitmap",focusedUri.toString());

                    //Pass on to the next activity that we are now dealing with a
                    //temporary cached file.
                    if(chec.hasExtra("tmp")){
                        edit.putExtra("tmp",true);
                    }

                    startActivity(edit);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        close_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_men.startAnimation(slide_down);
                main_men.setVisibility(View.GONE);
                tapImage.setClickable(true);
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(focusedUri != null){
                    Intent edit = new Intent(getApplicationContext(),GenericEditorActivity.class);
                    edit.putExtra("SquidContext",CROP_INT);
                    //Pass the focused image on to the next intent.
                    edit.putExtra("FocusedBitmap",focusedUri.toString());

                    //Pass on to the next activity that we are now dealing with a
                    //temporary cached file.
                    if(chec.hasExtra("tmp")){
                        edit.putExtra("tmp",true);
                    }

                    startActivity(edit);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        swit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(focusedUri != null){
                    Toast.makeText(getApplicationContext(),"Please choose a file to open...",Toast.LENGTH_SHORT).show();

                    open_int = new Intent();
                    open_int.setType("image/*");
                    open_int.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(open_int, "Select Picture"), PICK_SWAP_IMAGE);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            settings_lay.startAnimation(slide_up);
            settings_lay.setVisibility(View.VISIBLE);
            set_all_false();
            tapImage.setClickable(false);
            }
        });

        settings_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_lay.startAnimation(slide_down);
                settings_lay.setVisibility(View.GONE);
                set_all_true();
                tapImage.setClickable(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE == 1){
                    AlertDialog.Builder d = new AlertDialog.Builder(SquidSwapMain.this);

                    d.setTitle("Save edits to photo?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            squidFiles.save_image(squidFiles.load_cached_file());

                            Toast.makeText(getApplicationContext(),"Image Saved",Toast.LENGTH_SHORT).show();

                            squidFiles.delete_tmp();
                            focusedImage.setImageDrawable(null);
                            HAS_IMAGE = 0;

                            //Show the tap to open image.
                            tapImage.setVisibility(View.VISIBLE);
                            tap_to_open.setVisibility(View.VISIBLE);

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog a = d.create();
                    a.show();
                }else{
                    Toast.makeText(getApplicationContext(),"No Image has been Edited",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //When this button is clicked we first want to do a prompt asking if the user would like
        //to restart from the beginning erasing any current work from being saved.
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder d = new AlertDialog.Builder(SquidSwapMain.this);

                d.setTitle("Are you sure you would like to restart?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //If the user clicks yes we want to reset all of the different
                                //Values such as the images etc as well as the focused button values.
                                focusedUri = null;
                                squidFiles.delete_tmp();
                                focusedImage.setImageDrawable(null);
                                HAS_IMAGE = 0;

                                //Hide the tap to open image.
                                tapImage.setVisibility(View.VISIBLE);
                                tap_to_open.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //If not then do nothing.
                            }
                        });

                //Show the alert dialog.
                AlertDialog a = d.create();
                a.show();
            }
        });

        main_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_men.startAnimation(slide_up);
                main_men.setVisibility(View.VISIBLE);
                tapImage.setClickable(false);
            }
        });
    }

    //Check the size of the uri image before trying to load it into memory, if it is too large
    //we will want to load it with a lower bit rate.
    private Bitmap check_bitmap_rate(URI i){

        return null;
    }

    //Initializes the rest of the elements on the page that are not buttons
    //in particular.
    private void init_elements(){
        focusedImage = (ImageView) findViewById(R.id.focused_image);
        tap_to_open = (TextView) findViewById(R.id.tap_text);
        main_men = (FrameLayout) findViewById(R.id.main_menu);
        settings_lay = (FrameLayout) findViewById(R.id.settings_slide_up);
        opening_layout = (FrameLayout) findViewById(R.id.opening_layout);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);

        settings_list = (ListView) findViewById(R.id.settings_list);
        SquidFileService file = new SquidFileService(getApplicationContext(),null);
        ArrayList<SquidMenuItem> items = new ArrayList<SquidMenuItem>();
        items.add(0,new SquidMenuItem(this,"SquidSwap Ink. (Version 1.0)",file.load_drawable(this,R.drawable.ic_info_black_24dp),new Intent(this,SquidAboutPage.class),"Link",""));
        items.add(0,new SquidMenuItem(this,"High Image Quaility",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","save_high_res"));
        items.add(0,new SquidMenuItem(this,"Autocrop Paint/Swap",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","crop_to_original"));
        items.add(0,new SquidMenuItem(this,"Menu Drawer",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","main_drawer"));
        items.add(0,new SquidMenuItem(this,"Dark Theme",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","dark_theme"));
        items.add(0,new SquidMenuItem(this,"Watermark",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","watermark"));
        SquidListAdapter adapt = new SquidListAdapter(getApplicationContext(),R.layout.squidswap_menu_layout,items);
        settings_list.setAdapter(adapt);
    }

    //Make sure the application has the correct permissions.
    private void check_permissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SQUID_SWAP_PERMISIONS);
            }
        }
    }


    //Functions below set all the buttons on the main screen clickable
    //also not clickable.
    private void set_all_false(){
        paint.setClickable(false);
        crop.setClickable(false);
        save.setClickable(false);
        restart.setClickable(false);
        main_up.setClickable(false);
        swit.setClickable(false);
        settings.setClickable(false);
    }

    private void set_all_true(){
        paint.setClickable(true);
        crop.setClickable(true);
        save.setClickable(true);
        restart.setClickable(true);
        main_up.setClickable(true);
        swit.setClickable(true);
        settings.setClickable(true);
    }
}

