package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.models.Lesson;

/**
 * Created by jose on 18-10-17.
 */

public class DeleteLessonTask extends AsyncTask<Void,Void,Boolean> {

    private Lesson lesson;
    private Context context;
    public DeleteLessonTask(Lesson lesson,Context context)
    {
        this.lesson=lesson;
        this.context=context;

    }
    @Override
    protected Boolean doInBackground(Void... params) {

        AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().deleteLesson(lesson);

        return true;
    }
}
