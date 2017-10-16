package com.construapp.construapp.threading.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.LoginActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ESTEBANFML on 15-10-2017.
 */

public class VolleyLoginConnection {

    public static void volleyLoginConnection(final LoginActivity.VolleyCallback callback, Context context, String email, String password) {
        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String SESSIONS = "sessions";
        String url = BASE_URL + "/" + SESSIONS;
        final RequestQueue queue = Volley.newRequestQueue(context);


        JSONObject jsonObject = new JSONObject();
        final String requestBody = "{\"session\":{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}}";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        Log.i("RESPONSE", response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSE", "Response is:NULL");
                callback.onErrorResponse(error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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
