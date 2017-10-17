package com.construapp.construapp.threading.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.LessonFormActivity;
import com.construapp.construapp.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 16-10-2017.
 */

public class VolleyPostS3 {


    public static void volleyPostS3(final LessonFormActivity.VolleyCallback callback,
                                    Context context, String lesson_id, String[] lesson_key_files) {

        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String COMPANIES = "companies";
        String LESSONS = "lessons";
        String SAVE_KEY = "save_key";

        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString("company_id", "");

        String user_id = sharedpreferences.getString("user_id", "");
        final String userToken = sharedpreferences.getString("token", "");

        String url = BASE_URL + "/" + COMPANIES + "/" + company_id + "/" + LESSONS + "/" + lesson_id + "/" + SAVE_KEY;
        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        JSONArray routes_array = new JSONArray();

        for(int i=0;i<lesson_key_files.length;i++)
        {
            routes_array.put(lesson_key_files[i]);
        }
        final String requestBody = "{\"array_file_path\":"+routes_array+"}";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }

        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("Authorization",userToken);
                return params;
            }

            @Override
            public byte[] getBody()  {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        queue.add(jsonObjectRequest);
    }

}
