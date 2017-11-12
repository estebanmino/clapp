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
    private String lessonId;
    private List<Comment> list;

    public GetCommentsTask(Context context, String lessonId)
    {
        this.context=context;
        this.lessonId=lessonId;
    }
    @Override
    protected List<Comment> doInBackground(Void... params) {
        CommentDatabase commentDatabase = CommentDatabase.getDatabase(context.getApplicationContext());

        list = commentDatabase.commentDAO().getAllLessonComments(lessonId);

        return  list;
    }
}
