package com.construapp.construapp.threading.api;

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
import com.construapp.construapp.LoginActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 18-10-2017.
 */

public class VolleyGetUserProjectPermission {

    public static void volleyGetUserProjectPermission(final LoginActivity.VolleyProjectPermissionCallback callback,
                                            Context context, String user_id, String project_id) {

        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String GET_PERMISSION = "get_permission";
        String USERS = "users";
        String PROJECTS = "projects";


        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString("token", "");

        final String url = BASE_URL + "/" + USERS + "/" + user_id + "/" + PROJECTS + "/" + project_id + "/" + GET_PERMISSION;

        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
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
                params.put("Content-Type","application/json");
                params.put("Authorization",userToken);
                return params;
            }

        };
        queue.add(jsonObjectRequest);
    }
}
