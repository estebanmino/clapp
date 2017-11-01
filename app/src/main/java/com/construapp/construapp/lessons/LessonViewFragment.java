package com.construapp.construapp.lessons;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;


public class LessonViewFragment extends Fragment {

    private String lessonName;
    private String lessonDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lessonName = getArguments().getString(Constants.B_LESSON_NAME);
        lessonDescription = getArguments().getString(Constants.B_LESSON_DESCRIPTION);
        return inflater.inflate(R.layout.fragment_lesson_view,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textLessonName = view.findViewById(R.id.text_lesson_name);
        TextView textLessonDescription = view.findViewById(R.id.text_lesson_description);

        textLessonName.setText(lessonName);
        textLessonDescription.setText(lessonDescription);

    }
}
