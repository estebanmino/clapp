package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.models.Lesson;

import java.util.List;

/**
 * Created by jose on 17-10-17.
 */

public class GetLessonsTask extends AsyncTask<Void,Void,List<Lesson>>{

    private Context context;
    private String projectId;
    private String userId;
    private String validation;
    private List<Lesson> list;

    public GetLessonsTask(Context context, String projectId, String userId, String validation)
    {
        this.context=context;
        this.projectId=projectId;
        this.userId=userId;
        this.validation = validation;
    }
    @Override
    protected List<Lesson> doInBackground(Void... params) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context.getApplicationContext());
        if  (projectId.equals("null") && userId.equals("null")) {
            Log.i("JSON", "byall");

            list = AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getAllLessons();
        }
        else if (!projectId.equals("null") && userId.equals("null")) {
            Log.i("JSON", "byprojectid");
            list = AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getLessonByProjectId(projectId);
        }
        else if (projectId.equals("null") && !userId.equals("null")) {
            Log.i("JSON", "byuserid");

            list = AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getLessonByUserId(userId);
        }
        else {
            Log.i("JSON", "byuserprojectid");
            list = AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getLessonByUserProjectIdAndValidation(userId,projectId,validation);
        }
        return  list;
    }
}
