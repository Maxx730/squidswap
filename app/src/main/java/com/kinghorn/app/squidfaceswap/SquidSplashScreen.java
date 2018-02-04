package com.kinghorn.app.squidfaceswap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SquidSplashScreen extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squid_splash_screen);

        final Intent inten = new Intent(SquidSplashScreen.this,SquidSwapMain.class);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(inten);
            }
        },1000);
    }
}
