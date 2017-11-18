package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.db.CommentDatabase;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Lesson;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

public class InsertCommentTask  extends AsyncTask<Void,Void,Boolean> {

    private Comment comment;
    private Context context;

    public InsertCommentTask(Comment comment,Context context)
    {
        this.comment=comment;
        this.context=context;
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        CommentDatabase.getDatabase(context.getApplicationContext()).commentDAO().insertComment(comment);
        return true;
    }

}
