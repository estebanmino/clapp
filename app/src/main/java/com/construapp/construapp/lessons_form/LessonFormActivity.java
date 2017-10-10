package com.construapp.construapp.lessons_form;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.MainActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.threading.RetrieveFeedTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LessonFormActivity extends AppCompatActivity {

    //CONSTANTS
    private static final int WRITE_EXTERNAL_REEQUEST = 1886;
    private static final int CAMERA_REQUEST = 1888;
    private static  final int CAMERA_REQUEST_PICTURE = 1887;
    private static final int SELECT_IMAGE = 1885;
    private static final int READ_EXTERNAL_REQUEST = 1884;
    private static final int RECORD_AUDIO_REQUEST = 1883;
    private static final int FILES_REQUEST = 1882;
    private static final String S3_BUCKET_NAME = "construapp";
    private static String ABSOLUTE_STORAGE_PATH;
    private static final String EXTENSION_PICTURE = "PICTURE";
    private static final String EXTENSION_DOCUMENT = "DOCUMENT";

    //XML ELEMENTS
    private TextView lessonName;
    private TextView  lessonDescription;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private FloatingActionButton fabRecordAudio;
    private FloatingActionButton fabFiles;
    private FloatingActionButton fabSend;
    private EditText editLessonName;
    private EditText editLessonDescription;


    //LOCAL VARIABLES
    private String mPath;
    private View mLayout;

    //FOR AUDIO RECORD
    ////record audio
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mRecordFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    boolean isRecording =  false;

    //AmazonS3
    private TransferUtility transferUtility;

    //NEW LESSON FOR FORM
    private Lesson lesson;

    //CONSTANTS
    private Constants constants;

    //MM ADAPTER
    MultimediaImageAdapter multimediaImagePictureAdapter;
    MultimediaImageAdapter multimediaImageAudioAdapter;
    MultimediaImageAdapter multimediaImageDocumentAdapter;


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

        //progressBarRecordAudio =  (ProgressBar) findViewById(R.id.progress_bar_record);

        //INIT NEW LESSON
        lesson = new Lesson();
        setLesson();
        //lessonName.setText(lesson.getName());
        //lessonDescription.setText(lesson.getDescription());
        lesson.initMultimediaFiles();

        //INIT CONSTANTS
        constants = new Constants();

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

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaImagePictureAdapter = new MultimediaImageAdapter(lesson.getMultimediaPicturesFiles(),LessonFormActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaImagePictureAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaImageAudioAdapter = new MultimediaImageAdapter(lesson.getMultimediaAudiosFiles(),LessonFormActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaImageAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaImageDocumentAdapter = new MultimediaImageAdapter(lesson.getMultimediaDocumentsFiles(),LessonFormActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaImageDocumentAdapter);
    }

    private void setFabSendOnClickListener(){
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO fix params
                SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
                String lesson_name = editLessonName.getText().toString();
                String lesson_summary = editLessonName.getText().toString();
                String lesson_motivation = "Aprendizaje";
                String lesson_learning = editLessonDescription.getText().toString();
                String token = sharedpreferences.getString("token", "");
                String user_id = sharedpreferences.getString("user_id", "");
                String company_id = sharedpreferences.getString("company_id", "");
                //TODO fijar proyecto
                String project_id = "2";
                String response = "";
                String lesson_id="";
                try {
                    RetrieveFeedTask r = new RetrieveFeedTask("send-lesson");
                    response = r.execute(lesson_name,lesson_summary,lesson_motivation,lesson_learning,token,user_id,company_id,project_id).get();
                }
                catch (InterruptedException e){}
                catch (ExecutionException e){}
                if(response != "error")
                {
                    lesson_id=response;
                    String path_input = "";
                    for (MultimediaFile multimediaFile: lesson.getMultimediaPicturesFiles()) {
                        path_input+=multimediaFile.getExtension()+"/"+ multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/")+1)+";";
                    }
                    for (MultimediaFile multimediaFile: lesson.getMultimediaAudiosFiles()) {
                        path_input+=multimediaFile.getExtension()+"/"+multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/")+1)+";";
                    }
                    for (MultimediaFile multimediaFile: lesson.getMultimediaDocumentsFiles()) {
                        path_input+=multimediaFile.getExtension()+"/"+multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/")+1)+";";
                    }

                    RetrieveFeedTask r2 = new RetrieveFeedTask("fetch-s3");
                    String response2 = "";
                    try {
                        response2 = r2.execute(company_id,lesson_id,path_input).get();
                    }
                    catch (InterruptedException e){}
                    catch (ExecutionException e) {}

                    if(response2 == "OK")
                    {
                        for (MultimediaFile multimediaFile: lesson.getMultimediaPicturesFiles()) {
                            multimediaFile.initUploadThread();
                        }
                        for (MultimediaFile multimediaFile: lesson.getMultimediaAudiosFiles()) {
                            multimediaFile.initUploadThread();
                        }
                        for (MultimediaFile multimediaFile: lesson.getMultimediaDocumentsFiles()) {
                            multimediaFile.initUploadThread();
                        }
                    }
                }
                Toast.makeText(LessonFormActivity.this, "Nueva lección creada", Toast.LENGTH_LONG).show();

                startActivity(MainActivity.getIntent(LessonFormActivity.this));

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
        fabRecordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    if (!isRecording) {
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();
                        MultimediaFile audioMultimedia = new MultimediaFile(
                                "AUDIO",ABSOLUTE_STORAGE_PATH+ts.toString()+".3gp",transferUtility,S3_BUCKET_NAME);
                        startRecording(audioMultimedia);
                        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
                    }
                } else {
                    getRecorAudioPermissions();
                }//progressBarRecordAudio.setVisibility(View.VISIBLE);

                return false;
            }
        });

        fabRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LessonFormActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    stopRecording();
                } else {}
                //progressBarRecordAudio.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setFabGalleryOnClickListener() {
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Acceso a galería", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

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
                    Log.i("PERMISSION", "Storage Permission");
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonFormActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    Log.i("PERMISSION", "Camera Permission");
                    getCameraPermissions();
                }
                else {
                    Log.i("PERMISSION", "Granted");
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {

        String APP_DIRECTORY = "ConstruApp";

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

                    lesson.getMultimediaPicturesFiles().add(new MultimediaFile(EXTENSION_PICTURE,mPath, transferUtility,S3_BUCKET_NAME));
                    multimediaImagePictureAdapter.notifyDataSetChanged();
                    break;

                case SELECT_IMAGE:

                    if (data != null)
                    {
                        mPath = getRealPathFromURI_API19(getApplicationContext(),data.getData());

                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(EXTENSION_PICTURE,mPath, transferUtility,S3_BUCKET_NAME));
                        multimediaImagePictureAdapter.notifyDataSetChanged();


                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Toast.makeText(LessonFormActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case FILES_REQUEST:
                    Uri selectedUri = data.getData();

                    lesson.getMultimediaDocumentsFiles().add(new MultimediaFile(EXTENSION_DOCUMENT,
                            getPath(LessonFormActivity.this, selectedUri), transferUtility,S3_BUCKET_NAME));
                    multimediaImageDocumentAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
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

    public void onPlay(boolean start, String recordFileName) {
        if (start) {
            startPlaying(recordFileName);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String recordFileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(recordFileName);
            Log.i("AUDIO RECORD SOURCE", recordFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording(MultimediaFile multimediaFile) {
        isRecording = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(multimediaFile.getmPath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopRecording() {
        isRecording = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        //lesson.getMultimediaAudiosFiles().add(new MultimediaFile("AUDIO",mRecordFileName, transferUtility, S3_BUCKET_NAME));
        multimediaImageAudioAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    // From here, code from https://github.com/iPaulPro/aFileChooser

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
