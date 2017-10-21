package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.threading.GetLessons;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyLessonsFragment extends Fragment {

    private ListView myLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;

    String user_id;
    String project_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        user_id = sharedpreferences.getString(Constants.SP_USER,"");
        project_id = sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

        getMyLessons(project_id,user_id);

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lessons, container, false);
    }

    public void getMyLessons(String project_id, String user_id){
        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    };


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

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_my_lessons);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            new DeleteLessonTable(getContext()).execute().get();
                        } catch (Exception e){Log.i("NOTDROPPING","LESSONS");}

                        Lesson lesson = new Lesson();
                        JSONArray jsonLessons;
                        try {
                            jsonLessons = new JSONArray(result);
                            for (int i = 0; i  < jsonLessons.length(); i++) {
                                Log.i("JSON",jsonLessons.get(i).toString());
                                JSONObject object = (JSONObject) jsonLessons.get(i);
                                lesson.setName(object.get("name").toString());
                                lesson.setDescription(object.get("summary").toString());
                                lesson.setId(object.get("id").toString());
                                //lesson.setDescription(learning);
                                lesson.setMotivation(object.get("motivation").toString());
                                lesson.setLearning(object.get("learning").toString());
                                lesson.setValidation(object.get("validation").toString());
                                lesson.setUser_id(object.get("user_id").toString());
                                lesson.setProject_id(object.get("project_id").toString());
                                lesson.setCompany_id(object.get("company_id").toString());
                                try {
                                    new InsertLessonTask(lesson, getContext()).execute().get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            lessonList = new GetLessonsTask(getActivity(), project_id, user_id).execute().get();
                            Log.i("REQSIZE",Integer.toString(lessonList.size()));

                            Log.i("LESSONLIST",lessonList.toString());
                            lessonsAdapter.notifyDataSetChanged();
                            myLessonsList.invalidateViews();
                            myLessonsList.setAdapter(lessonsAdapter);
                            Log.i("NOTIFIYDATACHANGED", "donde");
                            Intent intent = MainActivity.getIntent(getActivity());

                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        } catch (Exception e) {}
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {

                    }
                }, getContext());
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }
}
