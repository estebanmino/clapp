package com.construapp.construapp.threading;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Lesson;

/**
 * Created by jose on 18-10-17.
 */

public class DeleteLessonTask extends AsyncTask<Void,Void,Boolean> {

    private String name;
    private String summary;
    private String id;
    private Context context;
    public DeleteLessonTask(String name,String summary,String id,Context context)
    {
        this.name=name;
        this.summary=summary;
        this.id=id;
        this.context=context;

    }
    @Override
    protected Boolean doInBackground(Void... params) {

        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.setDescription(summary);
        lesson.setId(id);

        AppDatabase.getDatabase(context.getApplicationContext()).lessonDAO().insertLesson(lesson);

        return true;
    }
}
