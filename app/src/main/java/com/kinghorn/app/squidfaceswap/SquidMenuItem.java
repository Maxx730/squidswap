package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

//Class for menuitems where string is the label and activity intent goes to where
//the list item should go next.
public class SquidMenuItem {

    private Context context;

    public String lab;
    public Bitmap ico;
    public Intent next;

    public SquidMenuItem(Context con, String label, Bitmap icon, Intent nex){
        context = con;
        next = nex;
        lab = label;
        ico = icon;
    }
}
