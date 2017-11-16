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
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private TextView noLessons;

    private SharedPreferences sharedPreferences;
    private String user_id = "null";
    private String project_id;

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

        noLessons = view.findViewById(R.id.textViewNoLessons);
        lessonsList = view.findViewById(R.id.lessons_list);

        lessonsList.setAdapter(lessonsAdapter);
        lessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getSummary(), lesson.getId()));

            }
        });
        setSwipeRefreshLayout();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lessons);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        if (lessonList.isEmpty()){
            noLessons.setVisibility(View.VISIBLE);
        }
        else {
            noLessons.setVisibility(View.GONE);
        }
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshListener = (new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean is_connected = Connectivity.isConnected(getContext());
                project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT, "");
                if(is_connected) {
                    VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Lesson lesson = new Lesson();
                            JSONArray jsonLessons;
                            try {
                                jsonLessons = new JSONArray(result);
                                for (int i = 0; i < jsonLessons.length(); i++) {
                                    JSONObject object = (JSONObject) jsonLessons.get(i);
                                    lesson.setName(object.get("name").toString());
                                    lesson.setSummary(object.get("summary").toString());
                                    lesson.setId(object.get("id").toString());
                                    lesson.setMotivation(object.get("motivation").toString());
                                    lesson.setLearning(object.get("learning").toString());
                                    lesson.setValidation(object.get("validation").toString());
                                    lesson.setUser_id(object.get("user_id").toString());
                                    lesson.setProject_id(object.get("project_id").toString());
                                    lesson.setCompany_id(object.get("company_id").toString());
                                    lesson.setReject_comment(object.get("reject_comment").toString());
                                    lesson.setComments(object.get("comments").toString());

                                    JsonParser parser = new JsonParser();
                                    JsonArray json = parser.parse(lesson.getComments()).getAsJsonArray();
                                    for (int j = 0; j < json.size(); j++) {
                                        JsonElement jsonObject = json.get(j);
                                        Comment comment = new Comment();
                                        comment.setText(jsonObject.getAsJsonObject().get("text").toString());
                                        comment.setId(jsonObject.getAsJsonObject().get("id").toString());
                                        JsonObject jsonObject1 = jsonObject.getAsJsonObject().get("user").getAsJsonObject();
                                        comment.setFirst_name(jsonObject1.get("first_name").toString());
                                        comment.setLast_name(jsonObject1.get("last_name").toString());
                                        comment.setPosition(jsonObject1.get("position").toString());
                                        comment.setAuthorId(jsonObject1.get("id").toString());
                                        comment.setLessonId(lesson.getId());
                                        try {
                                            new InsertCommentTask(comment, getContext()).execute().get();
                                        } catch (Exception e) {}
                                    }
                                    try {
                                        new InsertLessonTask(lesson, getContext()).execute().get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                                lessonsList.setAdapter(lessonsAdapter);
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {

                        }
                    }, getContext());
                } else {
                    try {

                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        lessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {}

                }
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }
}
