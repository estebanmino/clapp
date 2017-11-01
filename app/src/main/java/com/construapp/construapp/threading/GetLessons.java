package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Lesson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by ESTEBANFML on 21-10-2017.
 */

public class GetLessons extends AsyncTask {

    public Context context;

    public GetLessons (Context context) {
        this.context =  context;
    }

    private JSONArray jsonLessons;

    @Override
    protected Object doInBackground(Object[] objects) {
        boolean is_connected = Connectivity.isConnected(context);
        final Lesson lesson = new Lesson();
        if(is_connected) {

            VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        jsonLessons = new JSONArray(result);
                        for (int i = 0; i  < jsonLessons.length(); i++) {
                            Log.i("JSON",jsonLessons.get(i).toString());
                            JSONObject object = (JSONObject) jsonLessons.get(i);
                            lesson.setName(object.get("name").toString());
                            lesson.setDescription(object.get("summary").toString());
                            lesson.setId(object.get("id").toString());
                            //lesson.setDescription(learning);
                            lesson.setMotivation(object.get("motivation").toString());
                            lesson.setLearning(object.get("learning").toString());
                            lesson.setValidation(object.get("validation").toString());
                            lesson.setUser_id(object.get("user_id").toString());
                            lesson.setProject_id(object.get("project_id").toString());
                            lesson.setCompany_id(object.get("company_id").toString());
                            try {
                                new InsertLessonTask(lesson, context).execute().get();
                                //databaseThread.addLesson(getActivity(),name,summary,id);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                    } catch (Exception e) {}
                }

                @Override
                public void onErrorResponse(VolleyError result) {

                }
            }, context);
        }
        return false;
    }
}
