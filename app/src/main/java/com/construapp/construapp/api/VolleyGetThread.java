package com.construapp.construapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JOSE on 06-11-2017.
 */

public class VolleyGetThread {

    public static void volleyGetThread(final VolleyStringCallback callback,
                                        Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String company_id = sharedpreferences.getString(Constants.SP_COMPANY,"");
        final String thread_id = sharedpreferences.getString(Constants.SP_THREAD_ID,"");
        //// TODO: 06-11-17 jose conseguir section id y terminar request, y luego hacer el override de onresponse en la clase
        final String section_id = sharedpreferences.getString(Constants.SP_ACTUAL_SECTION,"");
        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" + Constants.SECTIONS+"/"+section_id+"/"+Constants.THREADS+"?thread_id="+thread_id;

        final RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String  response) {
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
                params.put(Constants.Q_CONTENTTYPE,Constants.Q_CONTENTTYPE_JSON);
                params.put(Constants.Q_AUTHORIZATION,userToken);
                return params;
            }

        };
        queue.add(jsonObjectRequest);
    }



}
