package com.construapp.construapp.dbTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.db.CommentDatabase;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Lesson;

import java.util.List;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

public class GetCommentsTask extends AsyncTask<Void,Void,List<Comment>>{

    private Context context;
    private String userId;
    private List<Comment> list;

    public GetCommentsTask(Context context, String userId)
    {
        this.context=context;
        this.userId=userId;
    }
    @Override
    protected List<Comment> doInBackground(Void... params) {
        CommentDatabase commentDatabase = CommentDatabase.getDatabase(context.getApplicationContext());

        list = commentDatabase.commentDAO().getAllLessonComments(userId);

        return  list;
    }
}
