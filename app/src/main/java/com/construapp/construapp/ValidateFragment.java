package com.construapp.construapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.ValidateLesson;

import java.util.ArrayList;


public class ValidateFragment extends Fragment {

    private ListView validateLessonsList;
    private LessonsAdapter validateLessonsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<Lesson> lessonList = new ArrayList<>();

        Lesson lesson_1 = new Lesson();
        lesson_1.setName("Lección 1");
        lesson_1.setDescription("Descripción 1");

        Lesson lesson_2 = new Lesson();
        lesson_2.setName("Lección 2");
        lesson_2.setDescription("Descripción 2");

        lessonList.add(lesson_2);
        lessonList.add(lesson_1);

        validateLessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validateLessonsList = view.findViewById(R.id.validate_lessons_list);

        validateLessonsList.setAdapter(validateLessonsAdapter);
        validateLessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) validateLessonsAdapter.getItem(position);
                startActivity(ValidateLessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription(),lesson.getId()));
            }
        });
    }
}
