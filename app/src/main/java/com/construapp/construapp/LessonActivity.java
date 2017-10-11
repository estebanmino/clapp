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
    private static final String PROJECT_FOLDER = "ConstruApp";

    private Lesson lesson = new Lesson();
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

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

        TextView nombre_proyecto = (TextView) findViewById(R.id.text_lesson_name);
        TextView descripcion_leccion = (TextView) findViewById(R.id.text_lesson_description);
        SharedPreferences spl = getSharedPreferences("Lesson", Context.MODE_PRIVATE);

        nombre_proyecto.setText(spl.getString("lesson_name", ""));
        descripcion_leccion.setText(spl.getString("lesson_description", ""));

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
        constants = new Constants();
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonActivity.this));
        transferUtility = new TransferUtility(s3, LessonActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = LessonActivity.this.getCacheDir().toString();

        String[] paths = {"PICTURE/1234.jpg", "PICTURE/1506577076.jpg"};
        for (String path: paths){
            lesson.getMultimediaPicturesFiles().add(new MultimediaFile(
                    "PICTURE",CACHE_FOLDER+"/"+path,path,transferUtility,"construapp"));
        }
        multimediaPictureAdapter.notifyDataSetChanged();

        String[] audioPaths = {"AUDIO/cache1506569807.3gp"};
        for (String audioPath: audioPaths) {
            MultimediaFile audioMultimedia = new MultimediaFile(
                    "AUDIO",CACHE_FOLDER+"/"+audioPath,audioPath,transferUtility,"construapp");
            lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        }
        multimediaAudioAdapter.notifyDataSetChanged();

        String[] documentPaths = {"DOCUMENT/The_Jungle_Book_T.pdf"};
        for (String documentPath: documentPaths) {
            MultimediaFile documentMultimedia = new MultimediaFile(
                    "DOCUMENT",ABSOLUTE_STORAGE_PATH+"/"+PROJECT_FOLDER+"/"+documentPath,documentPath,transferUtility,"construapp");
            lesson.getMultimediaDocumentsFiles().add(documentMultimedia);
        }
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
    }

    public static Intent getIntent(Context context, String name, String description) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(USERNAME,name);
        intent.putExtra(DESCRIPTION,description);
        //intent.putExtra(KEY_CHAT_ROOM_UUID,chatRoomUuid);

        return intent;
    }


}