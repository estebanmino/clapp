package com.construapp.construapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 22-10-2017.
 */

public class VolleyPutLesson {

    public static void volleyPutLesson(final VolleyJSONCallback callback,
                                       Context context, String lesson_id, String lesson_name,
                                       String lesson_summary, String lesson_motivation,
                                       String lesson_learning, ArrayList<String> array_added, ArrayList<String> array_deleted,
                                       String validation) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/" + Constants.LESSONS + "/" + lesson_id;

        final RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        // TODO: 18-10-2017 refactor json body
        try {
            JSONArray added_array = new JSONArray();
            for(int i=0;i<array_added.size();i++)
            {
                added_array.put(array_added.get(i));
            }

            JSONArray deleted_array = new JSONArray();
            for(int i=0;i<array_deleted.size();i++)
            {
                deleted_array.put(array_deleted.get(i));
            }

            final String requestBody =
                    "{\"lesson\":{\"name\":\"" + lesson_name + "\",\"summary\":\"" + lesson_summary + "\"," +
                            "\"motivation\":\"" + lesson_motivation + "\",\"learning\":\"" + lesson_learning + "\"," +
                            "\"validation\":\"" + validation + "\"}," +
                            "\"array_add_path\":" + added_array + "," +
                            "\"array_delete_path\":" + deleted_array
                            + "}";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
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
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            queue.add(jsonObjectRequest);
        }catch (Exception e){}
    }

}
