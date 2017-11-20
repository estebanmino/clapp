package com.construapp.construapp.lessons;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.models.SessionManager;
import com.construapp.construapp.multimedia.MultimediaAudioAdapter;
import com.construapp.construapp.multimedia.MultimediaDocumentAdapter;
import com.construapp.construapp.multimedia.MultimediaPictureAdapter;
import com.construapp.construapp.multimedia.MultimediaVideoAdapter;
import com.construapp.construapp.api.VolleyCreateLesson;
import com.construapp.construapp.api.VolleyPostS3;

import org.json.JSONObject;

import java.util.ArrayList;

public class LessonFormActivity extends LessonBaseActivity {

    private RecyclerView mClassificationsRecyclerView;
    private RecyclerView mDisciplinesRecyclerView;
    private RecyclerView mDepartmentsRecyclerView;
    private LessonAttributesAdapter disciplinesAttributesAdapter;
    private LessonAttributesAdapter departmentsAttributesAdapter;
    private LessonAttributesAdapter classificationsAttributesAdapter;
    private TextView textDisciplines;
    private TextView textDepartments;
    private TextView textClassifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(LessonFormActivity.this);

        //FIND XML ELEMENTS
        textLessonName = findViewById(R.id.text_label_lesson_name);
        textLessonSummary = findViewById(R.id.text_label_lesson_description);

        editLessonName = findViewById(R.id.text_lesson_name);
        editLessonDescription = findViewById(R.id.text_lesson_summary);

        mLayout = findViewById(R.id.lesson_form_layout);

        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        textLessonImages = findViewById(R.id.text_images);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        textLessonAudios = findViewById(R.id.text_audios);
        fabSend = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        textLessonDocuments = findViewById(R.id.text_documents);
        fabVideo = findViewById(R.id.fab_video);
        textLessonVideos = findViewById(R.id.text_videos);
        fabSave = findViewById(R.id.fab_save);
        textRecording = findViewById(R.id.text_recording);

        fabSend.setVisibility(View.VISIBLE);

