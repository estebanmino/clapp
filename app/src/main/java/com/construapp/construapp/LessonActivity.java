package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.api.VolleyPutLesson;
import com.construapp.construapp.db.AppDatabase;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
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


public class LessonActivity extends LessonBaseActivity {

    private static final String LESSON_NAME = "username";
    private static final String LESSON_DESCRIPTION = "description";
    private static final String LESSON_ID = "id";
    private static final String PROJECT_FOLDER = "ConstruApp";

    private int userPermission;

    //MM ADAPTER

    ImageView imageEditLesson;
    ImageView imageDeleteLesson;
    TextView textEditLesson;
    TextView textDeleteLesson;
    TextView textLessonName;
    TextView textLessonDescription;

    private EditText editName;
    private EditText editDescription;
    private TextView textNameEdit;
    private TextView textDescriptionEdit;
    private TextView textNewLessonName;
    private TextView textNewLessonDescription;

    private TextView textImages;
    private TextView textVideos;
    private TextView textAudios;
    private TextView textDocuments;

    private Boolean editing = false;

    private RecyclerView mPicturesRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mDocumentsRecyclerView;
    private RecyclerView mAudiosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        constants = new General();
        userPermission = Integer.parseInt(sharedPreferences.getString(Constants.SP_USER_PERMISSION,""));

        // Record to the external cache directory for visibility
        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();
        mRecordFileName = ABSOLUTE_STORAGE_PATH + "/audiorecordtest.3gp";

        imageEditLesson = (ImageView) findViewById(R.id.image_edit_lesson);
        imageDeleteLesson = (ImageView) findViewById(R.id.image_delete_lesson);
        textEditLesson = (TextView) findViewById(R.id.text_edit_lesson);
        textDeleteLesson = (TextView) findViewById(R.id.text_delete_lesson);
        textLessonName = (TextView) findViewById(R.id.text_lesson_name);
        textLessonDescription = (TextView) findViewById(R.id.text_lesson_description);
        textNewLessonName = findViewById(R.id.text_new_lesson_name);
        textNewLessonDescription = findViewById(R.id.text_new_lesson_description);

