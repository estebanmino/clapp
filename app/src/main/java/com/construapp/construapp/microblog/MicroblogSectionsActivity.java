package com.construapp.construapp.microblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetPendingValidations;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.main.MicroblogActivity;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;

public class MicroblogSectionsActivity extends Activity {

    Button createNewSectionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microblog_sections);
        createNewSectionButton = findViewById(R.id.button_new_section);
        createNewSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MicroblogActivity.getIntent(MicroblogSectionsActivity.this));
            }
        });
    }
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,MicroblogSectionsActivity.class);
        return intent;
    }
}
