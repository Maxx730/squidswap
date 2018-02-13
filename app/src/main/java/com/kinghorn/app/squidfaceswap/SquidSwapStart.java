package com.kinghorn.app.squidfaceswap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class SquidSwapStart extends AppCompatActivity {
    private static int SQUID_SWAP_PERMISIONS;
    private static CardView open_file, open_camera;
    private static Uri focused_image;
    private static final int CAMERA_PHOTO = 1;
    private static final int IMAGE_FROM_GALLERY = 2;
    private static LinearLayout choice_view;
    private static RelativeLayout image_view;
    private static ImageView image_preview;
    private static SquidFileService squid_files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squid_swap_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        squid_files = new SquidFileService(getApplicationContext());

        //Initialize all of our clicked buttons
        this.check_permissions();
        this.init_click_events();
        this.init_layouts();
        this.set_for_choice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case CAMERA_PHOTO:
                    //Set the uri for the focused image to the image that was taken on the camera.
                    focused_image = data.getData();
                    //Change layout for editing the image.
                    this.set_for_image();

                    image_preview.setImageBitmap((Bitmap) data.getExtras().get("data"));
                    break;
                case IMAGE_FROM_GALLERY:
                    try {
                        focused_image = data.getData();
                        Bitmap b = squid_files.open_first(data.getData(),new BitmapFactory.Options());
                        this.set_for_image();
                        image_preview.setImageBitmap(b);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_squid_swap_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        SQUID_SWAP_PERMISIONS);
            }
        }
    }

    //Grabs all the clickable "buttons" in the layout and sets the events.
    private void init_click_events(){
        open_file = (CardView) findViewById(R.id.open_by_file);
        open_camera = (CardView) findViewById(R.id.open_by_camera);

        open_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent,CAMERA_PHOTO);
                }
            }
        });

        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_FROM_GALLERY);
            }
        });
    }

    private void init_layouts(){
        choice_view = (LinearLayout) findViewById(R.id.choice_view);
        image_view = (RelativeLayout) findViewById(R.id.image_view);

        //ImageViews
        image_preview = (ImageView) findViewById(R.id.preview_image);
    }

    //Swap the layout depending on the context of what is happening in the application,
    //either editing an image or one has not been chosen yet.
    private void set_for_choice(){
        choice_view.setVisibility(View.VISIBLE);
        image_view.setVisibility(View.GONE);
    }

    private void set_for_image(){
        choice_view.setVisibility(View.GONE);
        image_view.setVisibility(View.VISIBLE);
    }
}
