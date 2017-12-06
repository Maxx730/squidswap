package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

public class SquidFileService {
    private FileOutputStream out;
    private File save, newDir;
    private String dir_string;
    private SquidCanvas can,bas;
    private SquidSelector sel;
    private SquidBitmapData dat;
    private static Context c;

    //Constructor for using the file service to load stuff.
    public SquidFileService(Context con,SquidSelector s){
        c = con;
        sel = s;
    }

    public SquidFileService(SquidCanvas c,SquidCanvas b,SquidSelector s){
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
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }

    //Takes in the bitmap data for both of the canvases and returns the data for
    //the final image before asking the user if they want to save it.
    public Bitmap generate_preview(){
        //Grab the data from both canvases.
        Bitmap base = bas.getDrawingCache();
        Bitmap hov = can.get_faded_img();
        Bitmap fin = Bitmap.createBitmap(bas.getWidth(),bas.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(fin);

        c.drawBitmap(base,0,0,null);
        c.drawBitmap(hov,dat.x,dat.y,null);

        save_image(fin);
        return fin;
    }

    //Function will check and return a bitmap if the image was sent along with the intent.
    public Bitmap open_first(Intent cont) throws FileNotFoundException {
        Intent in = cont;
        String path = in.getStringExtra("chosen_uri");
        InputStream i = c.getContentResolver().openInputStream(Uri.parse(path));
        Bitmap b = BitmapFactory.decodeStream(i);

        return b;
    }
}
