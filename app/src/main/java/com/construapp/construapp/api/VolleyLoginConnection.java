package com.construapp.construapp.api;

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
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ESTEBANFML on 15-10-2017.
 */

public class VolleyLoginConnection {

    public static void volleyLoginConnection(final VolleyJSONCallback callback,
                                             Context context, String email, String password) {

        String url = Constants.BASE_URL + "/" + Constants.SESSIONS;
        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        // TODO: 18-10-2017 refactor json
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
                return Constants.Q_CONTENTTYPE_JSON_UTF8;
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
