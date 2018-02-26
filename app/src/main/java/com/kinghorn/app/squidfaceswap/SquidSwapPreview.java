package com.kinghorn.app.squidfaceswap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preview_menu, menu);
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
            case R.id.share:
                Intent shar = new Intent();
                shar.setAction(Intent.ACTION_SEND);
                //Grab the URI of the temporary image and send it to next activity.
                shar.putExtra(Intent.EXTRA_TEXT,"testing");
                shar.setType("text/plain");
                startActivity(shar);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
