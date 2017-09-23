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

import com.construapp.construapp.models.Lesson;

public class LessonActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";
    private TextView lessonName;
    private TextView  lessonDescription;

    private Lesson lesson = new Lesson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lessonName = (TextView) findViewById(R.id.lesson_name);
        lessonDescription = (TextView) findViewById(R.id.lesson_description);
        setLesson();

        lessonName.setText(lesson.getName());
        lessonDescription.setText(lesson.getDescription());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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