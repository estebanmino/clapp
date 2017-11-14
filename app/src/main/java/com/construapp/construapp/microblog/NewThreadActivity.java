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
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.api.VolleyPostSections;
import com.construapp.construapp.api.VolleyPostThreads;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.SessionManager;

public class NewThreadActivity extends Activity {

    Button createNewThreadButton;
    EditText title;
    EditText description;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        createNewThreadButton = findViewById(R.id.button_new_thread);
        title = findViewById(R.id.new_thread_name);
        description = findViewById(R.id.new_thread_description);
        setCreateNewThreadListener();
        sessionManager = new SessionManager(NewThreadActivity.this);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,NewThreadActivity.class);
        return intent;
    }
    public void setCreateNewThreadListener(){
        createNewThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TITLE",title.getText().toString());
                Log.i("DESCRIPTION",description.getText().toString());
                VolleyPostThreads.volleyPostThreads(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        VolleyGetThreads.volleyGetThreads(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {

                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                            }
                        },NewThreadActivity.this);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                    }
                },NewThreadActivity.this, sessionManager.getSection(),title.getText().toString());
                finish();
            }
        });
    }
}
