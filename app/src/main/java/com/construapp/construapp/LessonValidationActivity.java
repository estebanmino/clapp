package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyPutRejectLesson;
import com.construapp.construapp.api.VolleyPutValidateLesson;
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
    private static final String PROJECT_FOLDER = Constants.M_APP_DIRECTORY;

    private TextView textSaveValidatedLesson;
    private TextView textSendValidatedLessonComments;

    private Switch validateNameSwitch;
    private Switch validateDescriptionSwitch;
    private Switch validateImagesSwitch;
    private Switch validateVideosSwitch;
    private Switch validateAudiosSwitch;
    private Switch validateDocumentsSwitch;
    private EditText editCommentName;
    private EditText editCommentDescription;
    private EditText editCommentImages;
    private EditText editCommentVideos;
    private EditText editCommentAudios;
    private EditText editCommentDocuments;
    private TextView switch_validateImageStringText;
    private TextView switch_validateVideoStringText;
    private TextView switch_validateDocumentStringText;
    private TextView switch_validateAudioStringText;
    private FloatingActionButton fabValidateLesson;
    private FloatingActionButton fabCommentLeson;
    private FloatingActionButton fabCancel;

    private TextView textImages;
    private TextView textVideos;
    private TextView textAudios;
    private TextView textDocuments;

    private Boolean editing = true;

    private RecyclerView mPicturesRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mDocumentsRecyclerView;
    private RecyclerView mAudiosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_lesson);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        constants = new General();

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonValidationActivity.this));
        transferUtility = new TransferUtility(s3, LessonValidationActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = LessonValidationActivity.this.getCacheDir().toString();

        lessonName = findViewById(R.id.text_lesson_name);
        lessonDescription = findViewById(R.id.text_lesson_description);

        setLesson();

        lessonName.setText(lesson.getName());
        lessonDescription.setText(lesson.getDescription());
        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicturesRecyclerView = findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonValidationActivity.this,lesson);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAudiosRecyclerView = findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonValidationActivity.this,lesson);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosRecyclerView = findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),LessonValidationActivity.this,lesson);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDocumentsRecyclerView = findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonValidationActivity.this,lesson);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);


        validateNameSwitch = findViewById(R.id.switch_lesson_name);
        validateDescriptionSwitch = findViewById(R.id.switch_lesson_description);
        validateImagesSwitch = findViewById(R.id.switch_lesson_images);
        validateVideosSwitch = findViewById(R.id.switch_lesson_video);
        validateAudiosSwitch = findViewById(R.id.switch_lesson_audios);
        validateDocumentsSwitch = findViewById(R.id.switch_lesson_documents);

        editCommentName = findViewById(R.id.edit_comment_name);
        editCommentDescription = findViewById(R.id.edit_comment_description);
        editCommentImages = findViewById(R.id.edit_comment_images);
        editCommentVideos = findViewById(R.id.edit_comment_videos);
        editCommentAudios = findViewById(R.id.edit_comment_audios);
        editCommentDocuments = findViewById(R.id.edit_comment_documents);

        switch_validateAudioStringText = findViewById(R.id.textView_switch_comment_lesson_audios);
        switch_validateVideoStringText = findViewById(R.id.textView_switch_comment_lesson_video);
        switch_validateDocumentStringText = findViewById(R.id.textView_switch_comment_lesson_documents);
        switch_validateImageStringText = findViewById(R.id.textView_switch_comment_lesson_images);

        textSaveValidatedLesson = findViewById(R.id.textView_save_validated_lesson);
        textSendValidatedLessonComments = findViewById(R.id.textView_send_validated_lesson_comments);
        fabValidateLesson = findViewById(R.id.fab_validate_lesson);
        fabCommentLeson = findViewById(R.id.fab_comment_lesson);
        fabCancel = findViewById(R.id.fab_reject_lesson);

        textImages = findViewById(R.id.text_images);
        textVideos = findViewById(R.id.text_videos);
        textAudios = findViewById(R.id.text_audios);
        textDocuments = findViewById(R.id.text_documents);

        validateNameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentName.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentName.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateDescriptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentDescription.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentDescription.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentImages.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentImages.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateVideosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentVideos.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentVideos.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateAudiosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentAudios.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentAudios.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateDocumentsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentDocuments.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentDocuments.setVisibility(View.GONE);
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
                        JsonElement jsonObject = jsonArray.get(i);
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
                            mPicturesRecyclerView.setVisibility(View.VISIBLE);
                            textImages.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_AUDIOS_PATH)) {
                            audioPathsList.add(fileKey);
                            validateAudiosSwitch.setVisibility(View.VISIBLE);
                            switch_validateAudioStringText.setVisibility(View.VISIBLE);
                            textAudios.setVisibility(View.VISIBLE);
                            mAudiosRecyclerView.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_DOCS_PATH)) {
                            documentPathsList.add(fileKey);
                            validateDocumentsSwitch.setVisibility(View.VISIBLE);
                            switch_validateDocumentStringText.setVisibility(View.VISIBLE);
                            textDocuments.setVisibility(View.VISIBLE);
                            mDocumentsRecyclerView.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_VIDEOS_PATH)) {
                            videosPathsList.add(fileKey);
                            validateVideosSwitch.setVisibility(View.VISIBLE);
                            textVideos.setVisibility(View.VISIBLE);
                            switch_validateVideoStringText.setVisibility(View.VISIBLE);
                            mVideosRecyclerView.setVisibility(View.VISIBLE);
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
                    multimediaVideoAdapter.notifyDataSetChanged();

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

        setFabValidateLessonListener();
        setFabCancelLessonListener();
        setFabCommentLessonListener();

    }

    public void setFabValidateLessonListener() {
        fabValidateLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                VolleyPutValidateLesson.volleyPutValidateLesson(new VolleyJSONCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(getApplicationContext(),"Validación realizada con éxito",Toast.LENGTH_LONG);
                        MainActivity.getIntent(LessonValidationActivity.this);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(getApplicationContext(),"No se pudo realizar la validación solicitada",Toast.LENGTH_LONG);
                    }
                }, LessonValidationActivity.this, lesson.getId());
            }
        });
    }

    public void setFabCommentLessonListener() {
        fabCommentLeson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = "COMENTARIOS POR SEGMENTO: \n";
                if (!editCommentName.getText().toString().isEmpty()) {
                    comment = comment + "NOMBRE: " + editCommentName.getText() + "\n";
                }
                if (!editCommentDescription.getText().toString().isEmpty()) {
                    comment = comment + "DESCRIPCIÓN: " + editCommentDescription.getText() + "\n";
                }
                if (!editCommentImages.getText().toString().isEmpty()) {
                    comment = comment + "IMÁGENES: " + editCommentImages.getText() + "\n";
                }
                if (!editCommentVideos.getText().toString().isEmpty()) {
                    comment = comment + "VIDEOS: " + editCommentVideos.getText() + "\n";
                }
                if (!editCommentAudios.getText().toString().isEmpty()) {
                    comment = comment + "AUDIOS: " + editCommentAudios.getText() + "\n";
                }
                if (!editCommentDocuments.getText().toString().isEmpty()) {
                    comment = comment + "DOCUMENTOS: " + editCommentDocuments.getText() + "\n";
                }

                VolleyPutRejectLesson.volleyPutRejectLesson(new VolleyJSONCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(LessonValidationActivity.this, "Se han enviado los comentarios", Toast.LENGTH_LONG).show();
                        startActivity(MainActivity.getIntent(LessonValidationActivity.this));
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(LessonValidationActivity.this, "No se han podido enviar los comentarios", Toast.LENGTH_LONG).show();
                    }
                }, LessonValidationActivity.this, lesson.getId(), comment);
            }
        });
    }

    public void setFabCancelLessonListener() {
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
    }


    public void checkedComments(){
        if (validateNameSwitch.isChecked() || validateDescriptionSwitch.isChecked() || validateImagesSwitch.isChecked() || validateVideosSwitch.isChecked() || validateAudiosSwitch.isChecked() || validateDocumentsSwitch.isChecked()){
            textSaveValidatedLesson.setVisibility(View.GONE);
            fabValidateLesson.setVisibility(View.GONE);
            textSendValidatedLessonComments.setVisibility(View.VISIBLE);
            fabCommentLeson.setVisibility(View.VISIBLE);
        }
        else {
            textSaveValidatedLesson.setVisibility(View.VISIBLE);
            fabValidateLesson.setVisibility(View.VISIBLE);
            textSendValidatedLessonComments.setVisibility(View.GONE);
            fabCommentLeson.setVisibility(View.GONE);
        }
    }

    public void setLesson() {
        lesson = new Lesson();
        lesson.setName(getIntent().getStringExtra(LESSON_NAME));
        lesson.setDescription(getIntent().getStringExtra(LESSON_DESCRIPTION));
        lesson.setId(getIntent().getStringExtra(LESSON_ID));
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
