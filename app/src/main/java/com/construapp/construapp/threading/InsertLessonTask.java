package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Lesson;

import java.util.concurrent.ExecutionException;

/**
 * Created by jose on 17-10-17.
 */

public class InsertLessonTask extends AsyncTask<Void,Void,Boolean> {

    private Lesson lesson;
    private Context context;

    public InsertLessonTask(Lesson lesson,Context context)
    {
        this.lesson=lesson;

    }
    @Override
    protected Boolean doInBackground(Void... params) {

                    AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().insertLesson(lesson);

                    return true;
                }

}
