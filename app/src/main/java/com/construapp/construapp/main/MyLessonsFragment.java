package com.construapp.construapp.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyLessonsFragment extends Fragment {

    private ListView myLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String user_id;
    private String project_id;

    private Button btnLessonsSaved;
    private Button btnLessonsRejected;
    private String lessonsValidationState;

    private SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity());
        user_id = sessionManager.getUserId();
        project_id = sessionManager.getActualProjectId();

        getMyLessons(project_id,user_id, Constants.R_SAVED);

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lessons, container, false);
    }

    public void getMyLessons(String project_id, String user_id, String validation){
        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id,validation).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myLessonsList = view.findViewById(R.id.my_lessons_list);

        btnLessonsRejected  = view.findViewById(R.id.btn_lessons_rejected);
        btnLessonsSaved  = view.findViewById(R.id.btn_lessons_saved);
        btnLessonsSaved.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        lessonsValidationState = Constants.R_REJECTED;

        btnLessonsRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonsValidationState = Constants.R_REJECTED;
                btnLessonsRejected.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                btnLessonsSaved.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Log.i("VALIDATION",lessonsValidationState);
                refreshData();
            }
        });

        btnLessonsSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonsValidationState = Constants.R_SAVED;
                btnLessonsRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnLessonsSaved.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                Log.i("VALIDATION",lessonsValidationState);
                refreshData();
            }
        });

        myLessonsList.setAdapter(lessonsAdapter);
        myLessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getSummary(),lesson.getId()));
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_my_lessons);
        setSwipeRefreshLayout();
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    public void refreshData(){
        boolean is_connected = Connectivity.isConnected(getContext());
        user_id = sessionManager.getUserId();
        project_id = sessionManager.getActualProjectId();
        if(is_connected) {
            VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {

                    try {
                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id, lessonsValidationState).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        myLessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {}
                }

                @Override
                public void onErrorResponse(VolleyError result) {
                    try {
                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id, lessonsValidationState).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        myLessonsList.setAdapter(lessonsAdapter);
                    }catch (Exception e) {}
                }
            }, getContext());
        } else {
            try {

                lessonList = new GetLessonsTask(getActivity(), project_id, user_id,lessonsValidationState).execute().get();
                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                myLessonsList.setAdapter(lessonsAdapter);
            } catch (Exception e) {}

        }
        swipeRefreshLayout.setRefreshing(false);

    }
}