package com.construapp.construapp.main;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LessonsFragment extends Fragment {

    private LessonsAdapter lessonsAdapter;
    private AppDatabase appDatabase;
    private JSONArray jsonLessons;
    //muestra los items lesson lesson
    private ListView lessonsList;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SharedPreferences sharedPreferences;
    private String user_id = "null";
    private String project_id;
    private TextView textViewNoLessons;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        String project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id,Constants.R_VALIDATED).execute().get();
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

        lessonsList = view.findViewById(R.id.lessons_list);
        textViewNoLessons = view.findViewById(R.id.textViewNoLessons);

        lessonsList.setAdapter(lessonsAdapter);
        lessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getSummary(), lesson.getId()));
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lessons);
        if (lessonList.isEmpty()) {
            textViewNoLessons.setVisibility(View.VISIBLE);
        }
        setSwipeRefreshLayout();
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean is_connected = Connectivity.isConnected(getContext());
                project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT, "");
                if(is_connected) {
                    VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                                lessonsList.setAdapter(lessonsAdapter);
                            } catch (Exception e) {
                            }
                            if (lessonList.isEmpty()) {
                                textViewNoLessons.setVisibility(View.VISIBLE);
                            } else {
                                textViewNoLessons.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {
                            try {
                                lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                                lessonsList.setAdapter(lessonsAdapter);
                            } catch (Exception e) {
                            }
                            if (lessonList.isEmpty()) {
                                textViewNoLessons.setVisibility(View.VISIBLE);
                            } else {
                                textViewNoLessons.setVisibility(View.GONE);
                            }
                        }
                    }, getContext());
                } else {
                    try {

                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        lessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {}
                    if (lessonList.isEmpty()) {
                        textViewNoLessons.setVisibility(View.VISIBLE);
                    } else {
                        textViewNoLessons.setVisibility(View.GONE);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }
}