        textImages = (TextView) findViewById(R.id.text_images);
        textVideos = (TextView) findViewById(R.id.text_videos);
        textAudios = (TextView) findViewById(R.id.text_audios);
        textDocuments = (TextView) findViewById(R.id.text_documents);

        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        fabSend = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        fabVideo = findViewById(R.id.fab_video);
        textRecording = findViewById(R.id.text_recording);

        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);

        editName = findViewById(R.id.edit_name);
        editDescription = findViewById(R.id.edit_description);
        textNameEdit = findViewById(R.id.text_lesson_name_edit);
        textDescriptionEdit = findViewById(R.id.text_lesson_description_edit);

        setLesson();

        mStartRecording = true;

        textLessonName.setText(lesson.getName());
        textLessonDescription.setText(lesson.getDescription());

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicturesRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonActivity.this,lesson);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),LessonActivity.this,lesson);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAudiosRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonActivity.this,lesson);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDocumentsRecyclerView = (RecyclerView) findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonActivity.this,lesson);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonActivity.this));
        transferUtility = new TransferUtility(s3, LessonActivity.this);

        ABSOLUTE_STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        final String CACHE_FOLDER = LessonActivity.this.getCacheDir().toString();

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
                    ArrayList<String> videosPathsList  = new ArrayList<>();

                    for (String fileKey: arrayList) {
                        if (fileKey.contains(Constants.S3_IMAGES_PATH)){
                            picturePathsList.add(fileKey);
                            textImages.setVisibility(View.VISIBLE);
                            mPicturesRecyclerView.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_AUDIOS_PATH)) {
                            audioPathsList.add(fileKey);
                            textAudios.setVisibility(View.VISIBLE);
                            mAudiosRecyclerView.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_DOCS_PATH)) {
                            documentPathsList.add(fileKey);
                            textDocuments.setVisibility(View.VISIBLE);
                            mDocumentsRecyclerView.setVisibility(View.VISIBLE);
                        } else if (fileKey.contains(Constants.S3_VIDEOS_PATH)) {
                            videosPathsList.add(fileKey);
                            textVideos.setVisibility(View.VISIBLE);
                            mVideosRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    String[] pictureArray = picturePathsList.toArray(new String[0]);
                    for (String path: pictureArray){
                        lesson.getMultimediaPicturesFiles().add(new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_IMAGES_PATH,CACHE_FOLDER+
                                "/"+path.substring(path.lastIndexOf("/")+1,path.length()-1),path.replace("\"", ""),transferUtility,notAdded));
                    }
                    multimediaPictureAdapter.notifyDataSetChanged();

                    for (String audioPath: audioPathsList) {
                        MultimediaFile audioMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                    Constants.S3_AUDIOS_PATH,CACHE_FOLDER+
                                "/"+audioPath.substring(audioPath.lastIndexOf("/")+1,audioPath.length()-1),audioPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaAudiosFiles().add(audioMultimedia);
                    }
                    multimediaAudioAdapter.notifyDataSetChanged();

                    for (String documentPath: documentPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                    Constants.S3_DOCS_PATH,ABSOLUTE_STORAGE_PATH+
                                "/"+PROJECT_FOLDER+"/"+documentPath.substring(documentPath.lastIndexOf("/")+1,documentPath.length()-1),
                                documentPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaDocumentsFiles().add(documentMultimedia);

                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();

                    for (String videoPath: videosPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_VIDEOS_PATH,ABSOLUTE_STORAGE_PATH+"/"+PROJECT_FOLDER+"/"+
                                videoPath.substring(videoPath.lastIndexOf("/")+1,videoPath.length()-1),
                                videoPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaVideosFiles().add(documentMultimedia);
                    }
                    multimediaVideoAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, LessonActivity.this, lesson.getId());

        setImageDeleteLessonListener();
        setImageEditLessonListener();

        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        setFabSendOnClickListener();
        setFabRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setFabVideoOnClickListener();
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
                    mySnackbar.setAction("Confirmar", new DeleteListener());
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
                editing = !editing;
                if (editing) {
                    editName.setText(getIntent().getStringExtra(LESSON_NAME));
                    editDescription.setText(getIntent().getStringExtra(LESSON_DESCRIPTION));
                    constraintMultimediaBar.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.VISIBLE);
                    editName.setVisibility(View.VISIBLE);
                    textNameEdit.setVisibility(View.VISIBLE);
                    textDescriptionEdit.setVisibility(View.VISIBLE);
                    textLessonName.setVisibility(View.GONE);
                    textLessonDescription.setVisibility(View.GONE);
                    textLessonName.setVisibility(View.GONE);
                    textLessonDescription.setVisibility(View.GONE);
                    textNewLessonName.setVisibility(View.GONE);
                    textNewLessonDescription.setVisibility(View.GONE);

                    textEditLesson.setText("Cancelar");
                    imageDeleteLesson.setVisibility(View.GONE);
                    textDeleteLesson.setVisibility(View.GONE);

                }
                else {
                    finish();
                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);
                    /*
                    constraintMultimediaBar.setVisibility(View.GONE);
                    editDescription.setVisibility(View.GONE);
                    editName.setVisibility(View.GONE);
                    textNameEdit.setVisibility(View.GONE);
                    textDescriptionEdit.setVisibility(View.GONE);

                    textLessonName.setVisibility(View.VISIBLE);
                    textLessonDescription.setVisibility(View.VISIBLE);
                    textLessonName.setVisibility(View.VISIBLE);
                    textLessonDescription.setVisibility(View.VISIBLE);
                    textNewLessonName.setVisibility(View.VISIBLE);
                    textNewLessonDescription.setVisibility(View.VISIBLE);

                    textEditLesson.setText("Editar");
                    imageDeleteLesson.setVisibility(View.VISIBLE);
                    textDeleteLesson.setVisibility(View.VISIBLE);
                    lesson = new Lesson();
                    copyLesson(originalLesson, lesson); */
                }

                multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),LessonActivity.this,lesson);
                mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);
                multimediaAudioAdapter.notifyDataSetChanged();
                multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),LessonActivity.this,lesson);
                mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);
                multimediaPictureAdapter.notifyDataSetChanged();
                multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),LessonActivity.this,lesson);
                mVideosRecyclerView.setAdapter(multimediaVideoAdapter);
                multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),LessonActivity.this,lesson);
                mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);
            }
        });
    }

    public void showInfo(View view) {
        Intent intent = new Intent(this, ShowInfo.class);
        TextView projectName = (TextView) findViewById(R.id.text_lesson_name);
        String message = projectName.getText().toString();
        startActivity(intent);
    }

    public void setLesson() {
        try {
            lesson = new GetLessonTask(LessonActivity.this, getIntent().getStringExtra(LESSON_ID)).execute().get();
/*        lesson.setName(getIntent().getStringExtra(LESSON_NAME));
            lesson.setDescription(getIntent().getStringExtra(LESSON_DESCRIPTION));
            lesson.setId(getIntent().getStringExtra(LESSON_ID));
            lesson.setCompany_id(sharedPreferences.getString(Constants.SP_COMPANY, ""));*/
            lesson.initMultimediaFiles();
            showPermissions();
        } catch (Exception e) {
            lesson = new Lesson();
        }
    }

    public static Intent getIntent(Context context, String name, String description, String id) {
        Intent intent = new Intent(context,LessonActivity.class);
        intent.putExtra(LESSON_NAME,name);
        intent.putExtra(LESSON_DESCRIPTION,description);
        intent.putExtra(LESSON_ID,id);
        return intent;
    }

    public void showPermissions(){


        int editPermission = constants.xmlPermissionTagToInt(imageEditLesson.getTag().toString());
        int deletePermission = constants.xmlPermissionTagToInt((imageDeleteLesson.getTag().toString()));
        sharedPreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);

        if (editPermission <= userPermission || sharedPreferences.getString(Constants.SP_ADMIN, "").equals("1")){
            imageEditLesson.setVisibility(View.VISIBLE);
            textEditLesson.setVisibility(View.VISIBLE);
        }
        if (deletePermission <= userPermission || sharedPreferences.getString(Constants.SP_ADMIN, "").equals("1")){
            imageDeleteLesson.setVisibility(View.VISIBLE);
            textDeleteLesson.setVisibility(View.VISIBLE);
        }
    }

    public class DeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.i("DELETE","Lesson deleted");
            VolleyDeleteLesson.volleyDeleteLesson(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        //Delete the lesson from DB
                        new DeleteLessonTask(lesson,getApplicationContext()).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LessonActivity.this, "Eliminada correctamente", Toast.LENGTH_LONG).show();
                    startActivity(MainActivity.getIntent(LessonActivity.this));
                }

                @Override
                public void onErrorResponse(VolleyError result) {
                    Toast.makeText(LessonActivity.this, "Ocurrio un error, por favor reintentar", Toast.LENGTH_LONG).show();
                }
            }, LessonActivity.this, lesson.getId());
        }
    }

    public void setFabSendOnClickListener(){
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String lesson_name = editName.getText().toString();
            String lesson_summary = editDescription.getText().toString();
            String lesson_motivation = "Aprendizaje";
            String lesson_learning = editDescription.getText().toString();
            String project_id = lesson.getProject_id();
            String validation = "0";
            ArrayList<String> array_added =  lesson.getAddedMultimediaKeysS3();
            ArrayList<String> array_deleted =  lesson.getDeletedMultimediaFilesS3Keys();
            VolleyPutLesson.volleyPutLesson(new VolleyJSONCallback() {
                  @Override
                  public void onSuccess(JSONObject result) {
                      Toast.makeText(LessonActivity.this, "Leccion editada", Toast.LENGTH_LONG).show();
                      String start = Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/";
                      for (MultimediaFile multimediaFile: lesson.getMultimediaPicturesFiles()) {
                          if (multimediaFile.getAdded() == 1) {
                              multimediaFile.setExtension(start+Constants.S3_IMAGES_PATH);
                              multimediaFile.initUploadThread();
                          }
                      }
                      for (MultimediaFile multimediaFile: lesson.getMultimediaAudiosFiles()) {
                          if (multimediaFile.getAdded() == 1) {
                              multimediaFile.setExtension(start+Constants.S3_AUDIOS_PATH);
                              multimediaFile.initUploadThread();
                          }
                      }
                      for (MultimediaFile multimediaFile: lesson.getMultimediaDocumentsFiles()) {
                          if (multimediaFile.getAdded() == 1) {
                              multimediaFile.setExtension(start+Constants.S3_DOCS_PATH);
                              multimediaFile.initUploadThread();
                          }
                      }
                      for (MultimediaFile multimediaFile: lesson.getMultimediaVideosFiles()) {
                          if (multimediaFile.getAdded() == 1) {
                              multimediaFile.setExtension(start+Constants.S3_VIDEOS_PATH);
                              multimediaFile.initUploadThread();
                          }
                      }
                      startActivity(MainActivity.getIntent(LessonActivity.this));
                  }

                  @Override
                  public void onErrorResponse(VolleyError result) {
                      Toast.makeText(LessonActivity.this, "No se puede editar la leccióon en este momento", Toast.LENGTH_LONG).show();
                  }
                }, LessonActivity.this,
                    lesson.getId(), lesson_name, lesson_summary,
                lesson_motivation, lesson_learning, array_added, array_deleted, validation
                );

            }
        });
    }

    public Boolean getEditing(){
        return editing;
    }

}