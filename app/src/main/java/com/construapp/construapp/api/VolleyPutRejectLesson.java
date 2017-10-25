package com.construapp.construapp.api;

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
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 25-10-2017.
 */

public class VolleyPutRejectLesson {

    public static void volleyPutRejectLesson(final VolleyJSONCallback callback,
                                       Context context, String lesson_id, String comment) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/"
                + Constants.LESSONS + "/" + lesson_id + "/" + Constants.API_REJECT;

        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();

        final JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("reject_comment", comment);
        } catch (Exception e) {}

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSEPER", error.toString());
                Log.i("URL", url);
                Log.i("TOKEN", userToken);
                callback.onErrorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.Q_CONTENTTYPE, Constants.Q_CONTENTTYPE_JSON);
                params.put(Constants.Q_AUTHORIZATION, userToken);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return Constants.Q_CONTENTTYPE_JSON_UTF8;
            }

            @Override
            public byte[] getBody() {
                try {
                    return jsonObject1 == null ? null : jsonObject1.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject1.toString(), "utf-8");
                    return null;
                }
            }
        };
        queue.add(jsonObjectRequest);
    }
}
