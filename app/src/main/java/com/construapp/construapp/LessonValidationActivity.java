package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.api.VolleyFetchLessonMultimedia;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

public class LessonValidationActivity extends LessonBaseActivity {

    private static final String LESSON_NAME = "username";
    private static final String LESSON_DESCRIPTION = "description";
    private static final String LESSON_ID = "id";
    private static final String PROJECT_FOLDER = "ConstruApp";

    TextView textSaveValidatedLesson;
    TextView textSendValidatedLessonComments;

    Switch validateNameSwitch;
    Switch validateDescriptionSwitch;
    Switch validateImagesSwitch;
    Switch validateVideosSwitch;
    Switch validateAudiosSwitch;
    Switch validateDocumentsSwitch;
    EditText validateNameComment;
    EditText validateDescriptionComment;
    EditText validateImagesComment;
    EditText validateVideosComment;
    EditText validateAudiosComment;
    EditText validateDocumentsComment;
    TextView switch_validateImageStringText;
    TextView switch_validateVideoStringText;
    TextView switch_validateDocumentStringText;
    TextView switch_validateAudioStringText;
    FloatingActionButton buttonSaveValidatedLesson;
    FloatingActionButton buttonSendValidatedLessonComments;

    private Boolean editing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        constants = new General();

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonValidationActivity.this));
        transferUtility = new TransferUtility(s3, LessonValidationActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = LessonValidationActivity.this.getCacheDir().toString();

        lessonName = (TextView) findViewById(R.id.text_lesson_name);
        lessonDescription = (TextView) findViewById(R.id.text_lesson_description);

        setLesson();

        lessonName.setText(lesson.getName());
        lessonDescription.setText(lesson.getDescription());
        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonValidationActivity.this,lesson);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonValidationActivity.this,lesson);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mVideosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),LessonValidationActivity.this,lesson);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonValidationActivity.this,lesson);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);


        validateNameSwitch = (Switch) findViewById(R.id.switch_lesson_name);
        validateDescriptionSwitch = (Switch) findViewById(R.id.switch_lesson_description);
        validateImagesSwitch = (Switch) findViewById(R.id.switch_lesson_images);
        validateVideosSwitch = (Switch) findViewById(R.id.switch_lesson_video);
        validateAudiosSwitch = (Switch) findViewById(R.id.switch_lesson_audios);
        validateDocumentsSwitch = (Switch) findViewById(R.id.switch_lesson_documents);

        validateNameComment = (EditText) findViewById(R.id.editText_validate_name_comment);
        validateDescriptionComment = (EditText) findViewById(R.id.editText_validate_description_comment);
        validateImagesComment = (EditText) findViewById(R.id.editText_validate_images_comment);
        validateVideosComment = (EditText) findViewById(R.id.editText_validate_videos_comment);
        validateAudiosComment = (EditText) findViewById(R.id.editText_validate_audios_comment);
        validateDocumentsComment = (EditText) findViewById(R.id.editText_validate_documents_comment);

        switch_validateAudioStringText = (TextView) findViewById(R.id.textView_switch_comment_lesson_audios);
        switch_validateVideoStringText = (TextView) findViewById(R.id.textView_switch_comment_lesson_video);
        switch_validateDocumentStringText = (TextView) findViewById(R.id.textView_switch_comment_lesson_documents);
        switch_validateImageStringText = (TextView) findViewById(R.id.textView_switch_comment_lesson_images);

        textSaveValidatedLesson = (TextView) findViewById(R.id.textView_save_validated_lesson);
        textSendValidatedLessonComments = (TextView) findViewById(R.id.textView_send_validated_lesson_comments);
        buttonSaveValidatedLesson = (FloatingActionButton) findViewById(R.id.floatingActionButton_save_validated_lesson);
        buttonSendValidatedLessonComments = (FloatingActionButton) findViewById(R.id.floatingActionButton_send_validated_lesson_comments);



        validateNameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateNameComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateNameComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateDescriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateDescriptionComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateDescriptionComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateImagesComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateImagesComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateVideosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateVideosComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateVideosComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateAudiosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateAudiosComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateAudiosComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateDocumentsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    validateDocumentsComment.setVisibility(View.VISIBLE);
                }
                else {
                    validateDocumentsComment.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });



        VolleyFetchLessonMultimedia.volleyFetchLessonMultimedia(new VolleyJSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try{
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(result.toString()).getAsJsonObject();
                    ArrayList<String> arrayList = new ArrayList<>();
                    JsonArray jsonArray = (JsonArray) json.get("filekeys");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonElement jsonObject = (JsonElement) jsonArray.get(i);
                        arrayList.add(jsonObject.getAsJsonObject().get("path").toString());
                    }

                    ArrayList<String> picturePathsList  = new ArrayList<>();
                    ArrayList<String> audioPathsList  = new ArrayList<>();
                    ArrayList<String> documentPathsList  = new ArrayList<>();
                    ArrayList<String> videosPathsList  = new ArrayList<>();



                    for (String fileKey: arrayList) {
                        if (fileKey.contains(Constants.S3_IMAGES_PATH)){
                            picturePathsList.add(fileKey);
                            validateImagesSwitch.setVisibility(View.VISIBLE);
                            switch_validateImageStringText.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_AUDIOS_PATH)) {
                            audioPathsList.add(fileKey);
                            validateAudiosSwitch.setVisibility(View.VISIBLE);
                            switch_validateAudioStringText.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_DOCS_PATH)) {
                            documentPathsList.add(fileKey);
                            validateDocumentsSwitch.setVisibility(View.VISIBLE);
                            switch_validateDocumentStringText.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_VIDEOS_PATH)) {
                            videosPathsList.add(fileKey);
                            validateVideosSwitch.setVisibility(View.VISIBLE);
                            switch_validateVideoStringText.setVisibility(View.VISIBLE);
                        }
                    }

                    String[] pictureArray = picturePathsList.toArray(new String[0]);
                    for (String path: pictureArray){
                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_IMAGES_PATH,CACHE_FOLDER+"/"+path.substring(path.lastIndexOf("/")+1,path.length()-1),
                                path.replace("\"", ""),transferUtility,0));
                    }
                    multimediaPictureAdapter.notifyDataSetChanged();

                    for (String videoPath: videosPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_VIDEOS_PATH,ABSOLUTE_STORAGE_PATH+"/"+PROJECT_FOLDER+"/"+
                                videoPath.substring(videoPath.lastIndexOf("/")+1,videoPath.length()-1),
                                videoPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaVideosFiles().add(documentMultimedia);
                    }

                    for (String audioPath: audioPathsList) {
                        MultimediaFile audioMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_AUDIOS_PATH,CACHE_FOLDER+"/"+audioPath.substring(audioPath.lastIndexOf("/")+1,audioPath.length()-1),
                                audioPath.replace("\"", ""),transferUtility,0);
                        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
                    }
                    multimediaAudioAdapter.notifyDataSetChanged();

                    for (String documentPath: documentPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_DOCS_PATH,ABSOLUTE_STORAGE_PATH+"/"+
                                PROJECT_FOLDER+"/"+documentPath.substring(documentPath.lastIndexOf("/")+1,documentPath.length()-1),
                                documentPath.replace("\"", ""),transferUtility,0);
                        lesson.getMultimediaDocumentsFiles().add(documentMultimedia);
                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, LessonValidationActivity.this, lesson.getId());
    }

    public void checkedComments(){
        if (validateNameSwitch.isChecked() || validateDescriptionSwitch.isChecked() || validateImagesSwitch.isChecked() || validateVideosSwitch.isChecked() || validateAudiosSwitch.isChecked() || validateDocumentsSwitch.isChecked()){
            textSaveValidatedLesson.setVisibility(View.GONE);
            buttonSaveValidatedLesson.setVisibility(View.GONE);
            textSendValidatedLessonComments.setVisibility(View.VISIBLE);
            buttonSendValidatedLessonComments.setVisibility(View.VISIBLE);
        }
        else {
            textSaveValidatedLesson.setVisibility(View.VISIBLE);
            buttonSaveValidatedLesson.setVisibility(View.VISIBLE);
            textSendValidatedLessonComments.setVisibility(View.GONE);
            buttonSendValidatedLessonComments.setVisibility(View.GONE);
        }
    }

    public void setLesson() {
        lesson = new Lesson();
        lesson.setName(getIntent().getStringExtra(LESSON_NAME));
        lesson.setDescription(getIntent().getStringExtra(LESSON_DESCRIPTION));
        lesson.setId(getIntent().getStringExtra(LESSON_ID));
        //lesson.setCompany_id(sharedPreferences.getString(Constants.SP_COMPANY, ""));
        lesson.initMultimediaFiles();
    }

    public static Intent getIntent(Context context, String name, String description, String id) {
        Intent intent = new Intent(context,LessonValidationActivity.class);
        intent.putExtra(LESSON_NAME,name);
        intent.putExtra(LESSON_DESCRIPTION,description);
        intent.putExtra(LESSON_ID,id);
        return intent;
    }
}