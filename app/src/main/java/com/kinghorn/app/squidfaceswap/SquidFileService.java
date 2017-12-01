package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class SquidFileService {
    private FileOutputStream out;
    private File save, newDir;
    private String dir_string;
    private SquidCanvas can;
    private SquidBaseImage bas;
    private SquidSelector sel;

    public SquidFileService(SquidCanvas c,SquidBaseImage b,SquidSelector s){
        dir_string = Environment.getExternalStorageDirectory().toString();

        can = c;
        bas = b;
        sel = s;

        //Make the directory to house the squidswap photos.
        newDir = new File(dir_string + "/squidswap");
        newDir.mkdirs();
    }

    //OUTPUTS A STRING OF THE FILENAME WE ARE GOING TO USE FOR THE GIVEN SAVED
    //IMAGE.
    private String get_filename(){
        //GENERATES A RANDOM NUMBER TO BE USED FOR THE IMAGE NAME;
        int num = 10000;
        Random gen = new Random();
        String filename = "squidswap_"+gen.nextInt(num)+".png";

        return filename;
    }

    //Takes in a Bitmap provided from the data taken from the canvas and saves the
    //data as a bitmap in the squidswap directory.
    public void save_image(Bitmap image_data){
        //Generate a new filename when we want to save an image.
        save = new File(newDir,get_filename());
        System.out.println("Saving file as: "+save);

        //Make sure we can open the stream to save the file.
        try{
            out = new FileOutputStream(save);
            System.out.println("Output stream initialized...");

            //Once the output stream has been initialized we want to saved the file
            //to the given location.
            image_data.compress(Bitmap.CompressFormat.PNG,100,out);
            System.out.println("Image has been saved to the SquidSwap directory!");

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //Takes in the bitmap data for both of the canvases and returns the data for
    //the final image before asking the user if they want to save it.
    public void generate_preview(){
        //Grab the data from both canvases.
        Bitmap base = bas.get_base();
        Bitmap hov = can.getDrawingCache();
        Bitmap fin = Bitmap.createBitmap(bas.getWidth(),bas.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(fin);

        c.drawBitmap(base,0,0,null);
        c.drawBitmap(hov,0,0,null);

        save_image(fin);
    }

    public Drawable load_drawable(Context con, int drawable_id){
        System.out.println("Loading icon from drawable...");
        System.out.println(Integer.toString(drawable_id));

        Drawable d = con.getResources().getDrawable(drawable_id);

        return d;
    }
}
