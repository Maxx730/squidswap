package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

//Class that handles saving and loading settings for the application.
public class SquidSettingsHandler {

    private String CROPPED_SAVING,HIGH_RES_PHOTOS;
    private SharedPreferences prefs;
    private Context c;

    public SquidSettingsHandler(Context con){
        c = con;

        //Load the shared preferences object into the context.
        prefs = PreferenceManager.getDefaultSharedPreferences(con);
        //Initialize our preferences, check if they exist, if they do not then we want to
        //initialize them as the default values.
        this.init_prefs();
    }

    //Saves a pref with the given value as an integer, might need to make
    //a string saving function down the line.
    public void save_pref(String pref_name,int val){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(pref_name,val);
        edit.commit();
    }

    public int load_pref(String pref_name){
        return prefs.getInt(pref_name,0);
    }

    //If the apps prefs do not exist yet then we want to set them to the default
    //value, most of the time this will be off i.e 0
    private void init_prefs(){
        SharedPreferences.Editor edit = prefs.edit();

        if(!prefs.contains("save_high_res")){
            this.save_pref("save_high_res",0);
        }

        if(!prefs.contains("watermark")){
            this.save_pref("watermark",0);
        }

        if(!prefs.contains("crop_to_original")){
            this.save_pref("crop_to_original",0);
        }

        if(!prefs.contains("dark_theme")){
            this.save_pref("dark_theme",0);
        }
    }
}
