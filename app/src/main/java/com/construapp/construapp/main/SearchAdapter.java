package com.construapp.construapp.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    private final Context context;

    private TextView lessonName;
    private TextView lessonDescription;

    private LayoutInflater layoutInflater;
    private final List<Lesson> listItemStorage;
    public SearchAdapter(Context context, List<Lesson> customizedListView) {
        this.context=context;
        //this.layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listItemStorage = customizedListView;
    }
    @Override
    public int getCount() {

        if(listItemStorage != null) {
            return listItemStorage.size();
        }

        else {
            return 0;
        }
    }
    @Override
    public Object getItem(int position) {
        return listItemStorage.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lesson_list_item, parent, false);

        }
        lessonName = convertView.findViewById(R.id.lesson_name);
        lessonDescription = convertView.findViewById(R.id.lesson_description);

        final String elementName = listItemStorage.get(position).getName();
        final String elementDescription = listItemStorage.get(position).getSummary();

        lessonName.setText(elementName);
        lessonDescription.setText(elementDescription);
        return convertView;
    }
}