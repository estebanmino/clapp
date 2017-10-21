package com.construapp.construapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.dbTasks.GetLessonsTask;

import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LessonsFragment extends Fragment {

    private LessonsAdapter lessonsAdapter;
    private AppDatabase appDatabase;
    private JSONArray jsonLessons;
    //muestra los items lesson lesson
    private ListView LessonsList;
    private List<Lesson> lessonList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String user_id = "null";
        String project_id = sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LessonsList = view.findViewById(R.id.lessons_list);

        LessonsList.setAdapter(lessonsAdapter);
        LessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription(), lesson.getId()));
            }
        });
    }
    }
