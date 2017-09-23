package com.construapp.construapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShowInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(25);
        textView.setPadding(10,5,0,0);
        textView.setTextColor(Color.parseColor("#08088A"));
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_show_info);
        layout.addView(textView);
    }
}
