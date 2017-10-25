package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.models.Lesson;

import java.util.List;

/**
 * Created by ESTEBANFML on 25-10-2017.
 */

public class GetLessonTask extends AsyncTask<Void,Void,Lesson> {


    private Context context;
    private String projectId;
    private String lessonId;
    private Lesson lesson;

    public GetLessonTask(Context context, String lessonId)
    {
        this.context=context;
        this.lessonId=lessonId;
    }
    @Override
    protected Lesson doInBackground(Void... params) {
        return  AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getLessonById(lessonId);
    }
}
