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
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.models.ThreadBlog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 16/11/2017.
 */

public class VolleyPutThread {
    public static void volleyPutThread(final VolleyStringCallback callback,
                                       Context context, String name, String description, String thread_id,
                                       ArrayList<MultimediaFile> selectedMultimediaFiles,
                                       ThreadBlog threadBlog) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");
        String section_id = sharedpreferences.getString(Constants.SP_ACTUAL_SECTION, "");
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");

        String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" +
                Constants.SECTIONS + "/" + section_id + "/" + Constants.THREADS;

        final RequestQueue queue = Volley.newRequestQueue(context);

        ArrayList<String> newFileKeysArray = new ArrayList<>();
        for (MultimediaFile multimediaFile: selectedMultimediaFiles) {
;            newFileKeysArray.add(multimediaFile.getApiFileKey());
        }


        JSONArray jsonArrayAddedFileKeys = getAddedArray(threadBlog.getSavedMultimediaFileKeys(), newFileKeysArray);
        JSONArray jsonArrayDeletedFileKeys = getDeletedArray(threadBlog.getSavedMultimediaFileKeys(), newFileKeysArray);

        final String requestBody =
                "{\"id\":\"" + thread_id +
                "\",\"title\":\"" + name +
                "\",\"text\":\"" + description +
                "\",\"delete_files\":" + jsonArrayDeletedFileKeys +
                ",\"add_files\":" + jsonArrayAddedFileKeys + "}" ;

        Log.i("PUTLESSON",requestBody.replace("\\\\",""));

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
                        Log.i("ERRORPUT", error.toString());
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
                    return requestBody == null ? null : requestBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody.toString(), "utf-8");
                    return null;
                }
            }

        };
        queue.add(jsonObjectRequest);
    }

    public static JSONArray getAddedArray(String[] originalArray, ArrayList<String> end){
        ArrayList<String> original = new ArrayList<>();
        for (int i =0; i < originalArray.length; i++){
            original.add(originalArray[i]);
        }
        JSONArray jsonArray = new JSONArray();
        for (String endString: end) {
            if (!original.contains(endString.replace("\\\\",""))) {
                jsonArray.put(endString.replace("\\\\",""));
            }
        }
        return jsonArray;
    }

    public static JSONArray getDeletedArray(String[] originalArray, ArrayList<String> end) {
        ArrayList<String> original = new ArrayList<>();
        for (int i =0; i < originalArray.length; i++){
            original.add(originalArray[i]);
        }
        JSONArray jsonArray = new JSONArray();
        for (String originalString: original) {
            if (!end.contains(originalString.replace("\\\\",""))){
                jsonArray.put(originalString.replace("\\\\",""));
            }
        }
        return jsonArray;
    }
}
