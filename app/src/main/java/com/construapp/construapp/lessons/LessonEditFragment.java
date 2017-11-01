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


/**
 * A simple {@link Fragment} subclass.
 */
public class LessonEditFragment extends Fragment {

    String lessonName;
    String lessonDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lessonName = getArguments().getString(Constants.B_LESSON_NAME);
        lessonDescription = getArguments().getString(Constants.B_LESSON_DESCRIPTION);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lesson_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editLessonName = view.findViewById(R.id.edit_lesson_name);
        EditText editLessonDescription = view.findViewById(R.id.edit_lesson_description);

        editLessonName.setText(lessonName);
        editLessonDescription.setText(lessonDescription);

    }
}
