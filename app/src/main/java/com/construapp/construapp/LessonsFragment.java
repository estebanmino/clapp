package com.construapp.construapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.content.SharedPreferences;

import com.construapp.construapp.models.AppDatabase;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.threading.RetrieveFeedTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

        //LessonModelList = new List<Lesson>();
        //ArrayList<Lesson> lessonList = new ArrayList<>();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String company_id = sharedpreferences.getString("company_id", "");
        RetrieveFeedTask lesson_fetcher=new RetrieveFeedTask("fetch-lessons");
        String lessons="";
        try {
            lessons = lesson_fetcher.execute(company_id).get();
            //Toast.makeText(this,lessons,Toast.LENGTH_SHORT).show();
        }
        catch(InterruptedException e)
        {

        }
        catch (ExecutionException e)
        {

        }
        //Log.i("TAG",lessons);

        //TODO hacer que se agreguen solo los items que no estaban en la DB
        //considerar el caso que hayan cambiado alguna leccion/
        //se podria hacer un fetch de la leccion abierta verificando que los datos sean los ultimos
        //mediante el param "updated_at" en api_request

        try
        {

            JSONArray lesson_array = new JSONArray(lessons);
            int num = lesson_array.length();
            for(int i=0;i<lesson_array.length();i++)
            {
//                JSONObject curr = lesson_array.getJSONObject(i);
//
//                String name = curr.getString("name");
//                Log.i("ldebug",name);
//                String learning = curr.getString("learning");
//                String id = curr.getString("id");
//                //Log.i("ldebug",learning);
//                //Log.i("ldebug","hola");
//                Lesson lesson_1 = new Lesson();
//                lesson_1.setName(name);
//                lesson_1.setDescription(learning);
//                lesson_1.setId(id);
//                //TODO ACA HAY QUE AGREGAR LOS ELEM
//                //MEDIANTE PARSEO DE JSONARRAY Y CREAR MULTIMEDIA FILES
//                //AGREGANDOLOS A LA LESSON
//                LessonModelList.add(lesson_1);
//                Log.i("count",String.valueOf(i));

            }
            Log.i("ROOM","NO DEBE SALIR NADA");


            try {

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Lesson lesson_1 = new Lesson();
                        lesson_1.setName("Prueba Jose");
                        lesson_1.setDescription("Prueba Room");
                        lesson_1.setId("1");
                        AppDatabase.getDatabase(getActivity()).lessonDAO().insertLesson(lesson_1);
                        Log.i("CREAR","CREE LA LECCION");
                        return "1";
                    }


                }.execute().get();
            }
            catch(InterruptedException e)
            {
                Log.i("INTERRUPTED","ERROR");

            }
            catch(ExecutionException e)
            {
                Log.i("EXECUTION","ERROR");
            }

            Log.i("intermedio","sali de crear la leccion");


            try {

                //LessonModelList = new List<Lesson>();

                List<Lesson> aux = new AsyncTask<Void, Void, List<Lesson>>() {
                    @Override
                    protected List<Lesson> doInBackground(Void... params) {
//                        Lesson lesson_1 = new Lesson();
//                        lesson_1.setName("Prueba Jose");
//                        lesson_1.setDescription("Prueba Room");
//                        lesson_1.setId("1");
//                        AppDatabase.getDatabase(getActivity()).lessonDAO().insertLesson(lesson_1);
                        Log.i("OBTENER","OBTENDRE LECCION");

                        return AppDatabase.getDatabase(getActivity()).lessonDAO().getAllLessons();
                    }


                }.execute().get();

                //Log.i("MENSAJE ULTRA IMPORTANT","EL VALOR ES "+aux);
                LessonModelList = aux;
            }
            catch(InterruptedException e)
            {
                Log.i("INTERRUPTED","ERROR");

            }
            catch(ExecutionException e)
            {
                Log.i("EXECUTION","ERROR lessons");
            }

        }
        catch(JSONException e)
        {

        }


        lessonsAdapter = new LessonsAdapter(getActivity(), LessonModelList);

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
                //Toast.makeText(MyLessonsFragment.this, "" + position, Toast.LENGTH_LONG).show();
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                Log.i("ROOM NAME",lesson.getName());
                Log.i("ROOM DESCR",lesson.getDescription());
                Log.i("ROOM COMPANYID",lesson.getCompany_id());



                //TODO esta SP pasa los params de lesson
                SharedPreferences spl = getActivity().getSharedPreferences("Lesson", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spl.edit();
                editor.putString("lesson_name", lesson.getName());
                editor.putString("lesson_description",lesson.getDescription());
                editor.putString("lesson_id",lesson.getId());

                editor.commit();
                

                startActivity(LessonActivity.getIntent(getActivity(), lesson.getName(),
                        lesson.getDescription()));
            }
        });
    }

}
