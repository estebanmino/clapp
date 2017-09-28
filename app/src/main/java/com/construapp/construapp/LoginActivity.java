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
import android.content.SharedPreferences;

import java.util.concurrent.ExecutionException;

import com.construapp.construapp.threading.RetrieveFeedTask;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;
    static RetrieveFeedTask myAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignin = (Button) findViewById(R.id.btn_sigin);

        //Hacemos manejo por si existe el token

        SharedPreferences mySPrefs =getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        boolean token_exists = mySPrefs.contains("token");
        if(token_exists)
        {
            startActivity(MainActivity.getIntent(LoginActivity.this));
        }


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAsyncTask = new RetrieveFeedTask("login");
                String request="error";
                try {
                     request = myAsyncTask.execute(editEmail.getText().toString(),editPassword.getText().toString()).get();

                }
                catch (ExecutionException e)
                {
                    Log.i("ERR","Execution");
                }
                catch (InterruptedException e)
                {
                    Log.i("ERR","INTERRUPTED");
                }

                //ESTA LINEA HAY QUE BORRARLA
                //request="tokenfalso";

                if(request!="error")
                {
                    Toast.makeText(LoginActivity.this,request,Toast.LENGTH_LONG).show();
                    SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String[] data = request.split(";");

                    editor.putString("token", data[0]);
                    editor.putString("user_id", data[1]);
                    editor.putString("company_id", data[2]);

                    editor.commit();

                    startActivity(MainActivity.getIntent(LoginActivity.this));
                }
                else
                {
                    //myAsyncTask.out=null;
                    Toast.makeText(LoginActivity.this,"Login incorrecto.",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(LoginActivity.this,myAsyncTask.getStatus().toString(),Toast.LENGTH_SHORT).show();

                    myAsyncTask.cancel(true);
                    //Toast.makeText(LoginActivity.this,myAsyncTask.getStatus().toString(),Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        return intent;
    }
}
