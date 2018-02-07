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

        //Crops the image to the original size when painting and when the
        if(!prefs.contains("crop_to_original")){
            this.save_pref("crop_to_original",0);
        }

        if(!prefs.contains("dark_theme")){
            this.save_pref("dark_theme",0);
        }

        if(!prefs.contains("autocrop_back")){
            this.save_pref("autocrop_back",0);
        }

        //Checks the preferences to see if the user has already seen the hints or not,
        //Once the GOT IT button on these activitys are clicked it will be set to false and
        //they will no longer show.
        if(!prefs.contains("hint_paint")){
            this.save_pref("hint_paint",1);
        }

        if(!prefs.contains("hint_crop")){
            this.save_pref("hint_crop",1);
        }

        if(!prefs.contains("hint_swap")){
            this.save_pref("hint_swap",1);
        }

        save_pref("hint_crop",1);
    }
}
