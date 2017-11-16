package com.construapp.construapp.lessons;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 14-11-2017.
 */

public class LessonAttributesAdapter extends RecyclerView.Adapter<LessonAttributesAdapter.AttributeViewHolder> {

    private String[] attributesList;
    private Context context;
    private Lesson thisLesson;
    private ArrayList<String> selectedAttributes;
    private Boolean editing;
    private String tagType;

    public LessonAttributesAdapter(String[] attributesList, Context context, Lesson thisLesson, Boolean editing, String tagType) {
        this.attributesList = attributesList;
        this.context = context;
        this.thisLesson = thisLesson;
        this.selectedAttributes = new ArrayList<>();
        this.editing = editing;
        this.tagType = tagType;
    }

    @Override
    public int getItemCount() {
        return attributesList.length;
    }

    public ArrayList<String> getSelectedAttributes() {
        return selectedAttributes;
    }

    @Override
    public void onBindViewHolder(LessonAttributesAdapter.AttributeViewHolder holder, int position) {
        holder.textAttribute.setText(attributesList[position]);
        if (tagType.equals(Constants.TAG_CLASSIFICATIONS)) {
            if (thisLesson.getClassifications().contains(attributesList[position])){
                holder.textAttribute.setBackgroundColor(Color.parseColor("#f7772f"));
                holder.textAttribute.setTag("true");
                selectedAttributes.add(holder.textAttribute.getText().toString());
            }
        } else if (tagType.equals(Constants.TAG_DISCIPLINES)) {
            if (thisLesson.getDisciplines().contains(attributesList[position])){
                holder.textAttribute.setBackgroundColor(Color.parseColor("#f7772f"));
                holder.textAttribute.setTag("true");
                selectedAttributes.add(holder.textAttribute.getText().toString());
            }
        } else if (tagType.equals(Constants.TAG_DEPARTMENTS)) {
            if (thisLesson.getDepartments().contains(attributesList[position])){
                holder.textAttribute.setBackgroundColor(Color.parseColor("#f7772f"));
                holder.textAttribute.setTag("true");
                selectedAttributes.add(holder.textAttribute.getText().toString());
            }
        }
    }

    @Override
    public AttributeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.attribute_list_item, parent, false);
        AttributeViewHolder  vh = new AttributeViewHolder(view);
        return vh;
    }

    public class AttributeViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView textAttribute;

        public AttributeViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            textAttribute = view.findViewById(R.id.text_attribute);
        }

        @Override
        public void onClick(View view) {
            if ((context.getClass() == LessonActivity.class && !((LessonActivity) context).getEditing()) ||
                    context.getClass() == LessonValidationActivity.class) {

            } else {

                if (view.getTag().equals("false")) {
                    view.setBackgroundColor(Color.parseColor("#f7772f"));
                    view.setTag("true");
                    selectedAttributes.add(((TextView) view).getText().toString());
                    for (String str : selectedAttributes) {
                        Log.i("sleectedattributes", str);
                    }
                } else {
                    view.setBackgroundColor(Color.parseColor("#161542"));
                    view.setTag("false");
                    int pos = selectedAttributes.indexOf(((TextView) view).getText().toString());
                    selectedAttributes.remove(pos);
                    for (String str : selectedAttributes) {
                        Log.i("sleectedattributes", str);
                    }
                }
            }
        }

    }
}

