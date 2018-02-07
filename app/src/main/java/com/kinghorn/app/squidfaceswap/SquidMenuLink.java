package com.kinghorn.app.squidfaceswap;

import android.content.Intent;

//Class that holds data for a squid menu link, basically the activity they
//need to point to and the name of the link as well as if it needs to take in
//extra info in the intent.
public class SquidMenuLink {
    private String link_title;
    private Intent next;

    public SquidMenuLink(String title,Intent ac){
        this.link_title = title;
        this.next = ac;
    }

    //Getters for returning the links data.
    public String return_title(){
        return this.link_title;
    }

    public Intent return_acivity(){
        return this.next;
    }
}
