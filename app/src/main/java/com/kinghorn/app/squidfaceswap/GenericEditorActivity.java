package com.kinghorn.app.squidfaceswap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

//Generic activity class that will be loaded everytime a tool to
//manipulate the image is clicked, the images that will be shown
//are based on the tool they want to use.
public class GenericEditorActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Switch different layouts based on the chosen activity.
        setContentView(R.layout.generic_editor_layout);
    }
}
