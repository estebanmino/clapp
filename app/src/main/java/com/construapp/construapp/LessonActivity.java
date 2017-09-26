package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.construapp.construapp.models.Lesson;

import org.w3c.dom.Text;


public class LessonActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";

    private Lesson lesson = new Lesson();
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView nombre_proyecto = (TextView) findViewById(R.id.textViewNombreProyecto);
        TextView descripcion_leccion = (TextView) findViewById(R.id.textViewDescripcionLeccion);
        SharedPreferences spl = getSharedPreferences("Lesson", Context.MODE_PRIVATE);

        nombre_proyecto.setText(spl.getString("lesson_name", ""));
        descripcion_leccion.setText(spl.getString("lesson_description", ""));

        setLesson();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void showInfo(View view) {
        Intent intent = new Intent(this, ShowInfo.class);
        TextView projectName = (TextView) findViewById(R.id.textViewNombreProyecto);
        String message = projectName.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void setLesson() {
        lesson.setName(getIntent().getStringExtra(USERNAME));
        lesson.setDescription(getIntent().getStringExtra(DESCRIPTION));
    }

    public static Intent getIntent(Context context, String name, String description) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(USERNAME,name);
        intent.putExtra(DESCRIPTION,description);
        //intent.putExtra(KEY_CHAT_ROOM_UUID,chatRoomUuid);

        return intent;
    }


}