package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 12-10-2017.
 */

public class RetrieveLessonMultimedia  extends AsyncTask<String, Integer, String> {


    private static  String BASE_URL = "http://construapp-api.ing.puc.cl";
    private static  String COMPANIES = "companies";
    private static  String LESSONS = "lessons";
    private static  String SAVE_KEY = "save_key";
    private static  String REQUEST_POST = "POST";


    private Context context;
    private String companyId;
    private String lessonId;
    private String userToken;

    public RetrieveLessonMultimedia(Context context,String conpanyId, String lessonId, String userToken) {
        this.context = context;
        this.companyId = conpanyId;
        this.lessonId = lessonId;
        this.userToken = userToken;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BASE_URL +"/"+COMPANIES+"/"+companyId+"/"+LESSONS+"/"+lessonId;
        String responseAux = "";
            //Authorization =? token
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        ArrayList pathsList = getPathsFromResponse(response);
                        Log.i("PATHLIST", pathsList.toString());
                        //responseAux = response;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RESPONSE","Response is:NULL");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                Log.i("TOKEN", userToken);
                params.put("Authorization",userToken);
                return params;
            }
        };

        queue.add(stringRequest);
        return responseAux;
    }

    public ArrayList<String> getPathsFromResponse(String response) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(response).getAsJsonObject();
            ArrayList<String> arrayList = new ArrayList<>();
            JsonArray jsonArray = (JsonArray) json.get("filekeys");
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement jsonObject = (JsonElement) jsonArray.get(i);
                Log.i("RESPONSE", "Response is json: " + jsonObject.getAsJsonObject().get("path"));
                arrayList.add(jsonObject.getAsJsonObject().get("path").toString());
            }
            return arrayList;
        } catch (Exception e) {
            return null;
        }
    }
}
