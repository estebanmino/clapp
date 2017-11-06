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


/**
 * Created by JOSE on 18-09-2017.
 */

public class SectionsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Lesson> LessonModelList;

    private TextView lessonName;
    private TextView lessonDescription;
    private TextView lessonStatus;

    public LessonsAdapter(Context context, List<Lesson> LessonModelList) {
        this.context = context;
        this.LessonModelList = LessonModelList;
    }

    @Override
    public int getCount() {
        if(LessonModelList != null) {
            return LessonModelList.size();
        }

        else {
            return 0;
        }
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
            convertView = layoutInflater.inflate(R.layout.section_list_item,null);
        }

        sectionName = convertView.findViewById(R.id.lesson_name);
        lessonDescription = convertView.findViewById(R.id.lesson_description);
        lessonStatus = convertView.findViewById(R.id.lesson_status);

        final String elementName = LessonModelList.get(position).getName();
        final String elementDescription = LessonModelList.get(position).getSummary();

        lessonName.setText(elementName);
        lessonDescription.setText(elementDescription);

        if(LessonModelList.get(position).getValidation().equals("-1"))
        {
            lessonStatus.setText("Rechazada");
        }
        else if (LessonModelList.get(position).getValidation().equals("0"))
        {
            lessonStatus.setText("En Espera");
        }

        return convertView;
    }

}
