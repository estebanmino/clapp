package com.construapp.construapp.lessons;

/**
 * Created by ESTEBANFML on 12-11-2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Comment;

import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Comment> commentList;

    private TextView commentText;
    private TextView commentFullName;
    private TextView commentPosition;
    private TextView timestamp;

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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.thread_comments_list_item,null);
        }

        commentText = convertView.findViewById(R.id.textview_text);
        commentFullName = convertView.findViewById(R.id.textview_fullname);
        commentPosition = convertView.findViewById(R.id.textview_position);
        timestamp = convertView.findViewById(R.id.textview_post_timestamp);
        timestamp.setVisibility(View.GONE);

        final String postComment = commentList.get(position).getText();
        final String fullname = commentList.get(position).getFirst_name()+ " " + commentList.get(position).getLast_name();
        final String postposition = commentList.get(position).getPosition();

        commentText.setText(postComment);
        commentFullName.setText(fullname);
        commentPosition.setText(postposition);

        return convertView;
    }

}
