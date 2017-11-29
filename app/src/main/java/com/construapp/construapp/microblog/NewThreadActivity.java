package com.construapp.construapp.microblog;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeleteThread;
import com.construapp.construapp.api.VolleyGetThread;
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.api.VolleyPostS3;
import com.construapp.construapp.api.VolleyPostThread;
import com.construapp.construapp.api.VolleyPutThread;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.lessons.LessonFormActivity;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.models.Post;
import com.construapp.construapp.models.SessionManager;
import com.construapp.construapp.models.ThreadBlog;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.construapp.construapp.utils.RealPathUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NewThreadActivity extends Activity {


    //CONSTANTS
    public static final int WRITE_EXTERNAL_REEQUEST = 1886;
    public static final int CAMERA_REQUEST = 1888;
    public static  final int CAMERA_REQUEST_PICTURE = 1887;
    public static final int SELECT_IMAGE = 1885;
    public static final int READ_EXTERNAL_REQUEST = 1884;
    public static final int RECORD_AUDIO_REQUEST = 1883;
    public static final int FILES_REQUEST = 1882;
    public static final int CAMERA_REQUEST_FOR_VIDEO = 1880;

    //LOCAL VARIABLES
    public String mPath;
    public View mLayout;

    //FOR AUDIO RECORD
    ////record audio
    public static final String LOG_TAG = "AudioRecordTest";
    public static String mRecordFileName = null;
    public MediaRecorder mRecorder = null;
    public MediaPlayer mPlayer = null;
    public boolean mStartRecording = true;
    public boolean mStartPlaying = true;
    public boolean isRecording =  false;


    public static String ABSOLUTE_STORAGE_PATH;
    public static final String EXTENSION_AUDIO_FORMAT = ".3gp";
    public static final String VIDEO_FORMAT = ".mp4";

    private FloatingActionButton fabCreateThread;
    private FloatingActionButton fabAttach;
    private FloatingActionButton fabFiles;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private FloatingActionButton fabRecordAudio;
    private FloatingActionButton fabVideo;


    private TextView textImages;
    private TextView textVideos;
    private TextView textAudios;
    private TextView textDocuments;

    private TextView textRecording;

    private EditText editTitle;
    private EditText editDescription;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private Boolean multimediaIsOpen = false;

    private ConstraintLayout constraintMultimediaBar;
    private ThreadBlog threadBlog;

    private RecyclerView mPicturesRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mDocumentsRecyclerView;
    private RecyclerView mAudiosRecyclerView;

    private TransferUtility transferUtility;

    public MultimediaPictureAdapter multimediaPictureAdapter;
    public MultimediaVideoAdapter multimediaVideoAdapter;
    public MultimediaAudioAdapter multimediaAudioAdapter;
    public MultimediaDocumentAdapter multimediaDocumentAdapter;

    private int notAdded = 0;
    private int added = 1;
    private String CACHE_FOLDER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nuevo Post");

        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();
        CACHE_FOLDER = NewThreadActivity.this.getCacheDir().toString();

        fabCreateThread = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        fabAttach = findViewById(R.id.fab_attach);
        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        fabFiles = findViewById(R.id.fab_files);
        fabVideo = findViewById(R.id.fab_video);


        editTitle = findViewById(R.id.new_thread_name);
        editDescription = findViewById(R.id.new_thread_description);
        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);

        textImages = findViewById(R.id.text_images);
        textVideos = findViewById(R.id.text_videos);
        textAudios = findViewById(R.id.text_audios);
        textDocuments = findViewById(R.id.text_documents);
        textRecording = findViewById(R.id.text_recording);

        sessionManager = new SessionManager(NewThreadActivity.this);

        threadBlog = new ThreadBlog();
        threadBlog.initMultimediaFiles();


        // Create an S3 client
        General general = new General();
        AmazonS3 s3 = new AmazonS3Client(general.getCredentialsProvider(NewThreadActivity.this));
        transferUtility = new TransferUtility(s3, NewThreadActivity.this);

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicturesRecyclerView = findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(threadBlog.getMultimediaPictureFiles(),
                NewThreadActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosRecyclerView = findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(threadBlog.getMultimediaVideosFiles(),
                NewThreadActivity.this);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAudiosRecyclerView = findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(threadBlog.getMultimediaAudioFiles(),
                NewThreadActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDocumentsRecyclerView = findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(threadBlog.getMultimediaDocumentsFiles(),
                NewThreadActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);



        setFabCreateThreadListener();
        setfabttAchListener();
        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        setFabRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setFabVideoOnClickListener();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,NewThreadActivity.class);
        return intent;
    }

    public void setFabCreateThreadListener(){
        fabCreateThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            threadBlog.setSavedMultimediaFileKeys();
            VolleyPostThread.volleyPostThread(new VolleyJSONCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.i("NEWTHREAD",result.toString());

                    if(!threadBlog.isEmptyMultimedia()) {

                        ArrayList<MultimediaFile> selectedMultimediaFiles = new ArrayList<>();
                        selectedMultimediaFiles.addAll(multimediaPictureAdapter.getmMultimediaFiles());
                        selectedMultimediaFiles.addAll(multimediaAudioAdapter.getmMultimediaFiles());
                        selectedMultimediaFiles.addAll(multimediaDocumentAdapter.getmMultimediaFiles());
                        selectedMultimediaFiles.addAll(multimediaVideoAdapter.getmMultimediaFiles());

                        try {
                            final String new_thread_blog_id = result.get("id").toString();
                            String path_input = threadBlog.getMultimediaFileKeys(new_thread_blog_id);

                            VolleyPutThread.volleyPutThread(new VolleyStringCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    Toast.makeText(NewThreadActivity.this, "Nuevo post creado", Toast.LENGTH_LONG).show();

                                    for (MultimediaFile multimediaFile : threadBlog.getMultimediaPictureFiles()) {
                                        multimediaFile.initUploadThread();
                                    }
                                    for (MultimediaFile multimediaFile : threadBlog.getMultimediaAudioFiles()) {
                                        multimediaFile.initUploadThread();
                                    }
                                    for (MultimediaFile multimediaFile : threadBlog.getMultimediaDocumentsFiles()) {
                                        multimediaFile.initUploadThread();
                                    }
                                    for (MultimediaFile multimediaFile : threadBlog.getMultimediaVideosFiles()) {
                                        multimediaFile.initUploadThread();
                                    }
                                    //startActivity(SectionActivity.getIntent(NewThreadActivity.this));
                                    finish();
                                }

                                @Override
                                public void onErrorResponse(VolleyError result) {
                                }
                            },
                                    NewThreadActivity.this, editTitle.getText().toString(), editDescription.getText().toString(),
                                    new_thread_blog_id, selectedMultimediaFiles, threadBlog);
                        } catch (Exception e) {
                            Toast.makeText(NewThreadActivity.this, "Error con archivos multimedia", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(NewThreadActivity.this, "Nuevo post creado", Toast.LENGTH_LONG).show();
                        //startActivity(SectionActivity.getIntent(NewThreadActivity.this));
                        finish();
                    }


                    VolleyGetThreads.volleyGetThreads(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {
                        }
                    },NewThreadActivity.this);

                }

                @Override
                public void onErrorResponse(VolleyError result) {
                }
            },NewThreadActivity.this, sessionManager.getSection(),editTitle.getText().toString(),editDescription.getText().toString());
            finish();
            }
        });
    }

    public void setfabttAchListener() {
        fabAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!multimediaIsOpen) {
                    constraintMultimediaBar.setVisibility(View.VISIBLE);
                } else {
                    constraintMultimediaBar.setVisibility(View.GONE);
                }
                multimediaIsOpen = !multimediaIsOpen;
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
                if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        textRecording.setVisibility(View.VISIBLE);
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_NORMAL);
                        fabFiles.setVisibility(View.GONE);
                        fabGallery.setVisibility(View.GONE);
                        fabCamera.setVisibility(View.GONE);
                        fabCreateThread.setVisibility(View.GONE);
                        fabVideo.setVisibility(View.GONE);
                        fabAttach.setVisibility(View.GONE);
                    }
                    else{
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_MINI);
                        textRecording.setVisibility(View.GONE);
                        fabFiles.setVisibility(View.VISIBLE);
                        fabGallery.setVisibility(View.VISIBLE);
                        fabCamera.setVisibility(View.VISIBLE);
                        fabCreateThread.setVisibility(View.VISIBLE);
                        fabVideo.setVisibility(View.VISIBLE);
                        fabAttach.setVisibility(View.VISIBLE);
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

                if (ContextCompat.checkSelfPermission(NewThreadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
                if (ContextCompat.checkSelfPermission(NewThreadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(NewThreadActivity.this, Manifest.permission.CAMERA)
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
                if (ContextCompat.checkSelfPermission(NewThreadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(NewThreadActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    getCameraPermissions();
                }
                else {
                    dispatchRecordVideoIntent();
                }
            }
        });
    }

    public void dispatchTakePictureIntent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Constants.M_APP_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdir();
        }
        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + ".jpg";

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                File.separator +
                Constants.M_APP_DIRECTORY + File.separator + imageName;
        File newFile = new File(file, imageName);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(takePictureIntent, CAMERA_REQUEST_PICTURE);
    }

    public void dispatchRecordVideoIntent() {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Constants.M_APP_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated) {
            file.mkdir();
        }
        Long timestamp = System.currentTimeMillis() / 1000;
        String imageName = timestamp.toString() + VIDEO_FORMAT;

        mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +
                File.separator +
                Constants.M_APP_DIRECTORY + File.separator + imageName;
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
                    if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
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
                    if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
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

                    threadBlog.getMultimediaPictureFiles().add(
                        new MultimediaFile(
                            Constants.S3_THREADS_PATH,
                            Constants.S3_IMAGES_PATH,
                            //CACHE_FOLDER+ "/"+mPath.substring(mPath.lastIndexOf("/")+1,mPath.length()),
                            mPath,
                            transferUtility,
                            threadBlog.getId(),
                            notAdded));
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

                    threadBlog.getMultimediaVideosFiles().add(
                        new MultimediaFile(
                            Constants.S3_THREADS_PATH,
                            Constants.S3_VIDEOS_PATH,
                            //CACHE_FOLDER+ "/"+mPath.substring(mPath.lastIndexOf("/")+1,mPath.length()),
                            mPath,
                            transferUtility,
                            threadBlog.getId(),
                            notAdded));
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

                        threadBlog.getMultimediaPictureFiles().add(
                            new MultimediaFile(
                                Constants.S3_THREADS_PATH,
                                Constants.S3_IMAGES_PATH,
                                //CACHE_FOLDER+ "/"+mPath.substring(mPath.lastIndexOf("/")+1,mPath.length()),
                                mPath,
                                transferUtility,
                                threadBlog.getId(),
                                notAdded));
                        multimediaPictureAdapter.notifyDataSetChanged();


                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Toast.makeText(NewThreadActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case FILES_REQUEST:
                    if (Build.VERSION.SDK_INT < 19) {
                        mPath = RealPathUtil.getRealPathFromURI_API11to18(getApplicationContext(), data.getData());
                    } else {
                        mPath = RealPathUtil.getRealPathFromURI_API19(getApplicationContext(), data.getData());
                    }
                    threadBlog.getMultimediaDocumentsFiles().add(
                        new MultimediaFile(
                            Constants.S3_THREADS_PATH,
                            Constants.S3_DOCS_PATH,
                            //CACHE_FOLDER+ "/"+mPath.substring(mPath.lastIndexOf("/")+1,mPath.length()),
                            mPath,
                            transferUtility,
                            threadBlog.getId(),
                            notAdded));

                    multimediaDocumentAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }

    public void getReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewThreadActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que se vean las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(NewThreadActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_REQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NewThreadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewThreadActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos guardar las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(NewThreadActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_REEQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NewThreadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REEQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(NewThreadActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos tomar las fotos, necesitamos acceso a su cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(NewThreadActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NewThreadActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getRecorAudioPermissions() {
        if (ContextCompat.checkSelfPermission(NewThreadActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(NewThreadActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos grabar audio, necesitamos permiso.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(NewThreadActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                RECORD_AUDIO_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(NewThreadActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //RECORD AUDIO
    public void onRecord(boolean start) {
        if (start) {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            MultimediaFile audioMultimedia =
                    new MultimediaFile(
                        Constants.S3_THREADS_PATH,
                        Constants.S3_AUDIOS_PATH,
                        ABSOLUTE_STORAGE_PATH + ts.toString() + EXTENSION_AUDIO_FORMAT,
                        transferUtility,
                        threadBlog.getId(),
                        notAdded);

            startRecording(audioMultimedia);
            threadBlog.getMultimediaAudioFiles().add(audioMultimedia);
        } else {
            stopRecording();
        }
    }

    public void startRecording(MultimediaFile multimediaFile) {
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

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        multimediaAudioAdapter.notifyDataSetChanged();
    }

}
