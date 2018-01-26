package com.kinghorn.app.squidfaceswap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//Inflates different menu items into a listview, used in settings etc.
public class SquidListAdapter extends ArrayAdapter {

    private Context context;
    private String type;
    private LayoutInflater inflate;
    private ArrayList<String> settings_options;
    private ArrayList<SquidMenuItem> it;
    private SquidSettingsHandler settings;

    public SquidListAdapter(@NonNull Context con, @LayoutRes int resource,ArrayList<SquidMenuItem> items) {
        super(con, resource,items);

        settings = new SquidSettingsHandler(con);
        context = con;
        it = items;
    }

    public SquidListAdapter(@NonNull Context con, @LayoutRes int resource,ArrayList<SquidMenuItem> items,SquidSettingsHandler set) {
        super(con, resource,items);

        settings = set;
        context = con;
        it = items;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch(it.get(position).typ){
            case "Toggle":
                v = infl.inflate(R.layout.squidswap_menu_toggle,null);

                //Set the onclick event for the given toggle.
                final Switch s = (Switch) v.findViewById(R.id.squid_menu_switch);

                if(settings.load_pref(it.get(position).pref) == 1){
                    s.setChecked(true);
                }

                s.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(s.isChecked()){
                            settings.save_pref(it.get(position).pref,1);
                        }else{
                            settings.save_pref(it.get(position).pref,0);
                        }
                    }
                });
                break;
            case "Link":
                v = infl.inflate(R.layout.squidswap_menu_layout,null);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent next = it.get(position).next;
                        context.startActivity(next);
                    }
                });
                break;
        }
        TextView label = v.findViewById(R.id.textView);
        //ImageView icon = (ImageView) v.findViewById(R.id.menu_icon);

        //icon.setImageDrawable(it.get(position).ico);
        label.setText(it.get(position).lab);
        return v;
    }
}
