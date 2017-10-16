package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.threading.api.VolleyFetchLessonMultimedia;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;


public class LessonActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";
    private static final String ID = "id";
    private static final String PROJECT_FOLDER = "ConstruApp";

    private Lesson lesson = new Lesson();
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private int userPermission;

    //CONSTANTS
    private Constants constants;

    //AmazonS3
    private TransferUtility transferUtility;


    //MM ADAPTER
    MultimediaPictureAdapter multimediaPictureAdapter;
    MultimediaAudioAdapter multimediaAudioAdapter;
    MultimediaDocumentAdapter multimediaDocumentAdapter;

    private static String ABSOLUTE_STORAGE_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        constants = new Constants();
        userPermission = Integer.parseInt(sharedpreferences.getString("user_permission",""));


        final TextView lesson_name = (TextView) findViewById(R.id.text_lesson_name);
        final TextView lesson_description = (TextView) findViewById(R.id.text_lesson_description);
        SharedPreferences spl = getSharedPreferences("Lesson", Context.MODE_PRIVATE);

        lesson_name.setText(spl.getString("lesson_name", ""));
        lesson_description.setText(spl.getString("lesson_description", ""));


        setLesson();

        lesson_name.setText(lesson.getName());
        lesson_description.setText(lesson.getDescription());

        String company_id = sharedpreferences.getString("company_id", "");
        String user_token = sharedpreferences.getString("token", "");


        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonActivity.this));
        transferUtility = new TransferUtility(s3, LessonActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = LessonActivity.this.getCacheDir().toString();


        String[] fileKeyPaths = new String[100];

        VolleyFetchLessonMultimedia.volleyFetchLessonMultimedia(new VolleyCallback() {
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
                        if (fileKey.contains("PICTURE")){
                            picturePathsList.add(fileKey);
                        } else if (fileKey.contains("AUDIO")) {
                            audioPathsList.add(fileKey);
                        } else if (fileKey.contains("DOCUMENT")) {
                            documentPathsList.add(fileKey);
                        }
                    }

                    String[] pictureArray = picturePathsList.toArray(new String[0]);
                    for (String path: pictureArray){
                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(
                                "PICTURE",CACHE_FOLDER+"/"+path.replace("\"", ""),path.replace("\"", ""),transferUtility,"construapp"));
                    }
                    multimediaPictureAdapter.notifyDataSetChanged();

                    for (String audioPath: audioPathsList) {
                        MultimediaFile audioMultimedia = new MultimediaFile(
                                "AUDIO",CACHE_FOLDER+"/"+audioPath.replace("\"", ""),audioPath.replace("\"", ""),transferUtility,"construapp");
                        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
                    }
                    multimediaAudioAdapter.notifyDataSetChanged();

                    for (String documentPath: documentPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                "DOCUMENT",ABSOLUTE_STORAGE_PATH+"/"+PROJECT_FOLDER+"/"+documentPath.replace("\"", ""),
                                documentPath.replace("\"", ""),transferUtility,"construapp");
                        lesson.getMultimediaDocumentsFiles().add(documentMultimedia);
                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, LessonActivity.this, lesson.getId());
    }

    public void showInfo(View view) {
        Intent intent = new Intent(this, ShowInfo.class);
        TextView projectName = (TextView) findViewById(R.id.text_lesson_name);
        String message = projectName.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void setLesson() {
        lesson.setName(getIntent().getStringExtra(USERNAME));
        lesson.setDescription(getIntent().getStringExtra(DESCRIPTION));
        lesson.setId(getIntent().getStringExtra(ID));
        lesson.initMultimediaFiles();
        showPermissions();
    }

    public static Intent getIntent(Context context, String name, String description, String id) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(USERNAME,name);
        intent.putExtra(DESCRIPTION,description);
        intent.putExtra(ID,id);
        return intent;
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
        void onErrorResponse(VolleyError result);
    }

    public void showPermissions(){
        final TextView edit_lesson_label = (TextView) findViewById(R.id.text_edit_lesson);
        final TextView delete_lesson_label = (TextView) findViewById(R.id.text_delete_lesson);
        final ImageView edit_lesson_image = (ImageView) findViewById(R.id.image_edit_lesson);
        final ImageView delete_lesson_image = (ImageView) findViewById(R.id.image_delete_lesson);

        int editPermission = constants.xmlPermissionTagToInt(edit_lesson_image.getTag().toString());
        int deletePermission = constants.xmlPermissionTagToInt((delete_lesson_image.getTag().toString()));

        if (editPermission > userPermission){
            edit_lesson_image.setVisibility(View.GONE);
            edit_lesson_label.setVisibility(View.GONE);
        }
        if (deletePermission > userPermission){
            delete_lesson_image.setVisibility(View.GONE);
            delete_lesson_label.setVisibility(View.GONE);
        }
    }
}