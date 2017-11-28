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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeleteLessonComment;
import com.construapp.construapp.api.VolleyPutLesson;
import com.construapp.construapp.api.VolleyPutLessonComment;
import com.construapp.construapp.dbTasks.DeleteLessonCommentTask;
import com.construapp.construapp.dbTasks.GetCommentsTask;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.SessionManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Comment> commentList;

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
        final TextView commentText;
        final TextView commentFullName;
        final TextView commentPosition;
        final Button buttonDeleteComment;
        final Button buttonEditComment;
        final Button buttonConfirmEdition;
        final Button buttonCancelEdition;
        final EditText editComment;
        final LinearLayout linearEdition;
        final LinearLayout linearSendEdition;

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.comment_list_item,null);
        }

        buttonDeleteComment = convertView.findViewById(R.id.btn_delete_comment);
        buttonEditComment = convertView.findViewById(R.id.btn_edit_comment);
        buttonCancelEdition = convertView.findViewById(R.id.btn_cancel_edition);
        buttonConfirmEdition = convertView.findViewById(R.id.btn_confirm_edition);
        commentText = convertView.findViewById(R.id.textview_text);
        commentFullName = convertView.findViewById(R.id.textview_fullname);
        commentPosition = convertView.findViewById(R.id.textview_position);
        editComment = convertView.findViewById(R.id.edit_comment);
        linearEdition = convertView.findViewById(R.id.linear_edition);
        linearSendEdition = convertView.findViewById(R.id.linear_send_edition);

        final String postComment = commentList.get(position).getText().replace("\"","");
        final String fullname = commentList.get(position).getFirst_name().replace("\"","")+ " "
                + commentList.get(position).getLast_name().replace("\"","");
        final String postposition = commentList.get(position).getPosition().replace("\"","");

        SessionManager sessionManager =  new SessionManager(context);

        if (sessionManager.getUserId().equals(commentList.get(position).getAuthorId()) ||
                sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)) {
            linearEdition.setVisibility(View.VISIBLE);
            buttonDeleteComment.setOnClickListener(new View.OnClickListener() {
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
                    },context, commentList.get(position).getLessonId(),  String.valueOf(commentList.get(position).getId()));
                }
            });

            buttonEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearSendEdition.setVisibility(View.VISIBLE);
                    editComment.setVisibility(View.VISIBLE);
                    editComment.setText(commentText.getText().toString());
                    commentText.setVisibility(View.GONE);
                    linearSendEdition.setVisibility(View.VISIBLE);
                    linearEdition.setVisibility(View.GONE);
                }
            });

            buttonCancelEdition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editComment.setVisibility(View.GONE);
                    commentText.setVisibility(View.VISIBLE);
                    linearSendEdition.setVisibility(View.GONE);
                    linearEdition.setVisibility(View.VISIBLE);
                }
            });

            buttonConfirmEdition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VolleyPutLessonComment.volleyPutLessonComment(new VolleyJSONCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Comment comment = new Comment();
                            try {
                                comment.setId(Integer.parseInt(result.get("id").toString()));
                                comment.setText(result.get("text").toString());
                                JSONObject jsonObject1 = (JSONObject) result.get("user");
                                comment.setFirst_name(jsonObject1.get("first_name").toString());
                                comment.setLast_name(jsonObject1.get("last_name").toString());
                                comment.setAuthorId(jsonObject1.get("id").toString());
                                comment.setPosition(jsonObject1.get("position").toString());
                                comment.setLessonId(commentList.get(position).getLessonId());
                                new InsertCommentTask(comment, context).execute().get();
                                commentList.get(position).setText(comment.getText());
                                notifyDataSetChanged();
                                editComment.setVisibility(View.GONE);
                                commentText.setVisibility(View.VISIBLE);
                                linearSendEdition.setVisibility(View.GONE);
                                linearEdition.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                Log.i("EXCEPTIONS", e.toString());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {
                            Log.i("EDITION", result.toString());

                        }
                    },context, commentList.get(position).getLessonId(), String.valueOf(commentList.get(position).getId()),
                            editComment.getText().toString());
                }
            });

        }
        commentText.setText(postComment);
        commentFullName.setText(fullname);
        commentPosition.setText(postposition);
        return convertView;
    }

}
