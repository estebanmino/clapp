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
import android.content.SharedPreferences;

import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyLessonsFragment extends Fragment {

    private ListView myLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String user_id = sharedpreferences.getString(Constants.SP_USER,"");
        String project_id = sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription(),lesson.getId()));
            }
        });
    }
}
