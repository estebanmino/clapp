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
import com.construapp.construapp.models.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 16-10-2017.
 */

public class VolleyDeleteLesson {

    public static void volleyDeleteLesson(final LessonActivity.VolleyStringCallback callback,
                                          Context context,
                                          String lesson_id) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");

        String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" + Constants.LESSONS + "/" + lesson_id;
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
                params.put(Constants.Q_CONTENTTYPE,Constants.Q_CONTENTTYPE_JSON);
                params.put(Constants.Q_AUTHORIZATION,userToken);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
