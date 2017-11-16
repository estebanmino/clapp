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

import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyGetFavouriteLessons;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.api.VolleyGetUserProject;
import com.construapp.construapp.api.VolleyLoginConnection;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.microblog.MicroblogActivity;
import com.construapp.construapp.models.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignin;

    private String user_id = "";
    private String admin = "";
    private JSONObject company;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(LoginActivity.this);

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnSignin = findViewById(R.id.btn_sigin);

        if (sessionManager.isLoggedIn()) {
            startActivity(MainActivity.getIntent(LoginActivity.this));
        } else {
            btnSignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VolleyLoginConnection.volleyLoginConnection(new VolleyJSONCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                            try {
                                admin = result.getString("admin");
                                user_id = result.getString("id");
                                company = result.getJSONObject("company");
                                sessionManager.saveLogInData(
                                        result.getString("auth_token"),
                                        result.getString("admin"),
                                        result.getString("id"),
                                        company.getString("id"));
                            } catch (Exception e) {}

                            Log.i("PERMISSION",admin);

                            VolleyGetFavouriteLessons.volleyGetFavouriteLessons(new VolleyStringCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    sessionManager.setFavouriteLessons(result);
                                }

                                @Override
                                public void onErrorResponse(VolleyError result) {
                                }
                            }, LoginActivity.this);

                            VolleyGetUserProject.volleyGetUserProject(new VolleyStringCallback() {
                                @Override
                                public void onSuccess(String result) {

                                    JsonParser parser = new JsonParser();
                                    ArrayList<String> arrayList = new ArrayList<>();
                                    JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JsonElement jsonObject = jsonArray.get(i);
                                        sessionManager.setProjectPermission(
                                                jsonObject.getAsJsonObject().get("project").getAsJsonObject().get("id").toString(),
                                                jsonObject.getAsJsonObject().get("permission_id").toString()
                                        );
                                        arrayList.add(jsonObject.getAsJsonObject().get("project").toString());
                                    }

                                    JsonArray obj = (JsonArray) parser.parse(arrayList.toString());
                                    sessionManager.setProjects(obj);
                                    if (sessionManager.hasProjects()) {
                                        JsonElement jsonObject = obj.get(0);
                                        sessionManager.setActualProject(
                                                jsonObject.getAsJsonObject().get("id").toString(),
                                                jsonObject.getAsJsonObject().get("name").toString());
                                    }
                                    startActivity(MainActivity.getIntent(LoginActivity.this));
                                }

                                @Override
                                public void onErrorResponse(VolleyError result) {
                                    Toast.makeText(LoginActivity.this, "No se pudo ingresar a su cuenta", Toast.LENGTH_SHORT).show();
                                }
                            }, LoginActivity.this, user_id);
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
}
