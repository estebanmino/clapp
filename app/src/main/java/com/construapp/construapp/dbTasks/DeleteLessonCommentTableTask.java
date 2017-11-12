package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.db.CommentDatabase;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

public class DeleteLessonCommentTableTask extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    public DeleteLessonCommentTableTask(Context context)
    {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        CommentDatabase.getDatabase(context.getApplicationContext()).commentDAO().nukeTable();

        return true;
    }

}