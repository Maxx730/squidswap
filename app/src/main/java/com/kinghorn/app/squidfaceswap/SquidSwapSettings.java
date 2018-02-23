package com.kinghorn.app.squidfaceswap;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

//Displays all the settings options.
public class SquidSwapSettings extends AppCompatActivity {
    private ListView list;
    private SquidListAdapter adapt;
    private ImageView ex;
    private SquidFileService file;
    private ListView settings_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squidswap_settings);

        //Sets up settings menu
        settings_list = (ListView) findViewById(R.id.settings_list);
        SquidFileService file = new SquidFileService(getApplicationContext(),null);
        ArrayList<SquidMenuItem> items = new ArrayList<SquidMenuItem>();
        items.add(0,new SquidMenuItem(this,"Show Paintbrush Sizing Slider",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","save_high_res"));
        items.add(0,new SquidMenuItem(this,"Autocrop Paint/Swap",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","crop_to_original"));
        items.add(0,new SquidMenuItem(this,"Autoscale Background",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","autoscale_back"));
        items.add(0,new SquidMenuItem(this,"Watermark",file.load_drawable(this,R.drawable.ic_image_black_24dp),new Intent(this,SquidAboutPage.class),"Toggle","watermark"));
        adapt = new SquidListAdapter(getApplicationContext(),R.layout.squidswap_menu_layout,items,1);
        settings_list.setAdapter(adapt);
    }
}


