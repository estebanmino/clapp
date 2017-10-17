package com.construapp.construapp.threading.api;

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
import com.construapp.construapp.LessonFormActivity;
import com.construapp.construapp.LoginActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 15-10-2017.
 */

public class VolleyCreateLesson {

    public static void volleyCreateLesson(final LessonFormActivity.VolleyCallback callback,
                                          Context context, String lesson_name, String lesson_summary,
                                          String lesson_motivation, String lesson_learning,
                                          String project_id) {

        String BASE_URL = "http://construapp-api.ing.puc.cl";
        String COMPANIES = "companies";
        String LESSONS = "lessons";
        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString("company_id", "");
        String user_id = sharedpreferences.getString("user_id", "");
        final String userToken = sharedpreferences.getString("token", "");

        String url = BASE_URL + "/" + COMPANIES + "/" + company_id + "/" + LESSONS;
        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        final String requestBody = "{\"lesson\":{\"name\":\"" + lesson_name + "\",\"summary\":\"" + lesson_summary + "\",\"motivation\":\""+lesson_motivation + "\",\"learning\":\""+lesson_learning + "\",\"user_id\":\""+user_id + "\",\"company_id\":\""+company_id + "\",\"project_id\":\"" + project_id + "\"}}";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {
                        Log.i("RESPONSE", response.toString());
                        callback.onSuccess(response);
                        Log.i("TOKEN",userToken);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSE", "Response is:NULL");
                Log.i("TOKEN",userToken);
                Log.i("JSON",requestBody);
                callback.onErrorResponse(error);
            }

        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("Authorization",userToken);
                return params;
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
