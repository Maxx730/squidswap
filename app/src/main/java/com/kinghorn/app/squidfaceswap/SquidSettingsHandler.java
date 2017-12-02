package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//Class that handles saving and loading settings for the application.
public class SquidSettingsHandler {

    private String CROPPED_SAVING,HIGH_RES_PHOTOS;
    private SharedPreferences prefs;
    private Context c;

    public SquidSettingsHandler(Context con){
        c = con;

        //Load the shared preferences object into the context.
        prefs = PreferenceManager.getDefaultSharedPreferences(con);
    }

    public void save_pref(String pref_name,int val){
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(pref_name,val);
        edit.commit();
    }

    public int load_pref(String pref_name){
        return prefs.getInt(pref_name,0);
    }
}
