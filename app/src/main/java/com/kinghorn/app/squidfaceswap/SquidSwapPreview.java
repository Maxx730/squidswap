package com.kinghorn.app.squidfaceswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SquidSwapPreview extends AppCompatActivity {

    private ImageView preview_image;
    private SquidFileService squid_files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squid_swap_preview);

        preview_image = (ImageView) findViewById(R.id.preview_image);
        squid_files = new SquidFileService(getApplicationContext());
        Intent prev = getIntent();

        if(prev.hasExtra("tmp")){
            preview_image.setImageBitmap(squid_files.load_cached_file());
        }
    }

}
