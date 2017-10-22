package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.dbTasks.DeleteLessonTask;
import com.construapp.construapp.api.VolleyDeleteLesson;
import com.construapp.construapp.api.VolleyFetchLessonMultimedia;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ValidateLessonActivity extends AppCompatActivity {


    private static final String LESSON_NAME = "username";
    private static final String LESSON_DESCRIPTION = "description";
    private static final String LESSON_ID = "id";
    private static final String PROJECT_FOLDER = "ConstruApp";

    private Lesson lesson = new Lesson();
    private int userPermission;

    //CONSTANTS
    private General constants;

    //AmazonS3
    private TransferUtility transferUtility;


    //MM ADAPTER
    MultimediaPictureAdapter multimediaPictureAdapter;
    MultimediaAudioAdapter multimediaAudioAdapter;
    MultimediaDocumentAdapter multimediaDocumentAdapter;

    private static String ABSOLUTE_STORAGE_PATH;

    ImageView imageEditLesson;
    ImageView imageDeleteLesson;
    TextView textEditLesson;
    TextView textDeleteLesson;
    TextView textLessonName;
    TextView textLessonDescription;
    TextView textSaveValidatedLesson;
    TextView textSendValidatedLessonComments;
    Switch validateNameSwitch;
    Switch validateDescriptionSwitch;
    Switch validateImagesSwitch;
    Switch validateAudiosSwitch;
    Switch validateDocumentsSwitch;
    EditText validateNameComment;
    EditText validateDescriptionComment;
    EditText validateImagesComment;
    EditText validateAudiosComment;
    EditText validateDocumentsComment;
    FloatingActionButton buttonSaveValidatedLesson;
    FloatingActionButton buttonSendValidatedLessonComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        constants = new General();
        userPermission = Integer.parseInt(sharedpreferences.getString(Constants.SP_USER_PERMISSION,""));

        imageEditLesson = (ImageView) findViewById(R.id.image_edit_lesson);
        imageDeleteLesson = (ImageView) findViewById(R.id.image_delete_lesson);
        textEditLesson = (TextView) findViewById(R.id.text_edit_lesson);
        textDeleteLesson = (TextView) findViewById(R.id.text_delete_lesson);
        textLessonName = (TextView) findViewById(R.id.text_lesson_name);
        textLessonDescription = (TextView) findViewById(R.id.text_lesson_description);

        validateNameSwitch = (Switch) findViewById(R.id.switch_lesson_name);
        validateDescriptionSwitch = (Switch) findViewById(R.id.switch_lesson_description);
        validateImagesSwitch = (Switch) findViewById(R.id.switch_lesson_images);
        validateAudiosSwitch = (Switch) findViewById(R.id.switch_lesson_audios);
        validateDocumentsSwitch = (Switch) findViewById(R.id.switch_lesson_documents);

        validateNameComment = (EditText) findViewById(R.id.editText_validate_name_comment);
        validateDescriptionComment = (EditText) findViewById(R.id.editText_validate_description_comment);
        validateImagesComment = (EditText) findViewById(R.id.editText_validate_images_comment);
        validateAudiosComment = (EditText) findViewById(R.id.editText_validate_audios_comment);
        validateDocumentsComment = (EditText) findViewById(R.id.editText_validate_documents_comment);

        textSaveValidatedLesson = (TextView) findViewById(R.id.textView_save_validated_lesson);
        textSendValidatedLessonComments = (TextView) findViewById(R.id.textView_send_validated_lesson_comments);
        buttonSaveValidatedLesson = (FloatingActionButton) findViewById(R.id.floatingActionButton_save_validated_lesson);
        buttonSendValidatedLessonComments = (FloatingActionButton) findViewById(R.id.floatingActionButton_send_validated_lesson_comments);

        setLesson();

        textLessonName.setText(lesson.getName());
        textLessonDescription.setText(lesson.getDescription());

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

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),ValidateLessonActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),ValidateLessonActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),ValidateLessonActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(ValidateLessonActivity.this));
        transferUtility = new TransferUtility(s3, ValidateLessonActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = ValidateLessonActivity.this.getCacheDir().toString();

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

                    for (String fileKey: arrayList) {
                        if (fileKey.contains(Constants.S3_IMAGES_PATH)){
                            picturePathsList.add(fileKey);
                        } else if (fileKey.contains(Constants.S3_AUDIOS_PATH)) {
                            audioPathsList.add(fileKey);
                        } else if (fileKey.contains(Constants.S3_DOCS_PATH)) {
                            documentPathsList.add(fileKey);
                        }
                    }

                    String[] pictureArray = picturePathsList.toArray(new String[0]);
                    for (String path: pictureArray){
                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_IMAGES_PATH,CACHE_FOLDER+"/"+path.substring(path.lastIndexOf("/")+1),path.replace("\"", ""),transferUtility));
                    }
                    multimediaPictureAdapter.notifyDataSetChanged();

                    for (String audioPath: audioPathsList) {
                        MultimediaFile audioMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_AUDIOS_PATH,CACHE_FOLDER+"/"+audioPath.substring(audioPath.lastIndexOf("/")+1),audioPath.replace("\"", ""),transferUtility);
                        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
                    }
                    multimediaAudioAdapter.notifyDataSetChanged();

                    for (String documentPath: documentPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_DOCS_PATH,ABSOLUTE_STORAGE_PATH+"/"+PROJECT_FOLDER+"/"+documentPath.substring(documentPath.lastIndexOf("/")+1),
                                documentPath.replace("\"", ""),transferUtility);
                        lesson.getMultimediaDocumentsFiles().add(documentMultimedia);
                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, ValidateLessonActivity.this, lesson.getId());

        setImageDeleteLessonListener();
        setImageEditLessonListener();
    }

    public void setImageDeleteLessonListener() {
        imageDeleteLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete

                //TODO implementar cola eliminacion
                if(Connectivity.isConnected(getApplicationContext())) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.lesson_form_layout),
                            "Confirme la eliminacion de lección", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("Confirmar", new ValidateLessonActivity.DeleteListener());
                    mySnackbar.show();
                }
                //if not connected
                else
                {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.lesson_form_layout),
                            "No se puede eliminar una lección estando sin conexión.", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("Confirmar",null);
                    mySnackbar.show();
                    //Toast.makeText(getApplicationContext(),"No se puede eliminar una lección estando sin conexión.",Toast.LENGTH_LONG);
                    //startActivity(MainActivity.getIntent(LessonActivity.this));
                }
            }
        });
    }

    public void setImageEditLessonListener() {
        imageEditLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edit
            }
        });
    }

    public void showInfo(View view) {
        Intent intent = new Intent(this, ShowInfo.class);
        TextView projectName = (TextView) findViewById(R.id.text_lesson_name);
        String message = projectName.getText().toString();
        startActivity(intent);
    }

    public void checkedComments(){
        if (validateNameSwitch.isChecked() || validateDescriptionSwitch.isChecked() || validateImagesSwitch.isChecked() || validateAudiosSwitch.isChecked() || validateDocumentsSwitch.isChecked()){
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
        lesson.setName(getIntent().getStringExtra(LESSON_NAME));
        lesson.setDescription(getIntent().getStringExtra(LESSON_DESCRIPTION));
        lesson.setId(getIntent().getStringExtra(LESSON_ID));
        lesson.initMultimediaFiles();
        showPermissions();
    }

    public static Intent getIntent(Context context, String name, String description, String id) {
        Intent intent = new Intent(context,ValidateLessonActivity.class);
        intent.putExtra(LESSON_NAME,name);
        intent.putExtra(LESSON_DESCRIPTION,description);
        intent.putExtra(LESSON_ID,id);
        return intent;
    }

    public void showPermissions(){


        int editPermission = constants.xmlPermissionTagToInt(imageEditLesson.getTag().toString());
        int deletePermission = constants.xmlPermissionTagToInt((imageDeleteLesson.getTag().toString()));


        if (editPermission > userPermission){
            imageEditLesson.setVisibility(View.GONE);
            textEditLesson.setVisibility(View.GONE);
        }
        if (deletePermission > userPermission){
            imageDeleteLesson.setVisibility(View.GONE);
            textDeleteLesson.setVisibility(View.GONE);
        }
    }

    public class DeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i("DELETE","Lesson deleted");
            try {
                //Delete the lesson from DB
                new DeleteLessonTask(lesson,getApplicationContext()).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            VolleyDeleteLesson.volleyDeleteLesson(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(ValidateLessonActivity.this, "Eliminada correctamente", Toast.LENGTH_LONG).show();
                    startActivity(MainActivity.getIntent(ValidateLessonActivity.this));
                }

                @Override
                public void onErrorResponse(VolleyError result) {
                    Toast.makeText(ValidateLessonActivity.this, "Ocurrio un error, por favor reintentar", Toast.LENGTH_LONG).show();
                }
            }, ValidateLessonActivity.this, lesson.getId());
        }
    }

}