package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.icu.util.Output;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class SquidFileService {
    private FileOutputStream out;
    private File save, newDir;
    private String dir_string;
    private static Context con;
    private boolean WATERMARK = true;

    //Constructor for using the file service to load stuff.
    public SquidFileService(Context cont,SquidSelector s){
        con = cont;
    }

    public SquidFileService(Context cont){
        dir_string = Environment.getExternalStorageDirectory().toString();
        con = cont;

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

            if(WATERMARK) {
                //Check if we want to add a watermark if so we need to
                //create a new canvas to put the watermark over the image.
                Bitmap b = Bitmap.createBitmap(image_data.getWidth(), image_data.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(b);

                c.drawBitmap(image_data,0,0,null);

                Paint p = new Paint();
                p.setColor(Color.WHITE);
                p.setAlpha(90);
                p.setTextSize(40);
                p.setAntiAlias(true);
                //Draw the watermark over the completed image.
                //c.drawText("SquidSwap",(image_data.getWidth() / 2)- 50,image_data.getHeight() / 2,p);

                //Set the completed image to the new image with the watermark.
                image_data = b;
            }

            //Once the output stream has been initialized we want to saved the file
            //to the given location.
            image_data.compress(Bitmap.CompressFormat.PNG,100,out);
            MediaScannerConnection.scanFile(con, new String[]{save.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });

            out.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Saves the current image as a temporary file.
    public String save_tmp(Bitmap bmp){
        File fil = new File(con.getCacheDir(),"squidswap_tmp.png");
        OutputStream out = null;
        String fil_name = null;

        try {
            fil.createNewFile();
            out = new FileOutputStream(fil);

            bmp.compress(Bitmap.CompressFormat.PNG,10,out);
            fil_name = fil.getAbsolutePath();

            out.flush();
            out.close();
        } catch (IOException e) {
            Toast.makeText(con,"Error creating temporary file...",Toast.LENGTH_SHORT).show();
        }

        return fil_name;
    }

    //Takes in the bitmap data for both of the canvases and returns the data for
    //the final image before asking the user if they want to save it.
    public Bitmap generate_preview(){
        return Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
    }

    //Function will check and return a bitmap if the image was sent along with the intent.
    public Bitmap open_first(Uri img_path) throws FileNotFoundException {
        InputStream i = con.getContentResolver().openInputStream(img_path);
        Bitmap b = BitmapFactory.decodeStream(i);
        return b;
    }

    //Returns the cached file that was last edited as a bitmap.
    public Bitmap load_cached_file(){
        Bitmap b = null;

        try {
            FileInputStream in = new FileInputStream(new File(con.getCacheDir(),"squidswap_tmp.png"));
            b = BitmapFactory.decodeStream(in);
            File[] files = con.getCacheDir().listFiles();

            for(int i = 0;i < files.length;i++){
                System.out.println(files[i].getAbsolutePath());
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(con,"Error opening cached file...",Toast.LENGTH_SHORT).show();
        }

        return b;
    }


    public Drawable load_drawable(Context con, int drawable_id){
        Drawable d = con.getResources().getDrawable(drawable_id);

        return d;
    }

    //Deletes the temp file stored in the cached directory when the user starts the
    //application over and or when the user finishes and saves the file.
    public void delete_tmp(){
        File f = new File(con.getCacheDir(),"squidswap_tmp.png");

        if(f.delete()){
            System.out.println("Temp file deleted successfully.");
        }

    }
}
