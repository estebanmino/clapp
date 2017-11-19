package com.construapp.construapp.lessons;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.api.VolleyDeleteFavouriteLesson;
import com.construapp.construapp.api.VolleyGetCompanyAttributes;
import com.construapp.construapp.api.VolleyGetFavouriteLessons;
import com.construapp.construapp.api.VolleyPostFavouriteLesson;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.main.LessonsFragment;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.ShowInfo;
import com.construapp.construapp.api.VolleyPutLesson;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.SessionManager;
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

    private int userPermission;

    //MM ADAPTER

    private Button btnEdit;
    private Button btnDelete;

    private TextView textImages;
    private TextView textVideos;
    private TextView textAudios;
    private TextView textDocuments;

    private TextView textDisciplines;
    private TextView textDepartments;
    private TextView textClassifications;

    private Boolean editing = false;

    private RecyclerView mPicturesRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mDocumentsRecyclerView;
    private RecyclerView mAudiosRecyclerView;

    private RecyclerView mClassificationsRecyclerView;
    private RecyclerView mDisciplinesRecyclerView;
    private RecyclerView mDepartmentsRecyclerView;
    private LessonAttributesAdapter disciplinesAttributesAdapter;
    private LessonAttributesAdapter departmentsAttributesAdapter;
    private LessonAttributesAdapter classificationsAttributesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        constants = new General();

        linearLayoutMultimedia = findViewById(R.id.linear_layout_multimedia);
        sessionManager = new SessionManager(LessonActivity.this);
        userPermission = Integer.parseInt(sessionManager.getActualUserPermission());

        // Record to the external cache directory for visibility
        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();
        mRecordFileName = ABSOLUTE_STORAGE_PATH + "/audiorecordtest.3gp";

        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        imageSetFavourite = findViewById(R.id.image_set_favourite);
        imageUndoFavourite = findViewById(R.id.image_undo_favourite);

        textImages = findViewById(R.id.text_images);
        textVideos = findViewById(R.id.text_videos);
        textAudios = findViewById(R.id.text_audios);
        textDocuments = findViewById(R.id.text_documents);
        textAttachments = findViewById(R.id.text_attachments);

        textDisciplines = findViewById(R.id.text_disciplines);
        textClassifications = findViewById(R.id.text_classifications);
        textDepartments = findViewById(R.id.text_departments);

        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        fabSend = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        fabVideo = findViewById(R.id.fab_video);
        fabSave = findViewById(R.id.fab_save);
        textRecording = findViewById(R.id.text_recording);

        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);
        linearEdition = findViewById(R.id.linear_edition);
        constraintActionBar = findViewById(R.id.constraint_action_bar);
        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);
        imageAttach = findViewById(R.id.image_attach);
        setImageAttachListener();

        tagEditTags = findViewById(R.id.edit_tags);

        tagEditTags.setInputType(InputType.TYPE_NULL);

        setLesson();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.B_LESSON_ARRAY_LIST, lesson.getFormAttributes());
        FragmentManager  fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonViewFragment lessonViewFragment = new LessonViewFragment();
        lessonViewFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.constraint_fragment_container,lessonViewFragment);

        Bundle bundleComments = new Bundle();
        bundleComments.putString(Constants.B_LESSON_COMMENTS, lesson.getComments());
        bundleComments.putString(Constants.B_LESSON_ID, lesson.getId());
        LessonCommentsFragment lessonCommentsFragment = new LessonCommentsFragment();
        lessonCommentsFragment.setArguments(bundleComments);
        fragmentTransaction.add(R.id.constraint_comments_container,lessonCommentsFragment);
        fragmentTransaction.commit();


        if (lesson.getReject_comment() !=  null && lesson.getValidation().equals(Constants.R_REJECTED)) {
            Bundle bundleComment = new Bundle();
            bundleComment.putString(Constants.B_LESSON_REJECT_COMMENT, lesson.getReject_comment());
            FragmentManager fragmentManagerComment = getFragmentManager();
            FragmentTransaction fragmentTransactionComment = fragmentManagerComment.beginTransaction();
            LessonRejectCommentFragment lessonRejectCommentFragment = new LessonRejectCommentFragment();
            lessonRejectCommentFragment.setArguments(bundleComment);
            fragmentTransactionComment.add(R.id.constraint_fragment_comment_container, lessonRejectCommentFragment);
            fragmentTransactionComment.commit();
        }

        if (!lesson.getValidation().equals(Constants.R_VALIDATED)){
            fabSend.setImageDrawable(ContextCompat.getDrawable(LessonActivity.this, R.drawable.ic_send_dark));
        }

        mStartRecording = true;
        linearLayoutTriggers = findViewById(R.id.linear_triggers);
        linearLayoutTriggers.setClickable(false);
        btnTriggerError = findViewById(R.id.btn_trigger_trerror);
        btnTriggerOmision = findViewById(R.id.btn_trigger_omision);
        btnTriggerGoodPractice = findViewById(R.id.btn_trigger_good_practices);
        btnTriggerImprovement = findViewById(R.id.btn_trigger_improvement);

        btnTriggerError.setClickable(false);
        btnTriggerOmision.setClickable(false);
        btnTriggerGoodPractice.setClickable(false);
        btnTriggerImprovement.setClickable(false);

        linearTriggersSetTriggerIdClicked(lesson.getTrigger_id());
        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicturesRecyclerView = findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(),
                LessonActivity.this,lesson);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosRecyclerView = findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(),
                LessonActivity.this,lesson);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAudiosRecyclerView = findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(),
                LessonActivity.this,lesson);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDocumentsRecyclerView = findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(),
                LessonActivity.this,lesson);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        //DISCIPLINES SCROLLING
        LinearLayoutManager disciplinesLayoutManager = new LinearLayoutManager(this);
        disciplinesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDisciplinesRecyclerView = findViewById(R.id.recycler_horizontal_disciplines);
        mDisciplinesRecyclerView.setLayoutManager(disciplinesLayoutManager);
        //String[] disciplinesArray =  sessionManager.getDisciplines();
        String[] disciplinesArray =  lesson.getDisciplinesArray();
        if (disciplinesArray.length != 0) {
            disciplinesAttributesAdapter = new LessonAttributesAdapter(disciplinesArray,
                    LessonActivity.this, lesson, editing, Constants.TAG_DISCIPLINES);
            mDisciplinesRecyclerView.setAdapter(disciplinesAttributesAdapter);
        } else {
            textDisciplines.setText("Disciplinas (no hay disciplinas asignadas)");
            mDisciplinesRecyclerView.setVisibility(View.GONE);
        }

        //CLASSIFICATIONS SCROLLING
        LinearLayoutManager classificationsLayoutManager = new LinearLayoutManager(this);
        classificationsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mClassificationsRecyclerView = findViewById(R.id.recycler_horizontal_classifications);
        mClassificationsRecyclerView.setLayoutManager(classificationsLayoutManager);
        ///String[] classificationsArray =  sessionManager.getClassifications();
        String[] classificationsArray =  lesson.getClassificationsArray();
        if (classificationsArray.length != 0) {
            classificationsAttributesAdapter = new LessonAttributesAdapter(classificationsArray,
                    LessonActivity.this, lesson, editing, Constants.TAG_CLASSIFICATIONS);
            mClassificationsRecyclerView.setAdapter(classificationsAttributesAdapter);
        } else {
            textClassifications.setText("Clasificación (no hay clasificaciones asignadas)");
            mClassificationsRecyclerView.setVisibility(View.GONE);
        }

        //DEPARTMENT SCROLLING
        LinearLayoutManager departmentsLayoutManager = new LinearLayoutManager(this);
        departmentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDepartmentsRecyclerView = findViewById(R.id.recycler_horizontal_departments);
        mDepartmentsRecyclerView.setLayoutManager(departmentsLayoutManager);
        //String[] departmentsArray =  sessionManager.getDepartments();
        String[] departmentsArray =  lesson.getDepartmentsArray();
        if (departmentsArray.length != 0) {
            departmentsAttributesAdapter = new LessonAttributesAdapter(departmentsArray,
                    LessonActivity.this, lesson, editing, Constants.TAG_DEPARTMENTS);
            mDepartmentsRecyclerView.setAdapter(departmentsAttributesAdapter);
        } else {
            textDepartments.setText("Departamento (no hay departamentos asignados)");
            mDepartmentsRecyclerView.setVisibility(View.GONE);
        }

        //TAGS
        tagEditTags.setText(lesson.getTagsSpaced());
        if (!editing) {tagEditTags.setClickable(false);};

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
                                "/"+Constants.M_APP_DIRECTORY+"/"+documentPath.substring(documentPath.lastIndexOf("/")+1,documentPath.length()-1),
                                documentPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaDocumentsFiles().add(documentMultimedia);

                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();

                    for (String videoPath: videosPathsList) {
                        MultimediaFile documentMultimedia = new MultimediaFile(
                                Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/"+
                                        Constants.S3_VIDEOS_PATH,ABSOLUTE_STORAGE_PATH+"/"+Constants.M_APP_DIRECTORY+"/"+
                                videoPath.substring(videoPath.lastIndexOf("/")+1,videoPath.length()-1),
                                videoPath.replace("\"", ""),transferUtility,notAdded);
                        lesson.getMultimediaVideosFiles().add(documentMultimedia);
                    }
                    multimediaVideoAdapter.notifyDataSetChanged();

                    if (!lesson.hasMultimediaFiles()) {
                        linearLayoutMultimedia.setVisibility(View.GONE);
                        textAttachments.setText(Constants.NO_ATTACHMENTS);
                    } else {
                        if (lesson.getMultimediaPicturesFiles().isEmpty()){
                            textImages.setText(Constants.NO_PICTURES);
                            mPicturesRecyclerView.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaAudiosFiles().isEmpty()){
                            textAudios.setText(Constants.NO_AUDIOS);
                            mAudiosRecyclerView.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaVideosFiles().isEmpty()){
                            textDocuments.setText(Constants.NO_VIDEOS);
                            mDocumentsRecyclerView.setVisibility(View.GONE);
                        }
                        if (lesson.getMultimediaDocumentsFiles().isEmpty()){
                            textVideos.setText(Constants.NO_DOCUMENTS);
                            mVideosRecyclerView.setVisibility(View.GONE);
                        }
                    }
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, LessonActivity.this, lesson.getId());

        setImageDeleteLessonListener();
        setImageEditLessonListener();
        setImageSetFavouriteListener();
        setImageUndoFavouriteListener();

        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        if (!lesson.getValidation().equals("1")) {
            setFabSendOnClickListener();
        } else {
            fabSend.setVisibility(View.GONE);
        }
        setFabSaveOnClickListener();
        setFabRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setFabVideoOnClickListener();

        if (lesson.getValidation().equals(Constants.R_VALIDATED)) {
            if (sessionManager.getFavouriteLessonsIds().contains(lesson.getId())) {
                imageUndoFavourite.setVisibility(View.VISIBLE);
                imageSetFavourite.setVisibility(View.GONE);
            } else {
                imageUndoFavourite.setVisibility(View.GONE);
                imageSetFavourite.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setImageDeleteLessonListener() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
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
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editing = !editing;
                if (editing) {
                    linearLayoutTriggers.setClickable(true);
                    btnTriggerError.setClickable(true);
                    btnTriggerOmision.setClickable(true);
                    btnTriggerGoodPractice.setClickable(true);
                    btnTriggerImprovement.setClickable(true);
                    setLinearTriggersButtonClick();

                    constraintActionBar.setVisibility(View.VISIBLE);
                    btnEdit.setText("Cancelar");
                    btnDelete.setVisibility(View.GONE);
                    tagEditTags.setInputType(InputType.TYPE_CLASS_TEXT);

                    if (Connectivity.isConnected(LessonActivity.this)) {
                        VolleyGetCompanyAttributes.volleyGetCompanyAttributes(new VolleyJSONCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                String[] classificationsArray =  sessionManager.getClassifications();
                                if (classificationsArray.length != 0) {
                                    textClassifications.setText("Clasificación (seleccione para agregar)");
                                    classificationsAttributesAdapter = new LessonAttributesAdapter(classificationsArray,
                                            LessonActivity.this, lesson, editing, Constants.TAG_CLASSIFICATIONS);
                                    mClassificationsRecyclerView.setAdapter(classificationsAttributesAdapter);
                                    mClassificationsRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    textClassifications.setText("Clasificación (no hay clasificaciones asignadas)");
                                    mClassificationsRecyclerView.setVisibility(View.GONE);
                                }

                                String[] disciplinesArray =  sessionManager.getDisciplines();
                                //String[] disciplinesArray =  lesson.getDisciplinesArray();
                                if (disciplinesArray.length != 0) {
                                    disciplinesAttributesAdapter = new LessonAttributesAdapter(disciplinesArray,
                                            LessonActivity.this, lesson, true, Constants.TAG_DISCIPLINES);
                                    mDisciplinesRecyclerView.setAdapter(disciplinesAttributesAdapter);
                                    mDisciplinesRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    textDisciplines.setText("Disciplinas (no hay disciplinas asignadas)");
                                    mDisciplinesRecyclerView.setVisibility(View.GONE);
                                }

                                String[] departmentsArray =  sessionManager.getDepartments();
                                if (departmentsArray.length != 0) {
                                    departmentsAttributesAdapter = new LessonAttributesAdapter(departmentsArray,
                                            LessonActivity.this, lesson, editing, Constants.TAG_DEPARTMENTS);
                                    mDepartmentsRecyclerView.setAdapter(departmentsAttributesAdapter);
                                    mDepartmentsRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    textDepartments.setText("Departamento (no hay departamentos asignados)");
                                    mDepartmentsRecyclerView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                            }
                        }, LessonActivity.this);
                    } else {
                        String[] classificationsArray =  sessionManager.getClassifications();
                        if (classificationsArray.length != 0) {
                            textClassifications.setText("Clasificación (seleccione para agregar)");
                            classificationsAttributesAdapter = new LessonAttributesAdapter(classificationsArray,
                                    LessonActivity.this, lesson, editing, Constants.TAG_CLASSIFICATIONS);
                            mClassificationsRecyclerView.setAdapter(classificationsAttributesAdapter);
                            mClassificationsRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            textClassifications.setText("Clasificación (no hay clasificaciones asignadas)");
                            mClassificationsRecyclerView.setVisibility(View.GONE);
                        }

                        String[] disciplinesArray =  sessionManager.getDisciplines();
                        //String[] disciplinesArray =  lesson.getDisciplinesArray();
                        if (disciplinesArray.length != 0) {
                            disciplinesAttributesAdapter = new LessonAttributesAdapter(disciplinesArray,
                                    LessonActivity.this, lesson, true, Constants.TAG_DISCIPLINES);
                            mDisciplinesRecyclerView.setAdapter(disciplinesAttributesAdapter);
                        } else {
                            textDisciplines.setText("Disciplinas (no hay disciplinas asignadas)");
                            mDisciplinesRecyclerView.setVisibility(View.GONE);
                        }

                        String[] departmentsArray =  sessionManager.getDepartments();
                        if (departmentsArray.length != 0) {
                            departmentsAttributesAdapter = new LessonAttributesAdapter(departmentsArray,
                                    LessonActivity.this, lesson, editing, Constants.TAG_DEPARTMENTS);
                            mDepartmentsRecyclerView.setAdapter(departmentsAttributesAdapter);
                        } else {
                            textDepartments.setText("Departamento (no hay departamentos asignados)");
                            mDepartmentsRecyclerView.setVisibility(View.GONE);
                        }
                    }


                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(Constants.B_LESSON_ARRAY_LIST, lesson.getFormAttributes());
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    LessonEditFragment lessonEditFragment = new LessonEditFragment();
                    lessonEditFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.constraint_fragment_container, lessonEditFragment);
                    fragmentTransaction.commit();
                }
                else {
                    finish();
                    overridePendingTransition( 0, 0);
                    startActivity(getIntent());
                    overridePendingTransition( 0, 0);
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
        TextView projectName = findViewById(R.id.text_lesson_name);
        String message = projectName.getText().toString();
        startActivity(intent);
    }

    public void setLesson() {
        try {
            lesson = new GetLessonTask(LessonActivity.this, getIntent().getStringExtra(LESSON_ID)).execute().get();
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

        int editPermission = constants.xmlPermissionTagToInt(btnEdit.getTag().toString());
        int deletePermission = constants.xmlPermissionTagToInt((btnEdit.getTag().toString()));

        if (editPermission <= userPermission || sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)
                || (sessionManager.getUserId().equals(lesson.getUser_id()) &&
                !lesson.getValidation().equals(Constants.R_VALIDATED)
                ))
        {
            //linearEdition.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
        }
        if (deletePermission <= userPermission || sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)){
            //linearEdition.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }
    }

    public class DeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
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

    public void setFabSaveOnClickListener() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putLesson(lesson.getValidation());
            }
        });
    }

    public void setFabSendOnClickListener() {
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAGS",tagEditTags.getText().toString());
                putLesson(Constants.R_WAITING);
            }
        });
    }



    public void putLesson(String validation){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.constraint_fragment_container);
        EditText editLessonName = fragment.getView().findViewById(R.id.new_section_name);
        EditText editLessonSummary = fragment.getView().findViewById(R.id.edit_lesson_summary);
        EditText editLessonMotivation = fragment.getView().findViewById(R.id.edit_lesson_motivation);
        EditText editLessonLearning = fragment.getView().findViewById(R.id.edit_lesson_learning);

        String lesson_name = editLessonName.getText().toString();
        String lesson_summary = editLessonSummary.getText().toString();
        String lesson_motivation = editLessonMotivation.getText().toString();
        String lesson_learning = editLessonLearning.getText().toString();
        String project_id = lesson.getProject_id();
        ArrayList<String> array_added =  lesson.getAddedMultimediaKeysS3();
        ArrayList<String> array_deleted =  lesson.getDeletedMultimediaFilesS3Keys();
        lesson.setTrigger_id(linearTriggersGetTriggerId());
        VolleyPutLesson.volleyPutLesson(new VolleyJSONCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(LessonActivity.this, "Lección editada", Toast.LENGTH_LONG).show();
                String start = Constants.S3_LESSONS_PATH+"/"+lesson.getId()+"/";

                try {
                    new InsertLessonTask(lesson, LessonActivity.this).execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
                //startActivity(MainActivity.getIntent(LessonActivity.this));
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError result) {
                Toast.makeText(LessonActivity.this, "No se puede editar la lección en este momento", Toast.LENGTH_LONG).show();
            }
        }, LessonActivity.this,
                lesson.getId(), lesson_name, lesson_summary,
                lesson_motivation, lesson_learning, array_added, array_deleted, validation,
                tagEditTags.getText().toString(),
                (disciplinesAttributesAdapter!=null) ? disciplinesAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                (classificationsAttributesAdapter!=null) ? classificationsAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                (departmentsAttributesAdapter!=null) ? departmentsAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                lesson
        );
    }


    public Boolean getEditing(){
        return editing;
    }

    public void setImageSetFavouriteListener() {
        imageSetFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyPostFavouriteLesson.volleyPostFavouriteLesson(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        VolleyGetFavouriteLessons.volleyGetFavouriteLessons(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {
                                sessionManager.setFavouriteLessons(result);
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                            }
                        }, LessonActivity.this);
                        Toast.makeText(LessonActivity.this,"Lección favorita agregada con éxito.",Toast.LENGTH_LONG).show();
                        imageUndoFavourite.setVisibility(View.VISIBLE);
                        imageSetFavourite.setVisibility(View.GONE);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(LessonActivity.this,"Lección favorita no se logró agregar con éxito.",Toast.LENGTH_LONG).show();
                    }
                },LessonActivity.this,lesson.getId());
            }
        });
    }

    public void setImageUndoFavouriteListener() {
        imageUndoFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyDeleteFavouriteLesson.volleyDeleteFavouriteLesson(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        sessionManager.setFavouriteLessons(result);
                        Toast.makeText(LessonActivity.this,"Lección favorita eliminada con éxito.",Toast.LENGTH_LONG).show();
                        imageUndoFavourite.setVisibility(View.GONE);
                        imageSetFavourite.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(LessonActivity.this,"Lección favorita no se logró eliminar con éxito.",Toast.LENGTH_LONG).show();
                    }
                },LessonActivity.this,lesson.getId());
            }
        });
    }
}