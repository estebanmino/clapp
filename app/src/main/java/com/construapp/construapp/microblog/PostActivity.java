package com.construapp.construapp.microblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import com.construapp.construapp.R;

public class PostActivity extends Activity {

    private TextView post;
    private Button editPostButton;
    private Button deletePostButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editPostButton = findViewById(R.id.btn_edit);
        deletePostButton = findViewById(R.id.btn_delete);
    }
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,PostActivity.class);
        return intent;
    }
}