        constraintActionBar = findViewById(R.id.constraint_action_bar);
        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);
        imageAttach = findViewById(R.id.image_attach);
        setImageAttachListener();

        textDisciplines = findViewById(R.id.text_disciplines);
        textClassifications = findViewById(R.id.text_classifications);
        textDepartments = findViewById(R.id.text_departments);

        tagEditTags = findViewById(R.id.edit_tags);

        //INIT NEW LESSON
        lesson = new Lesson();
        setLesson();
        lesson.initMultimediaFiles();

        //INIT CONSTANTS
        constants = new General();

        mStartRecording = true;
        linearLayoutTriggers = findViewById(R.id.linear_triggers);
        linearLayoutTriggers.setClickable(false);
        btnTriggerError = findViewById(R.id.btn_trigger_trerror);
        btnTriggerOmision = findViewById(R.id.btn_trigger_omision);
        btnTriggerGoodPractice = findViewById(R.id.btn_trigger_good_practices);
        btnTriggerImprovement = findViewById(R.id.btn_trigger_improvement);

        btnTriggerError.setClickable(true);
        btnTriggerError.setTag("true");
        btnTriggerError.setBackgroundColor(Color.parseColor("#f7772f"));
        btnTriggerOmision.setClickable(true);
        btnTriggerGoodPractice.setClickable(true);
        btnTriggerImprovement.setClickable(true);
        setLinearTriggersButtonClick();


        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.B_LESSON_ARRAY_LIST, lesson.getFormAttributes());
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LessonEditFragment lessonEditFragment = new LessonEditFragment();
        lessonEditFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.constraint_fragment_container,lessonEditFragment);
        fragmentTransaction.commit();
        constraintActionBar.setVisibility(View.VISIBLE);
        fabSend.setVisibility(View.VISIBLE);
        // Record to the external cache directory for visibility
        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(LessonFormActivity.this));
        transferUtility = new TransferUtility(s3, LessonFormActivity.this);

        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        setTextGalleryOnClickListener();
        setFabSendOnClickListener();
        setFabRecordAudioOnClickListener();
        setTextRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setTextFilesOnClickListener();
        setFabVideoOnClickListener();
        setTextVideoOnClickListener();
        setFabSaveOnClickListener();

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mPicturesRecyclerView = findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(lesson.getMultimediaPicturesFiles(), LessonFormActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mVideosRecyclerView = findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(lesson.getMultimediaVideosFiles(), LessonFormActivity.this);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mAudiosRecyclerView = findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(lesson.getMultimediaAudiosFiles(), LessonFormActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mDocumentsRecyclerView = findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(lesson.getMultimediaDocumentsFiles(), LessonFormActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        //DISCIPLINES SCROLLING
        LinearLayoutManager disciplinesLayoutManager = new LinearLayoutManager(this);
        disciplinesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDisciplinesRecyclerView = findViewById(R.id.recycler_horizontal_disciplines);
        mDisciplinesRecyclerView.setLayoutManager(disciplinesLayoutManager);
        String[] disciplinesArray =  sessionManager.getDisciplines();
        //String[] disciplinesArray =  lesson.getDisciplinesArray();
        if (disciplinesArray.length != 0) {
            disciplinesAttributesAdapter = new LessonAttributesAdapter(disciplinesArray,
                    LessonFormActivity.this, lesson, true, Constants.TAG_DISCIPLINES);
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
        String[] classificationsArray =  sessionManager.getClassifications();
        //String[] classificationsArray =  lesson.getClassificationsArray();
        if (classificationsArray.length != 0) {
            classificationsAttributesAdapter = new LessonAttributesAdapter(classificationsArray,
                    LessonFormActivity.this, lesson, true, Constants.TAG_CLASSIFICATIONS);
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
        String[] departmentsArray =  sessionManager.getDepartments();
        //String[] departmentsArray =  lesson.getDepartmentsArray();
        if (departmentsArray.length != 0) {
            departmentsAttributesAdapter = new LessonAttributesAdapter(departmentsArray,
                    LessonFormActivity.this, lesson, true, Constants.TAG_DEPARTMENTS);
            mDepartmentsRecyclerView.setAdapter(departmentsAttributesAdapter);
        } else {
            textDepartments.setText("Departamento (no hay departamentos asignados)");
            mDepartmentsRecyclerView.setVisibility(View.GONE);
        }
    }

    public void setFabSendOnClickListener() {
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.lesson_form_layout),
                        "Confirme que desea enviar la lección a validar", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("Confirmar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createLesson(Constants.R_WAITING);
                    }
                });
                mySnackbar.show();
            }
        });
    }

    public void setFabSaveOnClickListener() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.lesson_form_layout),
                        "Confirme que desea guardar la lección en borradores", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("Confirmar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createLesson(Constants.R_SAVED);
                    }
                });
                mySnackbar.show();
            }
        });
    }

    public void createLesson(String validateState) {
        SessionManager sessionManager = new SessionManager(LessonFormActivity.this);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.constraint_fragment_container);
        EditText editLessonName = fragment.getView().findViewById(R.id.new_section_name);
        EditText editLessonSummary = fragment.getView().findViewById(R.id.edit_lesson_summary);
        EditText editLessonMotivation = fragment.getView().findViewById(R.id.edit_lesson_motivation);
        EditText editLessonLearning = fragment.getView().findViewById(R.id.edit_lesson_learning);

        String lesson_name = editLessonName.getText().toString();
        String lesson_summary = editLessonSummary.getText().toString();
        String lesson_motivation = editLessonMotivation.getText().toString();
        String lesson_learning = editLessonLearning.getText().toString();
        lesson.setTrigger_id(linearTriggersGetTriggerId());
        String project_id = sessionManager.getActualProjectId();

        new VolleyCreateLesson(new VolleyJSONCallback() {
              @Override
              public void onSuccess(JSONObject result) {
                  if(!lesson.isEmptyMultimedia()) {

                      try {
                          final String new_lesson_id = result.get("id").toString();

                          String path_input = lesson.getMultimediaFileKeys(new_lesson_id);

                          VolleyPostS3.volleyPostS3(new VolleyJSONCallback() {
                              @Override
                              public void onSuccess(JSONObject result) {
                                  Toast.makeText(LessonFormActivity.this, "Nueva lección creada", Toast.LENGTH_LONG).show();

                                  for (MultimediaFile multimediaFile : lesson.getMultimediaPicturesFiles()) {
                                      Log.i("UPLOADINGMULTIMEDIA",multimediaFile.getApiFileKey());
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile : lesson.getMultimediaAudiosFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile : lesson.getMultimediaDocumentsFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  for (MultimediaFile multimediaFile : lesson.getMultimediaVideosFiles()) {
                                      multimediaFile.initUploadThread();
                                  }
                                  startActivity(MainActivity.getIntent(LessonFormActivity.this));
                              }

                              @Override
                              public void onErrorResponse(VolleyError result) {
                              }
                          }, LessonFormActivity.this, new_lesson_id, path_input.split(";"));
                      } catch (Exception e) {
                          Toast.makeText(LessonFormActivity.this, "Error con archivos multimedia", Toast.LENGTH_LONG).show();
                      }
                  } else {
                      Toast.makeText(LessonFormActivity.this, "Nueva lección creada", Toast.LENGTH_LONG).show();
                      startActivity(MainActivity.getIntent(LessonFormActivity.this));
                  }
              }
              @Override
              public void onErrorResponse(VolleyError result) {
                  Toast.makeText(LessonFormActivity.this, "No se pudo crear lección. Revisar que todos los campos estén completos", Toast.LENGTH_LONG).show();
              }
          }, LessonFormActivity.this, lesson_name, lesson_summary,
        lesson_motivation, lesson_learning,project_id, validateState,
                tagEditTags.getText().toString(),
                (disciplinesAttributesAdapter!=null) ? disciplinesAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                (classificationsAttributesAdapter!=null) ? classificationsAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                (departmentsAttributesAdapter!=null) ? departmentsAttributesAdapter.getSelectedAttributes() : new ArrayList<String>(),
                lesson
                ).execute();

        if (!Connectivity.isConnected(LessonFormActivity.this)) {
            Toast.makeText(LessonFormActivity.this, "Estás sin conexión, cuando se conecte se enviará automáticamente", Toast.LENGTH_LONG).show();

            killActivity();
        }
    }

    public void killActivity() {
        finish();
    }

}
