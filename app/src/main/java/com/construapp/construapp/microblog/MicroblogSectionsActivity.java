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
import com.construapp.construapp.api.VolleyPostSections;
import com.construapp.construapp.listeners.VolleyStringCallback;

public class MicroblogSectionsActivity extends Activity {

    Button createNewSectionButton;
    EditText name;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microblog_sections);
        createNewSectionButton = findViewById(R.id.button_new_section);
        name = findViewById(R.id.new_section_name);
        description = findViewById(R.id.new_section_description);
       setCreateNewSectionListener();
    }
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,MicroblogSectionsActivity.class);
        return intent;
    }
    public void setCreateNewSectionListener(){
        createNewSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("NAME",name.getText().toString());
                Log.i("DESCRIPTION",description.getText().toString());
                VolleyPostSections.volleyPostSections(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        VolleyGetFavouriteLessons.volleyGetFavouriteLessons(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {

                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                            }
                        },MicroblogSectionsActivity.this);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                    }
                },MicroblogSectionsActivity.this, name.getText().toString());
                startActivity(MicroblogActivity.getIntent(MicroblogSectionsActivity.this));
            }
        });
    }
}
