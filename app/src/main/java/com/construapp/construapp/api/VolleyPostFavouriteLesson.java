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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 07-11-2017.
 */

public class VolleyPostFavouriteLesson {

    public static void volleyPostFavouriteLesson(final VolleyStringCallback callback,
                                                      Context context, String lesson_id) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");

        String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" +
                Constants.FAVOURITES;

        final RequestQueue queue = Volley.newRequestQueue(context);


        final JSONObject jsonObject1 = new JSONObject();
        final JSONObject jsonObject2 = new JSONObject();

        try {

            jsonObject1.put("lesson_id",Integer.parseInt(lesson_id));
            try {
                jsonObject2.put("favourite",jsonObject1);
            } catch (Exception e) {}
        } catch (Exception e) {}

        Log.i("POST", jsonObject2.toString());
        Log.i("LESSONID",lesson_id);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
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
                    return jsonObject2 == null ? null : jsonObject2.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject2.toString(), "utf-8");
                    return null;
                }
            }

        };
        queue.add(jsonObjectRequest);
    }

}
