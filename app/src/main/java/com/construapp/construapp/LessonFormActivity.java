package com.construapp.construapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.lessonForm.RealPathUtil;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.construapp.construapp.api.VolleyCreateLesson;
import com.construapp.construapp.api.VolleyPostS3;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class LessonFormActivity extends LessonBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FIND XML ELEMENTS
        lessonName = (TextView) findViewById(R.id.text_new_lesson_name);
        lessonDescription = (TextView) findViewById(R.id.text_new_lesson_description);

        editLessonName =(EditText) findViewById(R.id.text_lesson_name);
        editLessonDescription = (EditText) findViewById(R.id.text_lesson_description);

        mLayout = findViewById(R.id.lesson_form_layout);

        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        fabSend = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        fabVideo = findViewById(R.id.fab_video);
        textRecording = findViewById(R.id.text_recording);

        //INIT NEW LESSON
        lesson = new Lesson();
        setLesson();
        lesson.initMultimediaFiles();

        //INIT CONSTANTS
        constants = new General();

        mStartRecording = true;


        // Record to the external cache directory for visibility
        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();
        mRecordFileName = ABSOLUTE_STORAGE_PATH + "/audiorecordtest.3gp";

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonFormActivity.this));
        transferUtility = new TransferUtility(s3, LessonFormActivity.this);

        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        setFabSendOnClickListener();
        setFabRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setFabVideoOnClickListener();

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonFormActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mVideosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),LessonFormActivity.this);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonFormActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonFormActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);
    }

    public void setFabSendOnClickListener(){
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO fix params, working with sharedpreferences
                sharedPreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
                String lesson_name = editLessonName.getText().toString();
                String lesson_summary = editLessonName.getText().toString();
                String lesson_motivation = "Aprendizaje";
                String lesson_learning = editLessonDescription.getText().toString();
                //TODO FIJAR PROYECTO CUANDO EXISTA
                String project_id = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

                VolleyCreateLesson.volleyCreateLesson(new VolleyJSONCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                          final String new_lesson_id = result.get("id").toString();
                          String path_input = "";
                          for (MultimediaFile multimediaFile : lesson.getMultimediaPicturesFiles()) {
                              multimediaFile.setExtension(Constants.S3_LESSONS_PATH+ "/"+ new_lesson_id +"/" +
                                      multimediaFile.getExtension());
                              path_input += multimediaFile.getExtension() + "/" +
                                      multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1) + ";";
                          }
                          for (MultimediaFile multimediaFile : lesson.getMultimediaAudiosFiles()) {
                              multimediaFile.setExtension(Constants.S3_LESSONS_PATH+ "/"+ new_lesson_id +"/" +
                                      multimediaFile.getExtension());
                              path_input += multimediaFile.getExtension() + "/"
                                      + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1) + ";";
                          }
                          for (MultimediaFile multimediaFile : lesson.getMultimediaDocumentsFiles()) {
                              multimediaFile.setExtension(Constants.S3_LESSONS_PATH+ "/"+ new_lesson_id +"/" +
                                      multimediaFile.getExtension());
                              path_input += multimediaFile.getExtension() + "/"
                                      + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1) + ";";
                          }

                          for (MultimediaFile multimediaFile : lesson.getMultimediaVideosFiles()) {
                              multimediaFile.setExtension(Constants.S3_LESSONS_PATH+ "/"+ new_lesson_id +"/" +
                                      multimediaFile.getExtension());
                              path_input += multimediaFile.getExtension() + "/"
                                      + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1) + ";";
                          }

                          VolleyPostS3.volleyPostS3(new VolleyJSONCallback() {
                              @Override
                              public void onSuccess(JSONObject result) {
                                  for (MultimediaFile multimediaFile: lesson.getMultimediaPicturesFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile: lesson.getMultimediaAudiosFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile: lesson.getMultimediaDocumentsFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile: lesson.getMultimediaVideosFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                      startActivity(MainActivity.getIntent(LessonFormActivity.this));
                              }

                              @Override
                              public void onErrorResponse(VolleyError result) {
                              }
                          }, LessonFormActivity.this, new_lesson_id, path_input.split(";"));
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {

                    }
                }, LessonFormActivity.this, lesson_name, lesson_summary,
                    lesson_motivation, lesson_learning,
                    project_id);
                Toast.makeText(LessonFormActivity.this, "Nueva lección creada", Toast.LENGTH_LONG).show();
            }
        });
    }

}
