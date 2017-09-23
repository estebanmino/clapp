package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.construapp.construapp.models.RetrieveFeedTask;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;
    RetrieveFeedTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignin = (Button) findViewById(R.id.btn_sigin);
        myAsyncTask = new RetrieveFeedTask();

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this,editEmail.getText().toString(),Toast.LENGTH_SHORT).show();
                myAsyncTask.execute(editEmail.getText().toString(),editPassword.getText().toString());
                Toast.makeText(LoginActivity.this,myAsyncTask.out,Toast.LENGTH_SHORT).show();
                //startActivity(MainActivity.getIntent(LoginActivity.this));
            }
        });

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        return intent;
    }
}
