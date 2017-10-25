package com.construapp.construapp;

import android.content.Context;
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
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyLessonsFragment extends Fragment {

    private ListView myLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    private String user_id;
    private String project_id;

    private Button btnLessonsSaved;
    private Button btnLessonsRejected;
    private String lessonsValidationState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.SP_USER,"");
        project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

        getMyLessons(project_id,user_id);

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lessons, container, false);
    }

    public void getMyLessons(String project_id, String user_id){
        try {
            lessonList = new GetLessonsTask(getActivity(),project_id,user_id,Constants.R_SAVED).execute().get();
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

        btnLessonsRejected  = view.findViewById(R.id.btn_lessons_rejected);
        btnLessonsSaved  = view.findViewById(R.id.btn_lessons_saved);
        btnLessonsRejected.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        lessonsValidationState = Constants.R_SAVED;

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
                        lesson.getDescription(),lesson.getId()));
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
        user_id = sharedPreferences.getString(Constants.SP_USER, "");
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
                            Log.i("JSON", jsonLessons.get(i).toString());
                            JSONObject object = (JSONObject) jsonLessons.get(i);
                            //TODO refactoring de params 24-10
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
                        lessonList = new GetLessonsTask(getActivity(), project_id, user_id,lessonsValidationState).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        myLessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onErrorResponse(VolleyError result) {

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
