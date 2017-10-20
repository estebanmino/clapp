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
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.construapp.construapp.threading.api.VolleyCreateLesson;
import com.construapp.construapp.threading.api.VolleyPostS3;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class LessonFormActivity extends AppCompatActivity {

    //CONSTANTS
    private static final int WRITE_EXTERNAL_REEQUEST = 1886;
    private static final int CAMERA_REQUEST = 1888;
    private static  final int CAMERA_REQUEST_PICTURE = 1887;
    private static final int SELECT_IMAGE = 1885;
    private static final int READ_EXTERNAL_REQUEST = 1884;
    private static final int RECORD_AUDIO_REQUEST = 1883;
    private static final int FILES_REQUEST = 1882;
    private static final int CAMERA_REQUEST_FOR_VIDEO = 1880;


    private static String ABSOLUTE_STORAGE_PATH;
    private static final String EXTENSION_AUDIO_FORMAT = ".3gp";
    private static final String VIDEO_FORMAT = ".mp4";
    private static String APP_DIRECTORY = "ConstruApp";


    //XML ELEMENTS
    private TextView lessonName;
    private TextView  lessonDescription;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private FloatingActionButton fabRecordAudio;
    private FloatingActionButton fabFiles;
    private FloatingActionButton fabSend;
    private FloatingActionButton fabVideo;
    private EditText editLessonName;
    private EditText editLessonDescription;
    private TextView textRecording;

    //LOCAL VARIABLES
    private String mPath;
    private View mLayout;

    //FOR AUDIO RECORD
    ////record audio
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mRecordFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean mStartRecording;
    boolean mStartPlaying = true;
    boolean isRecording =  false;

    //AmazonS3
    private TransferUtility transferUtility;

    //NEW LESSON FOR FORM
    private Lesson lesson;

    //CONSTANTS
    private General constants;

    //MM ADAPTER
    MultimediaPictureAdapter multimediaPictureAdapter;
    MultimediaVideoAdapter multimediaVideoAdapter;
    MultimediaAudioAdapter multimediaAudioAdapter;
    MultimediaDocumentAdapter multimediaDocumentAdapter;


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

        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabRecordAudio = (FloatingActionButton) findViewById(R.id.fab_record_audio);
        fabSend = (FloatingActionButton) findViewById(R.id.fab_send);
        fabFiles = (FloatingActionButton) findViewById(R.id.fab_files);
        fabVideo = (FloatingActionButton) findViewById(R.id.fab_video);
        textRecording = (TextView) findViewById(R.id.text_recording);

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

    private void setFabSendOnClickListener(){
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO fix params, working with sharedpreferences
                SharedPreferences sharedpreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
                String lesson_name = editLessonName.getText().toString();
                String lesson_summary = editLessonName.getText().toString();
                String lesson_motivation = "Aprendizaje";
                String lesson_learning = editLessonDescription.getText().toString();
                //TODO FIJAR PROYECTO CUANDO EXISTA
                String project_id = sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT,"");

                VolleyCreateLesson.volleyCreateLesson(new VolleyCallback() {
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

                            VolleyPostS3.volleyPostS3(new VolleyCallback() {
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

    public void setFabFilesOnClickListener(){
        fabFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(ABSOLUTE_STORAGE_PATH); // a directory
                intent.setDataAndType(uri, "*/*");
                startActivityForResult(Intent.createChooser(intent, "Open"), FILES_REQUEST);
            }
        });
    }

    public void setFabRecordAudioOnClickListener() {
        //
        fabRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(mStartRecording);
                    if (mStartRecording == true) {
                        textRecording.setVisibility(View.VISIBLE);
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_NORMAL);
                        fabFiles.setVisibility(View.GONE);
                        fabGallery.setVisibility(View.GONE);
                        fabCamera.setVisibility(View.GONE);
                        fabSend.setVisibility(View.GONE);
                    }
                    else{
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_MINI);
                        textRecording.setVisibility(View.GONE);
                        fabFiles.setVisibility(View.VISIBLE);
                        fabGallery.setVisibility(View.VISIBLE);
                        fabCamera.setVisibility(View.VISIBLE);
                        fabSend.setVisibility(View.VISIBLE);
                    }
                    mStartRecording = !mStartRecording;

                } else {
                    getRecorAudioPermissions();
                }
            }
        });
    }

    public void setFabGalleryOnClickListener() {
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    getReadStoragePermissions();
                }
                else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
                }
            }
        });
    }

    public void setFabCameraOnClickListener() {
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    getCameraPermissions();
                }
                else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    public void setFabVideoOnClickListener() {
        fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    getCameraPermissions();
                }
                else {
                    dispatchRecordVideoIntent();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), APP_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdir();
        }
        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + ".jpg";

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                File.separator +
                APP_DIRECTORY + File.separator + imageName;
        File newFile = new File(file, imageName);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_PICTURE);
    }

    private void dispatchRecordVideoIntent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), APP_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            file.mkdir();
        }
        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + VIDEO_FORMAT;

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                File.separator +
                APP_DIRECTORY + File.separator + imageName;
        File newFile = new File(file, imageName);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, CAMERA_REQUEST_FOR_VIDEO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                        dispatchTakePictureIntent();
                    }
                }   else {
                    Toast.makeText(this, "Debes dar permiso para tomar fotos", Toast.LENGTH_LONG).show();
                }
                return;

            case WRITE_EXTERNAL_REEQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCameraPermissions();
                } else {
                    Toast.makeText(this, "Debes dar permiso para poder seleccionar foto",
                            Toast.LENGTH_LONG).show();
                }
                break;


            case READ_EXTERNAL_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);

                } else {
                    Toast.makeText(this, "Debes dar permiso para poder seleccionar archivos",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case CAMERA_REQUEST_FOR_VIDEO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == 0) {
                        dispatchRecordVideoIntent();
                    }
                }   else {
                    Toast.makeText(this, "Debes dar permiso para grabar videos", Toast.LENGTH_LONG).show();
                }
                break;


            case RECORD_AUDIO_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Diste permiso para grabar audio",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Debes dar permiso para grabar audio",
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case CAMERA_REQUEST_PICTURE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "scanned"+path+":");
                                    Log.i("ExternalStorage", "-> Uri"+uri);
                                }
                            });

                    lesson.getMultimediaPicturesFiles().add(new MultimediaFile(Constants.S3_IMAGES_PATH,mPath, null,transferUtility));
                    multimediaPictureAdapter.notifyDataSetChanged();
                    break;

                case CAMERA_REQUEST_FOR_VIDEO:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "scanned"+path+":");
                                    Log.i("ExternalStorage", "-> Uri"+uri);
                                }
                            });
                    //Uri videoUri = data.getData();
                    //mPath = filesHandler.getPath(ChatRoomActivity.this, videoUri);
                    //multimedia = "videos/"+mPath.substring(mPath.lastIndexOf("/") + 1);
                    //imageAttachment.setImageDrawable(ContextCompat.getDrawable(ChatRoomActivity.this, R.drawable.ic_play_video));
                    //imageAttachment.setVisibility(View.VISIBLE);

                    lesson.getMultimediaVideosFiles().add(new MultimediaFile(Constants.S3_VIDEOS_PATH,mPath, null,transferUtility));
                    multimediaVideoAdapter.notifyDataSetChanged();

                    break;

                case SELECT_IMAGE:

                    if (data != null)
                    {
                        if (Build.VERSION.SDK_INT < 19) {
                            mPath = RealPathUtil.getRealPathFromURI_API11to18(getApplicationContext(), data.getData());
                        } else {
                            mPath = RealPathUtil.getRealPathFromURI_API19(getApplicationContext(), data.getData());
                        }

                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(Constants.S3_IMAGES_PATH,mPath, null,transferUtility));
                        multimediaPictureAdapter.notifyDataSetChanged();


                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Toast.makeText(LessonFormActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case FILES_REQUEST:
                    if (Build.VERSION.SDK_INT < 19) {
                        mPath = RealPathUtil.getRealPathFromURI_API11to18(getApplicationContext(), data.getData());
                    } else {
                        mPath = RealPathUtil.getRealPathFromURI_API19(getApplicationContext(), data.getData());
                    }
                    lesson.getMultimediaDocumentsFiles().add(new MultimediaFile(Constants.S3_DOCS_PATH,
                            mPath,null, transferUtility));
                    multimediaDocumentAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }

    public void setLesson() {
        //lesson.setName("Nueva leccion");
        //lesson.setDescription("Agregar form con datos");
        lesson.initMultimediaFiles();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,LessonFormActivity.class);
        //intent.putExtra(KEY_CHAT_ROOM_UUID,chatRoomUuid);

        return intent;
    }

    public void getReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonFormActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que se vean las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonFormActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_REQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonFormActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonFormActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos guardar las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonFormActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_REEQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonFormActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REEQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonFormActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos tomar las fotos, necesitamos acceso a su cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonFormActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonFormActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getRecorAudioPermissions() {
        if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonFormActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos grabar audio, necesitamos permiso.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonFormActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                RECORD_AUDIO_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonFormActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //RECORD AUDIO
    private void onRecord(boolean start) {
        if (start) {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            MultimediaFile audioMultimedia = new MultimediaFile(
                    Constants.S3_AUDIOS_PATH, ABSOLUTE_STORAGE_PATH + ts.toString() + EXTENSION_AUDIO_FORMAT, null,transferUtility);
            startRecording(audioMultimedia);
            lesson.getMultimediaAudiosFiles().add(audioMultimedia);
        } else {
            stopRecording();
        }
    }

    private void startRecording(MultimediaFile multimediaFile) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(multimediaFile.getmPath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
        }
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        multimediaAudioAdapter.notifyDataSetChanged();
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
        void onErrorResponse(VolleyError result);
    }
}
