package com.construapp.construapp.lessons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;

import java.util.ArrayList;


public class LessonViewFragment extends Fragment {

    private String lessonName;
    private String lessonSummary;
    private String lessonMotivation;
    private String lessonLearning;
    private ArrayList<String> arrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        arrayList = getArguments().getStringArrayList(Constants.B_LESSON_ARRAY_LIST);

        lessonName = arrayList.get(0);
        lessonSummary = arrayList.get(1);
        lessonLearning = arrayList.get(2);
        lessonMotivation = arrayList.get(3);
        return inflater.inflate(R.layout.fragment_lesson_view,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textLessonName = view.findViewById(R.id.text_lesson_name);
        TextView textLessonDescription = view.findViewById(R.id.text_lesson_summary);
        TextView textLessonMotivation = view.findViewById(R.id.text_lesson_motivation);
        TextView textLessonLearning = view.findViewById(R.id.text_lesson_learning);

        textLessonName.setText(lessonName);
        textLessonDescription.setText(lessonSummary);
        textLessonMotivation.setText(lessonMotivation);
        textLessonLearning.setText(lessonLearning);
    }
}
