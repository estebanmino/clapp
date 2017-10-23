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
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 22-10-2017.
 */

public class VolleyPatchLesson {

    public static void volleyPatchLesson(final VolleyJSONCallback callback,
                                         Context context, String lesson_id, String lesson_name,
                                         String lesson_summary, String lesson_motivation,
                                         String lesson_learning) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/" + Constants.LESSONS + "/" + lesson_id;

        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        // TODO: 18-10-2017 refactor json body
        final String requestBody =
                "{\"lesson\":{\"name\":\"" + lesson_name + "\",\"summary\":\"" + lesson_summary + "\"," +
                        "\"motivation\":\""+lesson_motivation + "\",\"learning\":\""+lesson_learning + "\"}}";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url,jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
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
