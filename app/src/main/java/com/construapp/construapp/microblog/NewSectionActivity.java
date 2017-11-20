package com.construapp.construapp.microblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetFavouriteLessons;
import com.construapp.construapp.api.VolleyGetSections;
import com.construapp.construapp.api.VolleyPostSections;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;

import org.json.JSONObject;

public class NewSectionActivity extends Activity {

    FloatingActionButton fabCreateSection;
    EditText editName;
    EditText editDescription;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_section);
        fabCreateSection = findViewById(R.id.fab_send);
        editName = findViewById(R.id.new_section_name);
        editDescription = findViewById(R.id.new_section_description);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nueva sección");
        setFabCreateSectionListener();
    }
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,NewSectionActivity.class);
        return intent;
    }
    public void setFabCreateSectionListener(){
        fabCreateSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("NAME",editName.getText().toString());
                Log.i("DESCRIPTION",editDescription.getText().toString());
                VolleyPostSections.volleyPostSections(new VolleyJSONCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        VolleyGetSections.volleyGetSections(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {

                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                            }
                        }, NewSectionActivity.this);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                    }
                },NewSectionActivity.this, editName.getText().toString(), editDescription.getText().toString()) ;
                finish();
            }
        });
    }
}
