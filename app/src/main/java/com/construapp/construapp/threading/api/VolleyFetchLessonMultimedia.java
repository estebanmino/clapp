package com.construapp.construapp.threading.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.LessonFormActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 16-10-2017.
 */

public class VolleyFetchLessonMultimedia {

    public static void volleyFetchLessonMultimedia(final LessonActivity.VolleyCallback callback,
                                    Context context,String lessonId) {

        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String COMPANIES = "companies";
        String LESSONS = "lessons";

        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String companyId = sharedpreferences.getString("company_id", "");
        final String userToken = sharedpreferences.getString("token", "");

        String url = BASE_URL +"/"+COMPANIES+"/"+companyId+"/"+LESSONS+"/"+lessonId;

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
        };
        queue.add(jsonObjectRequest);
    }
}
