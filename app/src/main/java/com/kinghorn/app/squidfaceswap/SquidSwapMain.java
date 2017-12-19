package com.kinghorn.app.squidfaceswap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapMain extends AppCompatActivity {

    private Button  paint,crop,swit,open;
    private ImageButton settings;
    private int SQUID_SWAP_PERMISIONS;
    private Intent sett_int,open_int,settings_int;
    private SquidFileService squidFiles;
    private static final int PICK_IMAGE = 1;
    private static int HAS_IMAGE = 0;
    private ImageView focusedImage;
    private TextView uri_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize our needed objects.
        squidFiles = new SquidFileService(getApplicationContext());

        setContentView(R.layout.activity_squid_swap_main);
        //Make sure the application has all the necessary
        //permisions to read and write to the phone.
        check_permissions();
        init_elements();
        init_buttons();
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
                        focusedImage.setImageBitmap(squidFiles.open_first(data.getData()));

                        uri_path.setText(squidFiles.get_uri_path(data.getData()));
                        //Set our has image variable to true so that the other buttons can be used.
                        HAS_IMAGE = 1;
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(),"There was an error opening the chosen file.",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    //Initializes all the buttons that will  be used in the main
    //menu to interact with the application.
    private void init_buttons(){
        open = (Button) findViewById(R.id.image_btn_one);
        paint = (Button) findViewById(R.id.main_paint);
        crop = (Button) findViewById(R.id.main_crop);
        swit = (Button) findViewById(R.id.main_swap);
        settings =  (ImageButton) findViewById(R.id.settings_open);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Please choose a file to open...",Toast.LENGTH_SHORT).show();

                open_int = new Intent();
                open_int.setType("image/*");
                open_int.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(open_int, "Select Picture"), PICK_IMAGE);
            }
        });

        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE == 1){
                    Intent edit = new Intent(getApplicationContext(),GenericEditorActivity.class);
                    startActivity(edit);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE == 1){
                    Intent edit = new Intent(getApplicationContext(),GenericEditorActivity.class);
                    startActivity(edit);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        swit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE == 1){
                    Intent edit = new Intent(getApplicationContext(),GenericEditorActivity.class);
                    startActivity(edit);
                }else{
                    Toast.makeText(getApplicationContext(),"Image to edit has not been chosen...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_int = new Intent(getApplicationContext(),SquidSwapSettings.class);
                startActivity(settings_int);
            }
        });
    }

    //Initializes the rest of the elements on the page that are not buttons
    //in particular.
    private void init_elements(){
        focusedImage = (ImageView) findViewById(R.id.focused_image);
        uri_path = (TextView) findViewById(R.id.uri_path);
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
}

