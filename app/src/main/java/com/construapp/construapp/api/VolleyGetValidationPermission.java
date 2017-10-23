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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose on 23-10-17.
 */

public class VolleyGetValidationPermission {

    public static void volleyGetValidationPermission(final VolleyStringCallback callback,
                                                      Context context) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String userId = sharedpreferences.getString(Constants.SP_USER,"");

        final String url = Constants.BASE_URL + "/" + Constants.USERS + "/" + userId + "/" + Constants.GET_PROJECTS;

        final RequestQueue queue = Volley.newRequestQueue(context);

        Log.i("VolleyValidate","Volley");

        //JSONObject jsonObject = new JSONObject();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String  response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSERRORPERMISSION", error.toString());
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
