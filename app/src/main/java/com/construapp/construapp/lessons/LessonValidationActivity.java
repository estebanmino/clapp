package com.construapp.construapp.lessons;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyPutRejectLesson;
import com.construapp.construapp.api.VolleyPutValidateLesson;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
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
    private Switch validateSummarySwitch;
    private Switch validateMotivationSwitch;
    private Switch validateLearningSwitch;
    private Switch validateImagesSwitch;
    private Switch validateVideosSwitch;
    private Switch validateAudiosSwitch;
    private Switch validateDocumentsSwitch;

    private EditText editCommentName;
    private EditText editCommentSummary;
    private EditText editCommentMotivation;
    private EditText editCommentLearning;
    private EditText editCommentPictures;
    private EditText editCommentVideos;
    private EditText editCommentAudios;
    private EditText editCommentDocuments;
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

    private LinearLayout linearLayoutMultimedia;

    private LinearLayout linearLayoutPictures;
    private LinearLayout linearLayoutAudios;
    private LinearLayout linearLayoutVideos;
    private LinearLayout linearLayoutDocuments;

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

        linearLayoutMultimedia = findViewById(R.id.linear_layout_multimedia);
        linearLayoutPictures = findViewById(R.id.linear_layout_pictures);
        linearLayoutAudios = findViewById(R.id.linear_layout_audios);
        linearLayoutVideos = findViewById(R.id.linear_layout_videos);
        linearLayoutDocuments = findViewById(R.id.linear_layout_documents);

        textLessonName = findViewById(R.id.text_lesson_name);
        textLessonSummary = findViewById(R.id.text_lesson_summary);
        textLessonMotivation = findViewById(R.id.text_lesson_motivation);
        textLessonLearning = findViewById(R.id.text_lesson_learning);

        setLesson();

        textLessonName.setText(lesson.getName());
        textLessonSummary.setText(lesson.getSummary());
        textLessonMotivation.setText(lesson.getMotivation());
        textLessonLearning.setText(lesson.getLearning());
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
        validateSummarySwitch = findViewById(R.id.switch_lesson_summary);
        validateMotivationSwitch = findViewById(R.id.switch_lesson_motivation);
        validateLearningSwitch = findViewById(R.id.switch_lesson_learning);
        validateImagesSwitch = findViewById(R.id.switch_lesson_images);
        validateVideosSwitch = findViewById(R.id.switch_lesson_videos);
        validateAudiosSwitch = findViewById(R.id.switch_lesson_audios);
        validateDocumentsSwitch = findViewById(R.id.switch_lesson_documents);

        validateNameSwitch.setChecked(true);
        validateSummarySwitch.setChecked(true);
        validateMotivationSwitch.setChecked(true);
        validateLearningSwitch.setChecked(true);
        validateImagesSwitch.setChecked(true);
        validateVideosSwitch.setChecked(true);
        validateAudiosSwitch.setChecked(true);
        validateDocumentsSwitch.setChecked(true);

        editCommentName = findViewById(R.id.edit_comment_name);
        editCommentSummary = findViewById(R.id.edit_comment_summary);
        editCommentMotivation = findViewById(R.id.edit_comment_motivation);
        editCommentLearning = findViewById(R.id.edit_comment_learning);
        editCommentPictures = findViewById(R.id.edit_comment_images);
        editCommentVideos = findViewById(R.id.edit_comment_videos);
        editCommentAudios = findViewById(R.id.edit_comment_audios);
        editCommentDocuments = findViewById(R.id.edit_comment_documents);

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

        validateSummarySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentSummary.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentSummary.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateMotivationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentMotivation.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentMotivation.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateLearningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentLearning.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentLearning.setVisibility(View.GONE);
                }
                checkedComments();
            }
        });

        validateImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editCommentPictures.setVisibility(View.VISIBLE);
                }
                else {
                    editCommentPictures.setVisibility(View.GONE);
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
                        } else if (fileKey.contains(Constants.S3_AUDIOS_PATH)) {
                            audioPathsList.add(fileKey);
                        } else if (fileKey.contains(Constants.S3_DOCS_PATH)) {
                            documentPathsList.add(fileKey);
                        } else if (fileKey.contains(Constants.S3_VIDEOS_PATH)) {
                            videosPathsList.add(fileKey);
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


                    if (!lesson.hasMultimediaFiles()) {
                        linearLayoutMultimedia.setVisibility(View.GONE);
                        validateImagesSwitch.setChecked(false);
                        validateAudiosSwitch.setChecked(false);
                        validateVideosSwitch.setChecked(false);
                        validateDocumentsSwitch.setChecked(false);
                    } else {
                        if (lesson.getMultimediaPicturesFiles().size() == 0){
                            validateImagesSwitch.setChecked(false);
                            linearLayoutPictures.setVisibility(View.GONE);
                            mPicturesRecyclerView.setVisibility(View.GONE);
                            editCommentPictures.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaAudiosFiles().size() == 0){
                            validateAudiosSwitch.setChecked(false);
                            linearLayoutAudios.setVisibility(View.GONE);
                            mAudiosRecyclerView.setVisibility(View.GONE);
                            editCommentAudios.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaVideosFiles().size() == 0){
                            validateVideosSwitch.setChecked(false);
                            linearLayoutVideos.setVisibility(View.GONE);
                            mVideosRecyclerView.setVisibility(View.GONE);
                            editCommentVideos.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaDocumentsFiles().size() == 0){
                            validateDocumentsSwitch.setChecked(false);
                            linearLayoutDocuments.setVisibility(View.GONE);
                            mDocumentsRecyclerView.setVisibility(View.GONE);
                            editCommentDocuments.setVisibility(View.GONE);
                        }
                    }
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
                if (!editCommentSummary.getText().toString().isEmpty()) {
                    comment = comment + "RESUMEN: " + editCommentSummary.getText() + "\n";
                }
                if (!editCommentMotivation.getText().toString().isEmpty()) {
                    comment = comment + "MOTIVACIÓN: " + editCommentMotivation.getText() + "\n";
                }
                if (!editCommentLearning.getText().toString().isEmpty()) {
                    comment = comment + "APRENDIZAJE: " + editCommentLearning.getText() + "\n";
                }
                if (!editCommentPictures.getText().toString().isEmpty()) {
                    comment = comment + "IMÁGENES: " + editCommentPictures.getText() + "\n";
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
        if (validateNameSwitch.isChecked() || validateSummarySwitch.isChecked() ||
                validateMotivationSwitch.isChecked() || validateLearningSwitch.isChecked() ||
                validateImagesSwitch.isChecked() || validateVideosSwitch.isChecked() ||
                validateAudiosSwitch.isChecked() || validateDocumentsSwitch.isChecked()){
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
        try {
            lesson = new GetLessonTask(LessonValidationActivity.this,getIntent().getStringExtra(LESSON_ID)).execute().get();
            lesson.initMultimediaFiles();

        } catch (Exception e) {}
    }

    public static Intent getIntent(Context context, String name, String description, String id) {
        Intent intent = new Intent(context,LessonValidationActivity.class);
        intent.putExtra(LESSON_NAME,name);
        intent.putExtra(LESSON_DESCRIPTION,description);
        intent.putExtra(LESSON_ID,id);
        return intent;
    }
}