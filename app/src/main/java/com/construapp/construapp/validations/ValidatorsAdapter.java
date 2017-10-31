package com.construapp.construapp.validations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ESTEBANFML on 31-10-2017.
 */

public class ValidatorsAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> validatorsList;

    private TextView lessonName;
    private TextView lessonDescription;
    private TextView lessonStatus;

    public ValidatorsAdapter(Context context, ArrayList<String> validatorsList) {
        this.context = context;
        this.validatorsList = validatorsList;
    }

    @Override
    public int getCount() {
        if(validatorsList != null) {
            return validatorsList.size();
        }

        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return validatorsList.get(position);
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
        lessonName = convertView.findViewById(R.id.lesson_name);
        lessonDescription = convertView.findViewById(R.id.lesson_description);
        lessonStatus = convertView.findViewById(R.id.lesson_status);

        final String elementName = validatorsList.get(position);
        final String elementDescription = "";

        lessonName.setText(elementName);
        lessonDescription.setText(elementDescription);
        lessonStatus.setText("En Espera");

        return convertView;
    }

}
