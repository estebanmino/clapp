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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.lessons.LessonValidationActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetValidationsTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class LessonValidateFragment extends Fragment {

    private ListView validateLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private SessionManager sessionManager;
    private TextView noLessons;

    private String user_id;
    private String project_id;
    public ArrayList<String> validationProjectsArray;

    private TextView textViewNoLessons;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity());
        user_id = sessionManager.getUserId();
        project_id = sessionManager.getActualProjectId();

        getValidations(project_id);
        lessonsAdapter = new LessonsAdapter(getActivity(),lessonList);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validate, container, false);
    }

    public void getValidations(String project_id){
        try {
            lessonList = new GetValidationsTask(getActivity(),project_id).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.i("VALIDATION","EXECUTION EX");
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noLessons = view.findViewById(R.id.textViewNoValidateLessons);
        validateLessonsList = view.findViewById(R.id.validation_lessons_list);
        textViewNoLessons = view.findViewById(R.id.textViewNoValidateLessons);

        validateLessonsList.setAdapter(lessonsAdapter);
        validateLessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonValidationActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getSummary(),lesson.getId()));
            }
        });
        getLessonsToValidate();

        setSwipeRefreshLayout();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_validations);
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
                getLessonsToValidate();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    public void getLessonsToValidate() {
        boolean is_connected = Connectivity.isConnected(getContext());
        user_id = sessionManager.getUserId();
        project_id = sessionManager.getActualProjectId();
        if(is_connected) {
            if (Integer.parseInt(sessionManager.getActualUserPermission()) >= Integer.parseInt(Constants.P_VALIDATE)){
                VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id, Constants.R_WAITING).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        validateLessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {}
                    if (lessonList.isEmpty()) {
                        textViewNoLessons.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onErrorResponse(VolleyError result) {
                    try {
                        lessonList = new GetValidationsTask(getActivity(), project_id).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        validateLessonsList.setAdapter(lessonsAdapter);
                    } catch ( Exception e ){}
                    if (lessonList.isEmpty()) {
                        textViewNoLessons.setVisibility(View.VISIBLE);
                    }
                }
            }, getContext());
            } else {
                lessonList.clear();
                ArrayList<String> idsArray = sessionManager.getPendingValidations();
                for (String id: idsArray) {
                    try {
                        lessonList.add(new GetLessonTask(getActivity(), id).execute().get());
                    } catch (Exception e) {}
                }
                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                validateLessonsList.setAdapter(lessonsAdapter);
                if (lessonList.isEmpty()) {
                    textViewNoLessons.setVisibility(View.VISIBLE);
                }
            }
        } else {
            try {
                lessonList = new GetValidationsTask(getActivity(), project_id).execute().get();
                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                validateLessonsList.setAdapter(lessonsAdapter);
            } catch (Exception e) {}
            if (lessonList.isEmpty()) {
                textViewNoLessons.setVisibility(View.VISIBLE);
            }
        }
    }
}
