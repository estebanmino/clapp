package com.construapp.construapp.lessons;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LessonEditFragment extends Fragment {

    private String lessonName;
    private String lessonSummary;
    private String lessonMotivation;
    private String lessonLearning;
    private ArrayList<String> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        arrayList = getArguments().getStringArrayList(Constants.B_LESSON_ARRAY_LIST);

        lessonName = arrayList.get(0);
        lessonSummary = arrayList.get(1);
        lessonLearning = arrayList.get(2);
        lessonMotivation = arrayList.get(3);
        return inflater.inflate(R.layout.fragment_lesson_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editLessonName = view.findViewById(R.id.new_section_name);
        EditText editLessonSummary = view.findViewById(R.id.edit_lesson_summary);
        EditText editLessonMotivation = view.findViewById(R.id.edit_lesson_motivation);
        EditText editLessonLearning = view.findViewById(R.id.edit_lesson_learning);

        editLessonName.setText(lessonName);
        editLessonSummary.setText(lessonSummary);
        editLessonMotivation.setText(lessonMotivation);
        editLessonLearning.setText(lessonLearning);
    }
}
