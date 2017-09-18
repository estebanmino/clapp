package com.construapp.construapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;

public class MyLessonsFragment extends Fragment {

    private ListView myLessonsList;
    private LessonsAdapter lessonsAdapter;

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

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lessons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myLessonsList = view.findViewById(R.id.my_lessons_list);

        myLessonsList.setAdapter(lessonsAdapter);
        myLessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Toast.makeText(MyLessonsFragment.this, "" + position, Toast.LENGTH_LONG).show();
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                Log.i("OBJECT",lesson.getName());
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription()));
            }
        });


    }

}
