package com.kinghorn.app.squidfaceswap;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by ddcjkinghorn on 11/17/17.
 */

//Class that will be used to hold data about each of the images, such as
    //the original bitmap, where the user highlighted to switch
    //and the actual data to switch.
public class SquidImageData {

    private File save_file;

    public SquidImageData(){
        save_file = new File("testfile.png");
    }

    //Saves the data to a given directory.
    public void save_data(){

    }
}
