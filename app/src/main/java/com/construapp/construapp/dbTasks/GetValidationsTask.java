package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.models.Lesson;

import java.util.List;

/**
 * Created by jose on 22-10-17.
 */

public class GetValidationsTask extends AsyncTask <Void,Void,List<Lesson>>{

    private Context context;
    private String projectId;
    private List<Lesson> list;

    public GetValidationsTask(Context context, String projectId)
    {
        this.context=context;
        this.projectId=projectId;

    }

    @Override
    protected List<Lesson> doInBackground(Void... params) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context.getApplicationContext());

        if (!projectId.equals("null")) {
            Log.i("VALIDATION", "BYPROJECT");
            list = appDatabase.lessonDAO().getLessonByProjectIdAndValidator(projectId,"true");
        }

        else {
            Log.i("VALIDATION", "BY VALIDATION");
            list = appDatabase.lessonDAO().getLessonByValidator("true");
        }
        return  list;
    }
}
