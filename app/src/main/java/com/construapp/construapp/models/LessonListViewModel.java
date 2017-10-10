package com.construapp.construapp.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jose on 10-10-17.
 */

public class LessonListViewModel extends AndroidViewModel{
    private final ArrayList<Lesson> LessonList;

    private AppDatabase appDatabase;

    public LessonListViewModel (Application application){
        super(application);

        //obtenemos una instancia de nuestra db
        appDatabase = AppDatabase.getDatabase(this.getApplication());

        //query para obtener todas las lecciones definida en nuestra clase DAO
        LessonList = appDatabase.LessonModel().getAllLessons();
    }

    public ArrayList<Lesson> getLessonList(){
        return LessonList;
    }

    public void deleteLesson(Lesson lessonModel){
        new deleteAsyncTask(appDatabase).execute(lessonModel);
    }

    private static class deleteAsyncTask extends AsyncTask<Lesson,Void, Void>{

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Lesson... params){
            db.LessonModel().deleteLesson(params[0]);
            return null;
        }
    }
}
