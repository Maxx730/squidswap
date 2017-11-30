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

//Displays all the settings options.
public class SquidSwapSettings extends AppCompatActivity {
    private ListView list;
    private SquidListAdapter adapt;
    private ImageView ex;
    private SquidFileService file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squidswap_settings);

        ArrayList<SquidMenuItem> menu_items = new ArrayList<SquidMenuItem>();

        list = (ListView) findViewById(R.id.squid_settings_list);
        adapt = new SquidListAdapter(this,R.layout.squidswap_menu_layout,menu_items);
        ex = (ImageView) findViewById(R.id.settings_back);
        file = new SquidFileService();

        menu_items.add(0,new SquidMenuItem(this,"Autosave Thumbnails",file.load_drawable(this,R.drawable.ic_save_black_24dp),new Intent(this,SquidSwapMain.class),"Toggle"));
        menu_items.add(0,new SquidMenuItem(this,"Save High Resolution Photos",file.load_drawable(this,R.drawable.ic_save_black_24dp),new Intent(this,SquidSwapMain.class),"Toggle"));

        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getApplicationContext(),SquidSwapMain.class);
                startActivity(main);
            }
        });

        list.setAdapter(adapt);
    }
}
