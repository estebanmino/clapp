package com.construapp.construapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Connectivity;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.threading.GetLessonsTask;
import com.construapp.construapp.threading.InsertLessonTask;
import com.construapp.construapp.threading.RetrieveFeedTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LessonsFragment extends Fragment {

    private LessonsAdapter lessonsAdapter;
    private AppDatabase appDatabase;

    //muestra los items lesson lesson
    private ListView LessonsList;
    private List<Lesson> lessonList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        lessonList = new ArrayList<>();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString("company_id", "");
        RetrieveFeedTask lesson_fetcher=new RetrieveFeedTask("fetch-lessons");
        String lessons="";

        boolean is_connected = Connectivity.isConnected(getActivity());
        Toast.makeText(getActivity(),"Estado conexion: "+is_connected,Toast.LENGTH_SHORT).show();

        if(is_connected) {
            try {
                lessons = lesson_fetcher.execute(company_id).get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }


            try {
                JSONArray lesson_json_array = new JSONArray(lessons);
                for (int i = 0; i < lesson_json_array.length(); i++) {
                    JSONObject current_object = lesson_json_array.getJSONObject(i);
                    String name = current_object.getString("name");
                    String summary = current_object.getString("summary");
                    String id = current_object.getString("id");
                    try {
                        new InsertLessonTask(name, summary, id, getActivity()).execute().get();
                        //databaseThread.addLesson(getActivity(),name,summary,id);
                        Log.i("ROOM", "hice inserts!");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
            }

            Log.i("ROOM", "VOY A CONSEGUIR ALL_LESONS");
            try {
                lessonList = new GetLessonsTask(getActivity()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.i("ROOM_SIZE_NULL: ", String.valueOf(lessonList == null));
        }
        //If isConnected() is False
        else
        {
            try {
                lessonList = new GetLessonsTask(getActivity()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Log.i("ROOM_SIZE_NULL: ", String.valueOf(lessonList == null));


        }

        lessonsAdapter = new LessonsAdapter(getActivity(), lessonList);

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
