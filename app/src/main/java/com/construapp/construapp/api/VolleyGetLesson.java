package com.construapp.construapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by ESTEBANFML on 27-11-2017.
 */

public class VolleyGetLesson {

    public static void volleyGetLesson(final VolleyJSONCallback callback,
                                        final Context context, String lesson_id) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        final String userToken = sharedpreferences.getString(Constants.SP_TOKEN, "");
        final String companyId = sharedpreferences.getString(Constants.SP_COMPANY, "");

        final String url = Constants.BASE_URL + "/" + Constants.COMPANIES + "/" + companyId + "/" + Constants.LESSONS + "/" + lesson_id;

        final RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject  object) {
                        Lesson lesson = new Lesson();
                        try {
                            lesson.setName(object.get("name").toString());
                            lesson.setSummary(object.get("summary").toString());
                            lesson.setId(object.get("id").toString());
                            lesson.setMotivation(object.get("motivation").toString());
                            lesson.setLearning(object.get("learning").toString());
                            lesson.setValidation(object.get("validation").toString());
                            JSONObject objectUser = (JSONObject) object.get("user");
                            lesson.setAuthor_id(objectUser.get("id").toString());
                            lesson.setAuthor_admin(objectUser.get("admin").toString());
                            lesson.setAuthor_email(objectUser.get("email").toString());
                            lesson.setAuthor_first_name(objectUser.get("first_name").toString());
                            lesson.setAuthor_last_name(objectUser.get("last_name").toString());
                            lesson.setAuthor_position(objectUser.get("position").toString());
                            lesson.setProject_id(object.get("project_id").toString());
                            lesson.setCompany_id(object.get("company_id").toString());
                            lesson.setTrigger_id((int) object.get("trigger_id"));
                            lesson.setReject_comment(object.get("reject_comment").toString());

                            String classificationsStringArray = "";
                            JSONArray jsonClassifications = (JSONArray) object.get(Constants.SP_CLASSIFICATIONS);
                            for (int j = 0; j < jsonClassifications.length(); j++) {
                                JSONObject jsonObject = (JSONObject) jsonClassifications.get(j);
                                classificationsStringArray += "/"+jsonObject.get("name");
                            }
                            String disciplinesStringArray = "";
                            JSONArray jsonDisciplines = (JSONArray) object.get(Constants.SP_DISCIPLINES);
                            for (int j = 0; j < jsonDisciplines.length(); j++) {
                                JSONObject jsonObject = (JSONObject) jsonDisciplines.get(j);
                                disciplinesStringArray += "/"+jsonObject.get("name");
                            }
                            String departmentsStringArray = "";
                            JSONArray jsonDepartments = (JSONArray) object.get(Constants.SP_DEPARTMENTS);
                            for (int j = 0; j < jsonDepartments.length(); j++) {
                                JSONObject jsonObject = (JSONObject) jsonDepartments.get(j);
                                departmentsStringArray += "/"+jsonObject.get("name");
                            }

                            String tagsStringArray = "";
                            JSONArray jsonTags = (JSONArray) object.get(Constants.SP_TAGS);
                            for (int j = 0; j < jsonTags.length(); j++) {
                                JSONObject jsonObject = (JSONObject) jsonTags.get(j);
                                tagsStringArray += "/"+jsonObject.get("name");
                            }
                            JSONArray jsonComments;
                            try {
                                jsonComments = new JSONArray(object.get("comments").toString());
                                for (int j = 0; j < jsonComments.length(); j++) {
                                    try {
                                        Comment comment = new Comment();
                                        JSONObject jsonComment = (JSONObject) jsonComments.get(j);
                                        comment.setId(Integer.parseInt(jsonComment.get("id").toString()));
                                        comment.setText(jsonComment.get("text").toString());
                                        JSONObject jsonObject1 = (JSONObject) jsonComment.get("user");
                                        comment.setFirst_name(jsonObject1.get("first_name").toString());
                                        comment.setLast_name(jsonObject1.get("last_name").toString());
                                        comment.setAuthorId(jsonObject1.get("id").toString());
                                        comment.setPosition(jsonObject1.get("position").toString());
                                        comment.setLessonId(lesson.getId());
                                        new InsertCommentTask(comment, context).execute().get();
                                    } catch (Exception e) {
                                        Log.i("EXCEPTIONS", e.toString());
                                    }
                                }
                            } catch (Exception e) {
                                Log.i("GETLESSONSEXCEPTION",e.toString());
                            }

                            lesson.setClassifications(classificationsStringArray);
                            lesson.setDisciplines(disciplinesStringArray);
                            lesson.setDepartments(departmentsStringArray);
                            lesson.setTags(tagsStringArray);


                            try {
                                new InsertLessonTask(lesson, context).execute().get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            Log.i("GETLESSONSEXCEPTION",e.toString());
                        }
                        callback.onSuccess(object);
                    }
                },
                new Response.ErrorListener() {
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
        };
        queue.add(jsonObjectRequest);
    }


}
