package com.activetasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import activetasks.activetasks.R;

public class RegisteredActivity extends ActionBarActivity {

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        btnLogin = (Button)findViewById(R.id.btnRegisteredLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisteredActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(RegisteredActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
