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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapMain extends AppCompatActivity {

    private Button img_choose_one;
    private Intent intent;
    private int FIRST_FILE;
    private InputStream input;
    private int SQUID_SWAP_PERMISIONS;
    private RelativeLayout select_one,select_two;
    private Intent to_edit;
    private ImageButton open_settings;
    private View mContentView;

    //Grabs the chosen file and sends to the next activity.
    protected void onActivityResult(int request_code, int result_code, Intent data){
        if(result_code == Activity.RESULT_OK && data != null){
            Uri u = data.getData();

            try{
                input = getApplicationContext().getContentResolver().openInputStream(u);
                to_edit.putExtra("chosen_uri",data.getData().toString());
                startActivity(to_edit);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        check_permissions();

        FIRST_FILE = 1;
        to_edit = new Intent(this,SquidSwapEditor.class);

        intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        setContentView(R.layout.activity_squid_swap_main);
        setTitle(R.string.app_title);

        img_choose_one = (Button) findViewById(R.id.image_btn_one);
        open_settings = (ImageButton) findViewById(R.id.settings_toggle);

        //Opens the settings activity.
        open_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set = new Intent(getApplicationContext(),SquidSwapSettings.class);
                startActivity(set);
            }
        });

        //Choose the image that we want to start editing.
        img_choose_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, FIRST_FILE);
            }
        });
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
