package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Lesson;

/**
 * Created by ESTEBANFML on 20-10-2017.
 */

public class DeleteLessonTable  extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    public DeleteLessonTable(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().nukeTable();

        return true;
    }

}
