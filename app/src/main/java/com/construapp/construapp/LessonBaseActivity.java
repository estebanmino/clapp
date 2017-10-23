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
import android.support.constraint.ConstraintLayout;
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
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.construapp.construapp.api.VolleyCreateLesson;
import com.construapp.construapp.api.VolleyPostS3;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class LessonBaseActivity extends AppCompatActivity {

    //CONSTANTS
    public static final int WRITE_EXTERNAL_REEQUEST = 1886;
    public static final int CAMERA_REQUEST = 1888;
    public static  final int CAMERA_REQUEST_PICTURE = 1887;
    public static final int SELECT_IMAGE = 1885;
    public static final int READ_EXTERNAL_REQUEST = 1884;
    public static final int RECORD_AUDIO_REQUEST = 1883;
    public static final int FILES_REQUEST = 1882;
    public static final int CAMERA_REQUEST_FOR_VIDEO = 1880;


    public static String ABSOLUTE_STORAGE_PATH;
    public static final String EXTENSION_AUDIO_FORMAT = ".3gp";
    public static final String VIDEO_FORMAT = ".mp4";


    //XML ELEMENTS
    public TextView lessonName;
    public TextView  lessonDescription;
    public FloatingActionButton fabCamera;
    public FloatingActionButton fabGallery;
    public FloatingActionButton fabRecordAudio;
    public FloatingActionButton fabFiles;
    public FloatingActionButton fabSend;
    public FloatingActionButton fabVideo;
    public EditText editLessonName;
    public EditText editLessonDescription;
    public TextView textRecording;
    public ConstraintLayout constraintMultimediaBar;

    //LOCAL VARIABLES
    public String mPath;
    public View mLayout;

    //FOR AUDIO RECORD
    ////record audio
    public static final String LOG_TAG = "AudioRecordTest";
    public static String mRecordFileName = null;
    public MediaRecorder mRecorder = null;
    public MediaPlayer mPlayer = null;
    public boolean mStartRecording;
    public boolean mStartPlaying = true;
    public boolean isRecording =  false;

    //AmazonS3
    public TransferUtility transferUtility;

    //NEW LESSON FOR FORM
    public Lesson lesson;

    //CONSTANTS
    public  General constants;

    //MM ADAPTER
    public MultimediaPictureAdapter multimediaPictureAdapter;
    public MultimediaVideoAdapter multimediaVideoAdapter;
    public MultimediaAudioAdapter multimediaAudioAdapter;
    public MultimediaDocumentAdapter multimediaDocumentAdapter;

    public SharedPreferences sharedPreferences;


    //LESSON VIEW


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(mStartRecording);
                    if (mStartRecording == true) {
                        textRecording.setVisibility(View.VISIBLE);
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_NORMAL);
                        fabFiles.setVisibility(View.GONE);
                        fabGallery.setVisibility(View.GONE);
                        fabCamera.setVisibility(View.GONE);
                        fabSend.setVisibility(View.GONE);
                        fabVideo.setVisibility(View.GONE);
                    }
                    else{
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_MINI);
                        textRecording.setVisibility(View.GONE);
                        fabFiles.setVisibility(View.VISIBLE);
                        fabGallery.setVisibility(View.VISIBLE);
                        fabCamera.setVisibility(View.VISIBLE);
                        fabSend.setVisibility(View.VISIBLE);
                        fabVideo.setVisibility(View.VISIBLE);
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

                if (ContextCompat.checkSelfPermission(LessonBaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
                if (ContextCompat.checkSelfPermission(LessonBaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonBaseActivity.this, Manifest.permission.CAMERA)
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
                if (ContextCompat.checkSelfPermission(LessonBaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(LessonBaseActivity.this, Manifest.permission.CAMERA)
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
                    if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
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
                    if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
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
                        Toast.makeText(LessonBaseActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonBaseActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que se vean las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonBaseActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_REQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonBaseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonBaseActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos guardar las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonBaseActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_REEQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonBaseActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REEQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonBaseActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos tomar las fotos, necesitamos acceso a su cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonBaseActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonBaseActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getRecorAudioPermissions() {
        if (ContextCompat.checkSelfPermission(LessonBaseActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LessonBaseActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos grabar audio, necesitamos permiso.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(LessonBaseActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                RECORD_AUDIO_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(LessonBaseActivity.this,
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
            MultimediaFile audioMultimedia = new MultimediaFile(
                    Constants.S3_AUDIOS_PATH, ABSOLUTE_STORAGE_PATH + ts.toString() + EXTENSION_AUDIO_FORMAT, null,transferUtility);
            startRecording(audioMultimedia);
            lesson.getMultimediaAudiosFiles().add(audioMultimedia);
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
