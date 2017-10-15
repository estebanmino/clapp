package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;


public class LessonActivity extends AppCompatActivity {

    private static final String USERNAME = "username";
    private static final String DESCRIPTION = "description";

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
        constants = new Constants();
        userPermission = constants.getUserPermission();

        final TextView lesson_name = (TextView) findViewById(R.id.text_lesson_name);
        final TextView lesson_description = (TextView) findViewById(R.id.text_lesson_description);
        SharedPreferences spl = getSharedPreferences("Lesson", Context.MODE_PRIVATE);

        lesson_name.setText(spl.getString("lesson_name", ""));
        lesson_description.setText(spl.getString("lesson_description", ""));

        setLesson();

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

        MultimediaFile pictureMultimedia = new MultimediaFile(
                "PICTURE",ABSOLUTE_STORAGE_PATH+"/ConstruApp/1234.jpg",transferUtility,"construapp");

        MultimediaFile audioMultimedia = new MultimediaFile(
                "AUDIO",ABSOLUTE_STORAGE_PATH+"/ConstruApp/1234.jpg",transferUtility,"construapp");

        MultimediaFile documentMultimedia1 = new MultimediaFile("DOCUMENT",ABSOLUTE_STORAGE_PATH+"/ConstruApp/Planos.pdf",transferUtility,"construapp");
        MultimediaFile documentMultimedia2 = new MultimediaFile("DOCUMENT",ABSOLUTE_STORAGE_PATH+"/ConstruApp/Horarios.pdf",transferUtility,"construapp");
        MultimediaFile documentMultimedia3 = new MultimediaFile("DOCUMENT",ABSOLUTE_STORAGE_PATH+"/ConstruApp/Personal.pdf",transferUtility,"construapp");
        MultimediaFile documentMultimedia4 = new MultimediaFile("DOCUMENT",ABSOLUTE_STORAGE_PATH+"/ConstruApp/Planificación.pdf",transferUtility,"construapp");

        lesson.getMultimediaPicturesFiles().add(pictureMultimedia);
        lesson.getMultimediaPicturesFiles().add(pictureMultimedia);
        lesson.getMultimediaPicturesFiles().add(pictureMultimedia);
        lesson.getMultimediaPicturesFiles().add(pictureMultimedia);
        lesson.getMultimediaPicturesFiles().add(pictureMultimedia);
        multimediaPictureAdapter.notifyDataSetChanged();

        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        multimediaAudioAdapter.notifyDataSetChanged();

        lesson.getMultimediaDocumentsFiles().add(documentMultimedia1);
        lesson.getMultimediaDocumentsFiles().add(documentMultimedia2);
        lesson.getMultimediaDocumentsFiles().add(documentMultimedia3);
        lesson.getMultimediaDocumentsFiles().add(documentMultimedia4);
        multimediaDocumentAdapter.notifyDataSetChanged();

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
        lesson.initMultimediaFiles();
        showPermissions();
    }

    public static Intent getIntent(Context context, String name, String description) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(USERNAME,name);
        intent.putExtra(DESCRIPTION,description);
        //intent.putExtra(KEY_CHAT_ROOM_UUID,chatRoomUuid);

        return intent;
    }

    public void showPermissions(){
        final TextView edit_lesson_label = (TextView) findViewById(R.id.text_edit_lesson);
        final TextView delete_lesson_label = (TextView) findViewById(R.id.text_delete_lesson);
        final ImageView edit_lesson_image = (ImageView) findViewById(R.id.image_edit_lesson);
        final ImageView delete_lesson_image = (ImageView) findViewById(R.id.image_delete_lesson);

        int userPermission = constants.getUserPermission();
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