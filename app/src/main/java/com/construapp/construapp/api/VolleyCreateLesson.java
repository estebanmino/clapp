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
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ESTEBANFML on 15-10-2017.
 */

public class VolleyCreateLesson extends AsyncTask<Void,Void,Boolean> {

    private VolleyJSONCallback callback;
    private Context context;
    private String lesson_name;
    private String lesson_summary;
    private String project_id;
    private String lesson_motivation;
    private String lesson_learning;
    private String validation;
    private String tagsString;
    private ArrayList<String> disciplinesString;
    private ArrayList<String> classificationsString;
    private ArrayList<String> departmentsString;
    private Lesson lesson;

    public VolleyCreateLesson(VolleyJSONCallback callback,
                              Context context, String lesson_name, String lesson_summary,
                              String lesson_motivation, String lesson_learning,
                              String project_id, String validation, String tagsString,
                              ArrayList<String> disciplinesString, ArrayList<String> classificationsString,
                              ArrayList<String> departmentsString,Lesson lesson) {


        this.callback = callback;
        this.context = context;
        this.lesson_name = lesson_name;
        this.project_id = project_id;
        this.lesson_summary = lesson_summary;
        this.lesson_motivation = lesson_motivation;
        this.lesson_learning = lesson_learning;
        this.validation = validation;
        this.tagsString = tagsString;
        this.disciplinesString = disciplinesString;
        this.classificationsString = classificationsString;
        this.departmentsString = departmentsString;
        this.lesson = lesson;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean sent = false;

        while (!sent) {
            if (Connectivity.isConnected(context)) {

                JsonArray tagsArray = new JsonArray();
                JsonArray disciplinesArray = new JsonArray();
                JsonArray classificationArray = new JsonArray();
                JsonArray departmentsArray = new JsonArray();

                String[] tagsStringArray = tagsString.split(" ");
                for(int i=0;i<tagsStringArray.length;i++)
                {
                    if (!tagsStringArray[i].isEmpty()) tagsArray.add(new JsonPrimitive(tagsStringArray[i]));
                }
                for(int i=0;i<disciplinesString.size();i++)
                {
                    if (!disciplinesString.get(i).isEmpty())  disciplinesArray.add(new JsonPrimitive(disciplinesString.get(i)));
                }
                for(int i=0;i<classificationsString.size();i++)
                {
                    if (!classificationsString.get(i).isEmpty())  classificationArray.add(new JsonPrimitive(classificationsString.get(i)));
                }
                for(int i=0;i<departmentsString.size();i++)
                {
                    if (!departmentsString.get(i).isEmpty()) departmentsArray.add(new JsonPrimitive(departmentsString.get(i)));
                }

                sent = true;
                SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
                String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");
                String user_id = sharedpreferences.getString(Constants.SP_USER, "");
                final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");

                String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + company_id + "/" + Constants.LESSONS;

                final RequestQueue queue = Volley.newRequestQueue(context);
                JsonObject jsonLessonBody = new JsonObject();

                JSONObject jsonObject = new JSONObject();
                final JsonObject jsonLesson = new JsonObject();

                jsonLessonBody.addProperty("name",lesson_name);
                jsonLessonBody.addProperty("summary",lesson_summary);
                jsonLessonBody.addProperty("motivation",lesson_motivation);
                jsonLessonBody.addProperty("learning",lesson_learning);
                jsonLessonBody.addProperty("validation",validation);
                jsonLessonBody.addProperty("user_id",user_id);
                jsonLessonBody.addProperty("company_id",company_id);
                jsonLessonBody.addProperty("project_id",project_id);
                jsonLessonBody.addProperty("trigger_id",lesson.getTrigger_id());

                //jsonTags.addProperty("tags_list",tags);

                jsonLesson.add("lesson", jsonLessonBody);
                jsonLesson.add("tags_list", tagsArray);
                jsonLesson.add("disciplines_list", disciplinesArray);
                jsonLesson.add("classifications_list", classificationArray);
                jsonLesson.add("departments_list", departmentsArray);

                Log.i("CREATELESSON",jsonLesson.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("RESPONSE", response.toString());
                                callback.onSuccess(response);
                                Log.i("TOKEN", userToken);


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RESPONSE", "Response is:"+error.getMessage());
                        Log.i("TOKEN", userToken);
                        Log.i("JSON", jsonLesson.toString());
                        callback.onErrorResponse(error);
                    }

                }) {
                    @Override
                    public String getBodyContentType() {
                        return Constants.Q_CONTENTTYPE_JSON_UTF8;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(Constants.Q_CONTENTTYPE, Constants.Q_CONTENTTYPE_JSON);
                        params.put(Constants.Q_AUTHORIZATION, userToken);
                        return params;
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return jsonLesson == null ? null : jsonLesson.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonLesson, "utf-8");
                            return null;
                        }
                    }
                };
                queue.add(jsonObjectRequest);


            } else {
                try {
                    Log.i("CREATELESSON","offline");
                    Thread.sleep(10000);
                } catch (Exception e) {
                }

            }
        }
        return null;
    }
    }

    /*public static void volleyCreateLesson(final VolleyJSONCallback callback,
                                          Context context, String lesson_name, String lesson_summary,
                                          String lesson_motivation, String lesson_learning,
                                          String project_id, String validation) {


    }*/

