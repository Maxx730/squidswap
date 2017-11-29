package com.kinghorn.app.squidfaceswap;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;

//Inflates different menu items into a listview, used in settings etc.
public class SquidListAdapter extends ArrayAdapter {

    private Context context;
    private String type;
    private LayoutInflater inflate;
    private ArrayList<String> settings_options;
    private ArrayList<SquidMenuItem> it;

    public SquidListAdapter(@NonNull Context con, @LayoutRes int resource,ArrayList<SquidMenuItem> items) {
        super(con, resource,items);

        context = con;
        it = items;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("Inflating list item...");
        View v = convertView;

        LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = infl.inflate(R.layout.squidswap_menu_layout,null);

        TextView label = v.findViewById(R.id.textView);
        ImageView icon = (ImageView) v.findViewById(R.id.menu_icon);

        System.out.println(it.get(position).ico.toString());
        icon.setImageDrawable(it.get(position).ico);
        label.setText(it.get(position).lab);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Launching activity for given list item...");
                view.setBackgroundColor(Color.WHITE);

                TextView t = (TextView)view.findViewById(R.id.textView);


                t.setTextColor(Color.BLACK);
                context.startActivity(it.get(position).next);
            }
        });

        return v;
    }
}
