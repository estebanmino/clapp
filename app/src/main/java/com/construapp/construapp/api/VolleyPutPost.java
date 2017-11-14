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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose on 14-11-17.
 */

public class VolleyPutPost {
    public static void volleyPutPost(final VolleyStringCallback callback,
                                       Context context, String text,int id) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");
        String section_id = sharedpreferences.getString(Constants.SP_ACTUAL_SECTION, "");
        String thread_id = sharedpreferences.getString(Constants.SP_THREAD_ID, "");
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");

        String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" + Constants.SECTIONS+"/" + section_id + "/"+Constants.POSTS;

        final RequestQueue queue = Volley.newRequestQueue(context);


        final JSONObject jsonObject1 = new JSONObject();



        try {
            Log.i("TOKEN",userToken);
            Log.i("TEXT",text);
            Log.i("id",Integer.toString(id));
            jsonObject1.put("id",id);
            jsonObject1.put("text",text);
        } catch (Exception e) {}


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                new com.android.volley.Response.Listener<String>() {
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
        )
        {
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
