package com.construapp.construapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 15-11-2017.
 */

public class VolleyGetCompanyAttributes  {

    public static void volleyGetCompanyAttributes(final VolleyJSONCallback callback,
                                                 Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        final SessionManager sessionManager = new SessionManager(context);

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/" + Constants.ATTRIBUTES;

        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        Log.i("ATTRIBUTES", result.toString());
                        String classificationsStringArray = "";
                        String departmentsStringArray = "";
                        String disciplinesStringArray = "";
                        Log.i("GETJSONCLASSIFICATIONS","hole " + result.toString());

                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(result.toString()).getAsJsonObject();
                        JsonArray jsonClassifications = (JsonArray) json.get(Constants.SP_CLASSIFICATIONS);

                        for (int i = 0; i < jsonClassifications.size(); i++) {
                            JsonElement jsonObject = jsonClassifications.get(i);
                            classificationsStringArray += "/"+jsonObject.getAsJsonObject().get("name").getAsString();
                        }
                        sessionManager.setClassifications(classificationsStringArray);
                        JsonArray jsonDisciplines = (JsonArray) json.get(Constants.SP_DISCIPLINES);
                        for (int i = 0; i < jsonDisciplines.size(); i++) {
                            JsonElement jsonObject = jsonDisciplines.get(i);
                            disciplinesStringArray += "/"+jsonObject.getAsJsonObject().get("name").getAsString();
                        }
                        sessionManager.setDisciplines(disciplinesStringArray);
                        JsonArray jsonDepartments = (JsonArray) json.get(Constants.SP_DEPARTMENTS);
                        for (int i = 0; i < jsonDepartments.size(); i++) {
                            JsonElement jsonObject = jsonDepartments.get(i);
                            departmentsStringArray += "/"+jsonObject.getAsJsonObject().get("name").getAsString();
                        }
                        sessionManager.setDepartments(departmentsStringArray);
                        callback.onSuccess(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RESPONSEPER", error.toString());
                        Log.i("URL",url);
                        Log.i("TOKEN",userToken);
                        callback.onErrorResponse(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.Q_CONTENTTYPE,Constants.Q_CONTENTTYPE_JSON);
                params.put(Constants.Q_AUTHORIZATION,userToken);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

}
