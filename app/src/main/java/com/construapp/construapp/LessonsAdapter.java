package com.construapp.construapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 18-09-2017.
 */

public class LessonsAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Lesson> LessonModelList;

    private TextView lessonName;
    private TextView lessonDescription;

    public LessonsAdapter(Context context, ArrayList<Lesson> LessonModelList) {
        this.context = context;
        this.LessonModelList = LessonModelList;
    }

    @Override
    public int getCount() {
        return LessonModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return LessonModelList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.lesson_list_item,null);
        }

        lessonName = convertView.findViewById(R.id.lesson_name);
        lessonDescription = convertView.findViewById(R.id.lesson_description);

        final String elementName = LessonModelList.get(position).getName();
        final String elementDescription = LessonModelList.get(position).getDescription();

        lessonName.setText(elementName);
        lessonDescription.setText(elementDescription.substring(0,Math.min(elementDescription.length(), 20))+"...");

        return convertView;
    }

}
