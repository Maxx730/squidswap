package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SquidToolMenuAdapter extends ArrayAdapter {
    private SquidSettingsHandler squid_settings;
    private ArrayList<> items = new ArrayList<>();

    public SquidToolMenuAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        //We need access to our settings
        squid_settings = new SquidSettingsHandler(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);


    }
}
