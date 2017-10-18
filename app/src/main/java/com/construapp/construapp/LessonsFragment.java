package com.construapp.construapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ListView;

import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.threading.api.RetrieveFeedTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LessonsFragment extends Fragment {

    private LessonsAdapter lessonsAdapter;
    //muestra los items lesson lesson
    private ListView LessonsList;
    private List<Lesson> LessonModelList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ArrayList<Lesson> lessonList = new ArrayList<>();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString(Constants.SP_COMPANY, "");

        // TODO: 18-10-2017 refactor to volley
        if (sharedpreferences.getBoolean(Constants.SP_HAS_PROJECTS, false)) {
            RetrieveFeedTask lesson_fetcher = new RetrieveFeedTask("fetch-lessons");
            String lessons = "";
            try {
                lessons = lesson_fetcher.execute(company_id).get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }

            try {
                JSONArray lesson_array = new JSONArray(lessons);
                for (int i = 0; i < lesson_array.length(); i++) {
                    JSONObject curr = lesson_array.getJSONObject(i);
                    String name = curr.getString("name");
                    String learning = curr.getString("learning");
                    String id = curr.getString("id");
                    Lesson lesson_1 = new Lesson();
                    lesson_1.setName(name);
                    lesson_1.setDescription(learning);
                    lesson_1.setId(id);
                    lessonList.add(lesson_1);
                }
            } catch (JSONException e) {
            }
            lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
        }

        return inflater.inflate(R.layout.fragment_my_lessons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LessonsList = view.findViewById(R.id.my_lessons_list);

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
