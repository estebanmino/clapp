package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.construapp.construapp.db.CommentDatabase;
import com.construapp.construapp.models.Comment;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

public class DeleteLessonCommentTask  extends AsyncTask<Void,Void,Boolean> {

    private Comment comment;
    private Context context;

    public DeleteLessonCommentTask(Comment comment,Context context)
    {
        this.comment=comment;
        this.context=context;
    }
    @Override
    protected Boolean doInBackground(Void... params) {

        CommentDatabase.getDatabase(context.getApplicationContext()).commentDAO().deleteComment(comment);
        return true;
    }

}

