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
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.api.VolleyGetUserProject;
import com.construapp.construapp.api.VolleyGetUserProjectPermission;
import com.construapp.construapp.api.VolleyLoginConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;

    private String auth_token = "";
    private String user_id = "";
    private String company_id = "";
    private String admin = "";
    private JSONObject company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignin = (Button) findViewById(R.id.btn_sigin);

        final SharedPreferences sharedPreferences =getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.SP_TOKEN)) {
            startActivity(MainActivity.getIntent(LoginActivity.this));
        } else {
            btnSignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VolleyLoginConnection.volleyLoginConnection(new VolleyJSONCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            try {
                                auth_token = result.getString("auth_token");
                                admin = result.getString("admin");
                                user_id = result.getString("id");
                                company = result.getJSONObject("company");
                                company_id = company.getString("id");
                            } catch (Exception e) {
                            }

                            editor.putString(Constants.SP_TOKEN, auth_token);
                            editor.putString(Constants.SP_USER, user_id);
                            editor.putString(Constants.SP_COMPANY, company_id);
                            editor.putString(Constants.SP_ADMIN, admin);
                            Log.i("PERMISSION",admin);
                            editor.apply();

                            VolleyGetUserProject.volleyGetUserProject(new VolleyStringCallback() {
                                @Override
                                public void onSuccess(String result) {

                                    JsonParser parser = new JsonParser();
                                    ArrayList<String> arrayList = new ArrayList<>();
                                    JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JsonElement jsonObject = (JsonElement) jsonArray.get(i);
                                        arrayList.add(jsonObject.getAsJsonObject().get("project").toString());
                                    }
                                    JsonArray obj = (JsonArray) parser.parse(arrayList.toString());
                                    //Log.i("JSONARRAY",obj.toString());
                                    //Log.i("JSONARRAY",Integer.toString(obj.size()));
                                    editor.putString(Constants.SP_PROJECTS, obj.toString());
                                    if (obj.size() != 0) {
                                        editor.putBoolean(Constants.SP_HAS_PROJECTS, true);
                                        editor.apply();
                                        JsonElement jsonObject = obj.get(0);
                                        editor.putString(Constants.SP_ACTUAL_PROJECT, jsonObject.getAsJsonObject().get("id").toString());
                                        editor.apply();
                                        VolleyGetUserProjectPermission.volleyGetUserProjectPermission(new VolleyJSONCallback() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                try {
                                                    editor.putString(Constants.SP_USER_PERMISSION_NAME,
                                                            result.get(Constants.SP_HAS_PERMISSION).toString());
                                                    editor.apply();
                                                    startActivity(MainActivity.getIntent(LoginActivity.this));
                                                } catch (Exception e) {
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError result) {

                                            }
                                        }, LoginActivity.this, user_id, jsonObject.getAsJsonObject().get("id").toString());
                                    } else {
                                        editor.putBoolean(Constants.SP_HAS_PROJECTS, false);
                                        editor.apply();
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError result) {

                                }
                            }, LoginActivity.this, user_id);

                            startActivity(MainActivity.getIntent(LoginActivity.this));
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {
                            Toast.makeText(LoginActivity.this, "No se pudo ingresar a su cuenta", Toast.LENGTH_SHORT).show();
                        }
                    }, LoginActivity.this, editEmail.getText().toString(), editPassword.getText().toString());
                }
            });
        }
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
                .setTitle("Salir")
                .setMessage("¿Estás seguro que quieres salir?")
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

    public interface VolleyProjectPermissionCallback{
        void onSuccess(JSONObject result);
        void onErrorResponse(VolleyError result);
    }

}
