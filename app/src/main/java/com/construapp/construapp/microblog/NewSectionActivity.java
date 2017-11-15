package com.construapp.construapp.microblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
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

    Button createNewSectionButton;
    EditText name;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_section);
        createNewSectionButton = findViewById(R.id.button_new_section);
        name = findViewById(R.id.new_section_name);
        description = findViewById(R.id.new_section_description);
        setCreateNewSectionListener();
    }
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,NewSectionActivity.class);
        return intent;
    }
    public void setCreateNewSectionListener(){
        createNewSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("NAME",name.getText().toString());
                Log.i("DESCRIPTION",description.getText().toString());
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
                },NewSectionActivity.this, name.getText().toString(), description.getText().toString()) ;
                startActivity(MicroblogActivity.getIntent(NewSectionActivity.this));
            }
        });
    }
}
