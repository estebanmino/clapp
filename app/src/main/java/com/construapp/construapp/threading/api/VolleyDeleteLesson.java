package com.construapp.construapp.threading.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.LessonActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 16-10-2017.
 */

public class VolleyDeleteLesson {

    public static void volleyDeleteLesson(final LessonActivity.VolleyStringCallback callback,
                                          Context context,
                                          String lesson_id) {

        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String COMPANIES = "companies";
        String LESSONS = "lessons";
        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString("company_id", "");
        String user_id = sharedpreferences.getString("user_id", "");
        final String userToken = sharedpreferences.getString("token", "");

        String url = BASE_URL + "/" + COMPANIES + "/" + company_id + "/" + LESSONS + "/" + lesson_id;
        final RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String  response) {
                    callback.onSuccess(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onErrorResponse(error);
                }
            }

        ){
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
