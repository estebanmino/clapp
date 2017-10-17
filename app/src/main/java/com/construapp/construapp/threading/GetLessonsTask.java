package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Lesson;

import java.util.List;

/**
 * Created by jose on 17-10-17.
 */

public class GetLessonsTask extends AsyncTask<Void,Void,List<Lesson>>{

    private Context context;
    public GetLessonsTask(Context context)
    {
        this.context=context;
    }
    @Override
    protected List<Lesson> doInBackground(Void... params) {
        return AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().getAllLessons();
    }
}
