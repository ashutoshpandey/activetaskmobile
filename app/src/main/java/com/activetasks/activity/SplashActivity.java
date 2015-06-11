package com.activetasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import activetasks.activetasks.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        findViewById(R.id.btnNext).setOnTouchListener(mDelayHideTouchListener);
    }

    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);

            return false;
        }
    };
}
