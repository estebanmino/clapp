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
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.api.VolleyPostSections;
import com.construapp.construapp.api.VolleyPostThreads;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.SessionManager;

public class NewThreadActivity extends Activity {

    FloatingActionButton fabCreateThread;
    EditText editTitle;
    EditText editDescription;
    SessionManager sessionManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nuevo Post");
        fabCreateThread = findViewById(R.id.fab_send);
        editTitle = findViewById(R.id.new_thread_name);
        editDescription = findViewById(R.id.new_thread_description);
        setFabCreateThreadListener();
        sessionManager = new SessionManager(NewThreadActivity.this);
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,NewThreadActivity.class);
        return intent;
    }
    public void setFabCreateThreadListener(){
        fabCreateThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Log.i("TITLE",editTitle.getText().toString());
            Log.i("DESCRIPTION",editDescription.getText().toString());
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
            },NewThreadActivity.this, sessionManager.getSection(),editTitle.getText().toString(),editDescription.getText().toString());
            finish();
            }
        });
    }
}
