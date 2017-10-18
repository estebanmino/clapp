package com.construapp.construapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.construapp.construapp.threading.RetrieveFeedTask;
import com.construapp.construapp.threading.api.VolleyGetProjectPermission;
import com.construapp.construapp.threading.api.VolleyLoginConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;
    static RetrieveFeedTask myAsyncTask;

    private String auth_token = "";
    private String user_id = "";
    private String company_id = "";
    private JSONObject company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignin = (Button) findViewById(R.id.btn_sigin);

        SharedPreferences mySPrefs =getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        boolean token_exists = mySPrefs.contains("token");
        if(token_exists)
        {
            startActivity(MainActivity.getIntent(LoginActivity.this));
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyLoginConnection.volleyLoginConnection(new VolleyCallback(){
                    @Override
                    public void onSuccess(JSONObject result){
                        Toast.makeText(LoginActivity.this,"Bienvenido",Toast.LENGTH_LONG).show();
                        final SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedpreferences.edit();
                        try{
                            auth_token = result.getString("auth_token");
                            user_id = result.getString("id");
                            company = result.getJSONObject("company");
                            company_id = company.getString("id");
                        } catch (Exception e) {}

                        editor.putString("token", auth_token);
                        editor.putString("user_id", user_id);
                        editor.putString("company_id", company_id);
                        editor.apply();

                        VolleyGetProjectPermission.volleyGetProjectPermission(new VolleyProjectsCallback() {
                            @Override
                            public void onSuccess(String result) {
                                JsonParser parser = new JsonParser();
                                JsonArray obj = (JsonArray) parser.parse(result);
                                editor.putString("projects", result);
                                JsonElement jsonObject = obj.get(0);
                                editor.putString("actual_project", jsonObject.getAsJsonObject().get("id").toString());
                                editor.apply();
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {

                            }
                        }, LoginActivity.this, user_id);

                        startActivity(MainActivity.getIntent(LoginActivity.this));
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(LoginActivity.this,"No se pudo ingresar a su cuenta",Toast.LENGTH_SHORT).show();
                    }
                }, LoginActivity.this, editEmail.getText().toString(), editPassword.getText().toString());
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        LoginActivity.this.finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        return intent;
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
        void onErrorResponse(VolleyError result);
    }


    public interface VolleyProjectsCallback{
        void onSuccess(String result);
        void onErrorResponse(VolleyError result);
    }

}
