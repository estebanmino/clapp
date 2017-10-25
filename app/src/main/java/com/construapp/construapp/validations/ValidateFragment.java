package com.construapp.construapp.validations;

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

import com.android.volley.VolleyError;
import com.construapp.construapp.LessonsAdapter;
import com.construapp.construapp.R;
import com.construapp.construapp.LessonValidationActivity;
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.api.VolleyGetValidationPermission;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetValidationsTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ValidateFragment extends Fragment {

    private ListView validateLessonsList;
    private LessonsAdapter lessonsAdapter;
    private List<Lesson> lessonList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    private String user_id;
    private String project_id;
    public ArrayList<String> validationProjectsArray;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.SP_USER,"");
        project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

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
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        validateLessonsList = view.findViewById(R.id.validation_lessons_list);

        validateLessonsList.setAdapter(lessonsAdapter);
        validateLessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonValidationActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription(),lesson.getId()));
            }
        });
        getLessonsToValidate();

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_validations);
        setSwipeRefreshLayout();
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLessonsToValidate();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    public void getLessonsToValidate() {
        boolean is_connected = Connectivity.isConnected(getContext());
        user_id = sharedPreferences.getString(Constants.SP_USER, "");
        project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT, "");
        if(is_connected) {
            VolleyGetValidationPermission.volleyGetValidationPermission(new VolleyStringCallback(){
                @Override
                public void onSuccess(String result){
                    JSONArray jsonLessons;
                    try {
                        jsonLessons = new JSONArray(result);
                        validationProjectsArray = new ArrayList<String>();
                        for (int i=0;i<jsonLessons.length();i++)
                        {
                            JSONObject object = (JSONObject) jsonLessons.getJSONObject(i);
                            String permission = object.get("permission_id").toString();
                            if(permission.equals("4"))
                            {
                                JSONObject project_object = (JSONObject) object.get("project");
                                validationProjectsArray.add(project_object.get("id").toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onErrorResponse(VolleyError result) {
                    Log.i("PARSE_ERROR","OVERERRORRESPONSE");
                }
            },getContext());
            VolleyGetLessons.volleyGetLessons(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    Lesson lesson = new Lesson();
                    JSONArray jsonLessons;
                    try {
                        jsonLessons = new JSONArray(result);
                        for (int i = 0; i < jsonLessons.length(); i++) {
                            //Log.i("JSON", jsonLessons.get(i).toString());
                            JSONObject object = (JSONObject) jsonLessons.get(i);
                            //TODO: REFACTORING params 23-10
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
                            lesson.setValidator("false");
                            for(int j=0;j<validationProjectsArray.size();j++)
                            {
                                if(lesson.getProject_id().equals(validationProjectsArray.get(j)))
                                {
                                    lesson.setValidator("true");
                                }
                                else {}
                            }
                            try {
                                new InsertLessonTask(lesson, getContext()).execute().get();
                            } catch (ExecutionException e) {
                            } catch (InterruptedException e) {
                            }
                        }
                        lessonList = new GetValidationsTask(getActivity(), project_id).execute().get();

                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        validateLessonsList.setAdapter(lessonsAdapter);
                    } catch (Exception e) {}
                }
                @Override
                public void onErrorResponse(VolleyError result) {
                    try {
                        lessonList = new GetValidationsTask(getActivity(), project_id).execute().get();
                        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                        validateLessonsList.setAdapter(lessonsAdapter);
                    } catch ( Exception e ){}
                }
            }, getContext());
        } else {
            try {
                lessonList = new GetValidationsTask(getActivity(), project_id).execute().get();
                lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);
                validateLessonsList.setAdapter(lessonsAdapter);
            } catch (Exception e) {}
        }
    }
}
