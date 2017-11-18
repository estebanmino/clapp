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
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose on 16-11-17.
 */

public class VolleyGetLessonsBySearch {

    public static void volleyGetLessonsBySearch(final VolleyStringCallback callback,
                                        Context context,String search_params) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        try {
            search_params = URLEncoder.encode(search_params, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.i("SEARCH",e.toString());
            e.printStackTrace();
        }

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/" + Constants.SEARCH+"?"+Constants.PATTERN+"="+search_params;

        final RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String  response) {
                        callback.onSuccess(response);
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
