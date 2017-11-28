package com.kinghorn.app.squidfaceswap;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = infl.inflate(R.layout.squidswap_list_about,null);

        TextView label = v.findViewById(R.id.textView);

        label.setText(it.get(position).lab);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Launching activity for given list item...");

                context.startActivity(it.get(position).next);
            }
        });

        return v;
    }
}
