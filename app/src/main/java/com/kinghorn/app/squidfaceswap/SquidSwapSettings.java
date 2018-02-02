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

    }
}
