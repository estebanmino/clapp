package com.construapp.construapp;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_NS = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler mHandler = new Handler();
        Runnable mRunnble = new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.getIntent(SplashActivity.this));
            }
        };
        mHandler.postDelayed(mRunnble, SPLASH_TIME_NS);

    }
}
