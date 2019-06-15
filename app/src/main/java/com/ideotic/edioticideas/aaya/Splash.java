package com.ideotic.edioticideas.aaya;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Mukul on 13-05-2016.
 */
public class Splash extends Activity {

    private static int SPLASH_TIMER_COUNT = 5000;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext=this;
        new Handler().postDelayed(new Runnable() {

            //showing splash screen with timer..

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {




                SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                if (pref.getBoolean("activity_executed", false)) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putBoolean("activity_executed", true);
                    ed.commit();
                    //Starts needs activity
                    Intent intent = new Intent(Splash.this, Needs.class);
                    startActivity(intent);
                }


                //closes splash
                finish();
            }
        }, SPLASH_TIMER_COUNT);
    }
}
