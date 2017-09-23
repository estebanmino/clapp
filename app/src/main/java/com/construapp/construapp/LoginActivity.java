package com.construapp.construapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;

import com.construapp.construapp.models.OnTaskCompleted;
import com.construapp.construapp.models.RetrieveFeedTask;

public class LoginActivity extends AppCompatActivity implements OnTaskCompleted {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;
    RetrieveFeedTask myAsyncTask;
    OnTaskCompleted listener;


    public void onTaskCompleted()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignin = (Button) findViewById(R.id.btn_sigin);


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this,editEmail.getText().toString(),Toast.LENGTH_SHORT).show();
                myAsyncTask = new RetrieveFeedTask(LoginActivity.this);
                myAsyncTask.execute(editEmail.getText().toString(),editPassword.getText().toString());

                while(myAsyncTask.out == null)
                {

                }
                if(myAsyncTask.out!="error")
                {
                    Toast.makeText(LoginActivity.this,myAsyncTask.out,Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("token", myAsyncTask.out);
                    editor.commit();

                    startActivity(MainActivity.getIntent(LoginActivity.this));
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Login incorrecto.",Toast.LENGTH_SHORT).show();
                    myAsyncTask.cancel(true);
                }

                //TODO agregar un thread.kill para asegurar que se cierre.
            }
        });

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        return intent;
    }
}
