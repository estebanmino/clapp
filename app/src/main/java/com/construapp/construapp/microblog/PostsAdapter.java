package com.construapp.construapp.microblog;

/**
 * Created by jose on 06-11-17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Post;
import com.construapp.construapp.models.Section;

import java.util.List;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Section;
import com.construapp.construapp.models.Threadblog;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by JOSE on 18-09-2017.
 */

public class PostsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Post> postModelList;

    private TextView postText;
    private TextView postTimestamp;
    private TextView postFullname;
    private TextView postPosition;

    public PostsAdapter(Context context, List<Post> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    @Override
    public int getCount() {
        if(postModelList != null) {
            return postModelList.size();
        }

        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return postModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //firebaseFirebase.init();

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.thread_comments_list_item,null);
        }

        postText = convertView.findViewById(R.id.textview_text);
        postTimestamp = convertView.findViewById(R.id.textview_post_timestamp);
        postFullname = convertView.findViewById(R.id.textview_fullname);
        postPosition = convertView.findViewById(R.id.textview_position);


        final String postComment = postModelList.get(position).getText();
        final String fullname = postModelList.get(position).getFirst_name()+ " " + postModelList.get(position).getLast_name();
        final String postposition = postModelList.get(position).getPosition();
        final String timestamp = postModelList.get(position).getTimestamp();


        postText.setText(postComment);
        postFullname.setText(fullname);
        postPosition.setText(postposition);
        postTimestamp.setText(timestamp);



        return convertView;
    }

}
