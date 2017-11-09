package com.construapp.construapp.main;

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

public class ThreadsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Threadblog> threadModelList;

    private TextView threadTitle;
    private TextView threadText;

    public ThreadsAdapter(Context context, List<Threadblog> threadModelList) {
        this.context = context;
        this.threadModelList = threadModelList;
    }

    @Override
    public int getCount() {
        if(threadModelList != null) {
            return threadModelList.size();
        }

        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return threadModelList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.thread_list_item,null);
        }

        threadTitle = convertView.findViewById(R.id.textview_thread_title);
        threadText = convertView.findViewById(R.id.textview_thread_text);


        final String elementTitle = threadModelList.get(position).getTitle();
        final String elementText = threadModelList.get(position).getText();


        threadTitle.setText(elementTitle);
        threadText.setText(elementText);



        return convertView;
    }

}
