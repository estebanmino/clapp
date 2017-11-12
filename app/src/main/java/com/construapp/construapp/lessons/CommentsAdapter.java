package com.construapp.construapp.lessons;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeleteLessonComment;
import com.construapp.construapp.dbTasks.DeleteLessonCommentTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.SessionManager;

import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Comment> commentList;

    private TextView commentText;
    private TextView commentFullName;
    private TextView commentPosition;
    private FloatingActionButton fabDeleteComment;

    public CommentsAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        if(commentList != null) {
            return commentList.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.comment_list_item,null);
        }

        fabDeleteComment = convertView.findViewById(R.id.fab_delete_comment);
        commentText = convertView.findViewById(R.id.textview_text);
        commentFullName = convertView.findViewById(R.id.textview_fullname);
        commentPosition = convertView.findViewById(R.id.textview_position);

        final String postComment = commentList.get(position).getText();
        final String fullname = commentList.get(position).getFirst_name()+ " " + commentList.get(position).getLast_name();
        final String postposition = commentList.get(position).getPosition();

        SessionManager sessionManager =  new SessionManager(context);

        if (sessionManager.getUserId().equals(commentList.get(position).getAuthorId()) ||
                sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)) {
            fabDeleteComment.setVisibility(View.VISIBLE);
            fabDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VolleyDeleteLessonComment.volleyDeleteLessonComment(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                new DeleteLessonCommentTask(commentList.get(position), context).execute().get();
                                commentList.remove(position);
                                notifyDataSetChanged();
                            } catch (Exception e) {}
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {
                            Log.i("DELETERESULLT", result.toString());
                        }
                    },context, commentList.get(position).getLessonId(),  commentList.get(position).getId());
                }
            });
        }

        commentText.setText(postComment);
        commentFullName.setText(fullname);
        commentPosition.setText(postposition);

        return convertView;
    }

}
