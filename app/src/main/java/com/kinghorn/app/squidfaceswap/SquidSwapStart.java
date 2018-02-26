package com.kinghorn.app.squidfaceswap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class SquidSwapStart extends AppCompatActivity {
    private static int SQUID_SWAP_PERMISIONS;
    private static CardView open_file, open_camera,meme_card,paint_card,swap_card,crop_card;
    private static Uri focused_image;
    private static final int CAMERA_PHOTO = 1;
    private static final int IMAGE_FROM_GALLERY = 2;
    private static final int CROP_ID = 3;
    private static final int PAINT_ID = 2;
    private static final int SWAP_ID = 1;
    private static final int PICK_SWAP_IMAGE = 5;
    private static final int MEME_ID = 6;
    private static boolean HAS_IMAGE = false;
    private static LinearLayout choice_view,previous_swap;
    private static RelativeLayout image_view;
    private static ImageView image_preview;
    private static SquidFileService squid_files;
    private static HorizontalScrollView squid_tools;
    private static boolean EDITED = false;
    private static boolean FROM_CAM = false;
    private static Button save_btn,pre_btn;

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
        this.init_tool_cards();
        this.set_for_choice();

        Intent prev = getIntent();

        //Check here if we have been editing a file.
        if(prev.hasExtra("tmp") || prev.hasExtra("FocusedUri")){
            if(prev.hasExtra("tmp")){
                Bitmap b = squid_files.load_cached_file();
                image_preview.setImageBitmap(b);
                EDITED = true;
                set_for_image();
            }else if(prev.hasExtra("FocusedUri")){
                try {
                    image_preview.setImageBitmap(squid_files.open_first(Uri.parse(prev.getStringExtra("FocusedUri")),new BitmapFactory.Options()));
                    set_for_image();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
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
                        //Here we want to check the resolution of the image, if it is really large then we want to scale it down
                        //automatically to about half the size.
                        Bitmap fin = null;

                        if(b.getWidth() > 1500){

                            Toast.makeText(getApplicationContext(),"testing",Toast.LENGTH_SHORT).show();
                            Matrix m = new Matrix();
                            m.setScale(.5f,.5f);
                            fin = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);
                        }else{
                            fin = b;
                        }

                        this.set_for_image();
                        squid_files.save_tmp(fin);
                        HAS_IMAGE = true;
                        EDITED = true;
                        image_preview.setImageBitmap(fin);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case PICK_SWAP_IMAGE:
                    //Once they have picked a swap image, we want to then send them to the generic activity
                    //to move around the swapping tool, we need to send the uri for the chosen image in the intent.
                    Uri u = data.getData();
                    Intent n = new Intent(getApplicationContext(),GenericEditorActivity.class);

                    n.putExtra("BackgroundImage",u.toString());
                    n.putExtra("FrontImage",focused_image.toString());
                    n.putExtra("SquidContext",1);

                    startActivity(n);
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
        switch(id){
            case R.id.action_reset:
                AlertDialog.Builder al = new AlertDialog.Builder(this);
                focused_image = null;
                image_preview.setImageBitmap(null);
                EDITED = false;
                HAS_IMAGE = false;
                this.set_for_choice();
                break;
            case R.id.action_settings:
                //Start the settings activity.
                Intent in = new Intent(getApplicationContext(),SquidSwapSettings.class);
                startActivity(in);
                break;
            case R.id.action_about:
                //Start the about activity.
                AlertDialog alertDialog = new AlertDialog.Builder(SquidSwapStart.this).create();
                alertDialog.setTitle("Thank you for using Squidswap!");
                alertDialog.setMessage("Please visit www.squidswap.com for more information.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
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
        save_btn = (Button) findViewById(R.id.save_button);
        pre_btn = (Button) findViewById(R.id.preview_button);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                squid_files.save_image(squid_files.load_cached_file());
                Snackbar snac = Snackbar.make(findViewById(R.id.main_content),"Saving Image...", Snackbar.LENGTH_SHORT);
                snac.show();

                HAS_IMAGE = false;
                set_for_choice();
            }
        });

        pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),SquidSwapPreview.class);
                in.putExtra("tmp",true);
                startActivity(in);
            }
        });

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
        previous_swap = (LinearLayout) findViewById(R.id.previous_swaps);

        //ImageViews
        image_preview = (ImageView) findViewById(R.id.preview_image);

        //Scroll layouts
        squid_tools = (HorizontalScrollView) findViewById(R.id.squidswap_tools_scroller);
    }

    private void init_tool_cards(){
        meme_card = (CardView) findViewById(R.id.meme_card);
        paint_card = (CardView) findViewById(R.id.paint_card);
        swap_card = (CardView) findViewById(R.id.swap_card);
        crop_card = (CardView) findViewById(R.id.crop_card);

        crop_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE){
                    startActivity(build_editor_intent(CROP_ID));
                }else{
                    Snackbar snac = Snackbar.make(findViewById(R.id.main_content),"No image has been opened.", Snackbar.LENGTH_SHORT);
                    snac.show();
                }
            }
        });

        paint_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE){
                    startActivity(build_editor_intent(PAINT_ID));
                }else{
                    Snackbar snac = Snackbar.make(findViewById(R.id.main_content),"No image has been opened.", Snackbar.LENGTH_SHORT);
                    snac.show();
                }
        }
        });

        swap_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HAS_IMAGE){
                    Intent open_int;

                    open_int = new Intent();
                    open_int.setType("image/*");
                    open_int.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(open_int, "Select Picture"), PICK_SWAP_IMAGE);
                }else{
                    Snackbar snac = Snackbar.make(findViewById(R.id.main_content),"No image has been opened.", Snackbar.LENGTH_SHORT);
                    snac.show();
                }
            }
        });
    }

    //Swap the layout depending on the context of what is happening in the application,
    //either editing an image or one has not been chosen yet.
    private void set_for_choice(){
        choice_view.setVisibility(View.VISIBLE);
        image_view.setVisibility(View.GONE);
        previous_swap.setVisibility(View.VISIBLE);
        squid_tools.setAlpha(.3f);
    }

    private void set_for_image(){
        choice_view.setVisibility(View.GONE);

        image_view.setVisibility(View.VISIBLE);
        squid_tools.setAlpha(1);
    }

    private Intent build_editor_intent(int context){
        Intent in = new Intent(getApplicationContext(),GenericEditorActivity.class);
        in.putExtra("SquidContext",context);

        if(EDITED){
            in.putExtra("tmp",true);
        }else{
            in.putExtra("FocusedBitmap",focused_image.toString());
        }

        return in;
    }
}
