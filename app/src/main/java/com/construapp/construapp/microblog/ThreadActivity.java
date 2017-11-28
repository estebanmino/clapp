package com.construapp.construapp.microblog;

import android.Manifest;
import android.app.Activity;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeletePost;
import com.construapp.construapp.api.VolleyDeleteThread;
import com.construapp.construapp.api.VolleyGetThread;
import com.construapp.construapp.api.VolleyPostPosts;
import com.construapp.construapp.api.VolleyPutPost;
import com.construapp.construapp.api.VolleyPutThread;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.lessons.LessonActivity;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jose on 09-11-17.
 */

public class ThreadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

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


    private ListView postsListListView;
    private ArrayList<Post> postsList;
    private PostsAdapter postsAdapter;

    private SessionManager sessionManager;
    private String[] mProjectTitles;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button deleteThreadButton;
    private Button editThreadButton;
    private CoordinatorLayout threadCommentsLayout;

    private String threadTitle;
    private String threadText;
    private String fullname;
    private String position;
    private String thread_id;
    private String timestamp;
    private String userThreadId;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;
    private General general;

    private TextView userName;
    private TextView threadTimestamp;
    private TextView userPosition;

    private LinearLayout mContainerView;

    private AppCompatButton createPost;
    private EditText newComment;


    private LinearLayout linearThreadComments;
    private LinearLayout linearAuthorInformation;
    private LinearLayout linearThreadContent;
    private LinearLayout linearThreadEdition;
    private ThreadBlog threadBlog;

    private TextView textImages;
    private TextView textVideos;
    private TextView textDocuments;
    private TextView textAudios;
    private TextView textAttachments;
    private TextView textThreadText;
    private TextView textThreadTitle;

    private EditText editTitle;
    private EditText editText;

    //MM ADAPTER
    public MultimediaPictureAdapter multimediaPictureAdapter;
    public MultimediaVideoAdapter multimediaVideoAdapter;
    public MultimediaAudioAdapter multimediaAudioAdapter;
    public MultimediaDocumentAdapter multimediaDocumentAdapter;

    private RecyclerView mPicturesRecyclerView;
    private RecyclerView mVideosRecyclerView;
    private RecyclerView mDocumentsRecyclerView;
    private RecyclerView mAudiosRecyclerView;

    private General constants;
    private  TransferUtility transferUtility;

    private String CACHE_FOLDER;
    private int notAdded = 0;
    private int added = 1;

    private Boolean editing = false;

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

    public FloatingActionButton fabCamera;
    public FloatingActionButton fabGallery;
    public FloatingActionButton fabRecordAudio;
    public FloatingActionButton fabFiles;
    public FloatingActionButton fabSend;
    public FloatingActionButton fabVideo;
    public FloatingActionButton fabSave;
    public ImageView fabAttach;
    public TextView textRecording;
    public ConstraintLayout constraintMultimediaBar;
    public ConstraintLayout constraintActionBar;

    private Boolean multimediaIsOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        threadTitle = getIntent().getStringExtra("TITLE");
        threadText = getIntent().getStringExtra("TEXT");
        userThreadId = getIntent().getStringExtra("THREAD_USER");
        fullname = "";
        position = "";
        timestamp = "";
        thread_id=getIntent().getStringExtra("ID");
        sessionManager = new SessionManager(ThreadActivity.this);

        threadBlog = new ThreadBlog();
        threadBlog.initMultimediaFiles();
        getThreadBlog();



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.POST);
        setSupportActionBar(toolbar);

        // Record to the external cache directory for visibility
        ABSOLUTE_STORAGE_PATH = getExternalCacheDir().getAbsolutePath();
        mRecordFileName = ABSOLUTE_STORAGE_PATH + "/audiorecordtest.3gp";

        userName = findViewById(R.id.textview_fullname);

        threadTimestamp = findViewById(R.id.textview_post_timestamp);

        userPosition = findViewById(R.id.textview_position);

        editThreadButton = findViewById(R.id.btn_edit);
        deleteThreadButton = findViewById(R.id.btn_delete);

        threadCommentsLayout = findViewById(R.id.main_content);

        linearThreadComments = findViewById(R.id.linear_thread_comments);
        linearAuthorInformation = findViewById(R.id.linear_author_information);
        linearThreadContent = findViewById(R.id.linear_thread_content);
        linearThreadEdition = findViewById(R.id.linear_thread_edition);

        mContainerView = (LinearLayout)findViewById(R.id.linear_layout_posts);

        textImages = findViewById(R.id.text_images);
        textVideos = findViewById(R.id.text_videos);
        textDocuments = findViewById(R.id.text_documents);
        textAudios = findViewById(R.id.text_audios);
        textAttachments = findViewById(R.id.text_attachments);
        textThreadText = findViewById(R.id.textview_post_message);
        textThreadTitle = findViewById(R.id.textview_thread_title);

        editTitle = findViewById(R.id.edit_title);
        editText = findViewById(R.id.edit_text);


        fabCamera = findViewById(R.id.fab_camera);
        fabGallery = findViewById(R.id.fab_gallery);
        fabRecordAudio = findViewById(R.id.fab_record_audio);
        fabSend = findViewById(R.id.fab_send);
        fabFiles = findViewById(R.id.fab_files);
        fabVideo = findViewById(R.id.fab_video);
        fabSave = findViewById(R.id.fab_save);
        textRecording = findViewById(R.id.text_recording);

        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);
        constraintActionBar = findViewById(R.id.constraint_action_bar);
        constraintMultimediaBar = findViewById(R.id.constraint_multimedia_bar);
        fabAttach = findViewById(R.id.image_attach);
        setImageAttachListener();


        if (sessionManager.getUserId().equals(userThreadId) ||
                sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)){
            editThreadButton.setVisibility(View.VISIBLE);
            deleteThreadButton.setVisibility(View.VISIBLE);
        }
        else {
            editThreadButton.setVisibility(View.GONE);
            deleteThreadButton.setVisibility(View.GONE);
        }

        deleteThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog diaBox = AskOptionThread();
                diaBox.show();
            }
        });

        editThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadBlog.setSavedMultimediaFileKeys();

                editing = !editing;
                //startActivity(ThreadEditActivity.getIntent(ThreadActivity.this));
                linearThreadComments.setVisibility(View.GONE);
                linearAuthorInformation.setVisibility(View.GONE);
                linearThreadContent.setVisibility(View.GONE);
                linearThreadEdition.setVisibility(View.VISIBLE);

                constraintActionBar.setVisibility(View.VISIBLE);

                editTitle.setText(threadTitle);
                editText.setText(threadText);

                mDocumentsRecyclerView.setVisibility(View.VISIBLE);
                mPicturesRecyclerView.setVisibility(View.VISIBLE);
                mVideosRecyclerView.setVisibility(View.VISIBLE);
                mAudiosRecyclerView.setVisibility(View.VISIBLE);

                multimediaDocumentAdapter = new MultimediaDocumentAdapter(threadBlog.getMultimediaDocumentsFiles(),ThreadActivity.this);
                mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);
                multimediaAudioAdapter.notifyDataSetChanged();
                multimediaPictureAdapter = new MultimediaPictureAdapter(threadBlog.getMultimediaPictureFiles(),ThreadActivity.this);
                mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);
                multimediaPictureAdapter.notifyDataSetChanged();
                multimediaVideoAdapter = new MultimediaVideoAdapter(threadBlog.getMultimediaVideosFiles(),ThreadActivity.this);
                mVideosRecyclerView.setAdapter(multimediaVideoAdapter);
                multimediaAudioAdapter = new MultimediaAudioAdapter(threadBlog.getMultimediaAudioFiles(),ThreadActivity.this);
                mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);
            }
        });

        sessionManager.setThreadId(thread_id);

        try {
            jsonArray = new JSONArray(sessionManager.getProjects());
            mProjectTitles = new String[(jsonArray.length())];

            projects = new HashMap<String, String>();
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject project = (JSONObject) jsonArray.get(i);
                projects.put(project.getString("name"),project.getString("id"));
                mProjectTitles[i] = project.getString("name");
            }
            projects.put(Constants.ALL_PROJECTS_NAME,Constants.ALL_PROJECTS_KEY);
        } catch (Exception e) {}

        general = new General();

        // Create an S3 client
        constants = new General();
        AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(ThreadActivity.this));
        transferUtility = new TransferUtility(s3, ThreadActivity.this);

        CACHE_FOLDER = ThreadActivity.this.getCacheDir().toString();

        postsList = new ArrayList<Post>();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_posts);
        setSwipeRefreshLayout();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        //showComments();

        sessionManager.setThreadId(thread_id);

        postsListListView = findViewById(R.id.posts_list);
        postsAdapter = new PostsAdapter(getApplicationContext(), postsList);
        postsListListView.setAdapter(postsAdapter);

        postsListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Post post = (Post) postsAdapter.getItem(position);
            }
        });

        postsListListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        ////HORIZONTAL IMAGES SCROLLING

        //GENRAL LAYOUT SCROLL
        //PICTURES SCROLLING
        LinearLayoutManager picturesLayoutManager = new LinearLayoutManager(this);
        picturesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPicturesRecyclerView = findViewById(R.id.recycler_horizontal_pictures);
        mPicturesRecyclerView.setLayoutManager(picturesLayoutManager);
        multimediaPictureAdapter = new MultimediaPictureAdapter(threadBlog.getMultimediaPictureFiles(),
                ThreadActivity.this);
        mPicturesRecyclerView.setAdapter(multimediaPictureAdapter);

        //VIDEOS SCROLLING
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosRecyclerView = findViewById(R.id.recycler_horizontal_videos);
        mVideosRecyclerView.setLayoutManager(videosLayoutManager);
        multimediaVideoAdapter = new MultimediaVideoAdapter(threadBlog.getMultimediaVideosFiles(),
                ThreadActivity.this);
        mVideosRecyclerView.setAdapter(multimediaVideoAdapter);

        //AUDIOS SCROLLING
        LinearLayoutManager audiosLayoutManager = new LinearLayoutManager(this);
        audiosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAudiosRecyclerView = findViewById(R.id.recycler_horizontal_audios);
        mAudiosRecyclerView.setLayoutManager(audiosLayoutManager);
        multimediaAudioAdapter = new MultimediaAudioAdapter(threadBlog.getMultimediaAudioFiles(),
                ThreadActivity.this);
        mAudiosRecyclerView.setAdapter(multimediaAudioAdapter);

        //DOCUMENTS SCROLLING
        LinearLayoutManager documentsLayoutManager = new LinearLayoutManager(this);
        documentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDocumentsRecyclerView = findViewById(R.id.recycler_horizontal_documents);
        mDocumentsRecyclerView.setLayoutManager(documentsLayoutManager);
        multimediaDocumentAdapter = new MultimediaDocumentAdapter(threadBlog.getMultimediaDocumentsFiles(),
                ThreadActivity.this);
        mDocumentsRecyclerView.setAdapter(multimediaDocumentAdapter);

        //SET BUTTONS LISTENER
        setFabCameraOnClickListener();
        setFabGalleryOnClickListener();
        setFabRecordAudioOnClickListener();
        setFabFilesOnClickListener();
        setFabVideoOnClickListener();
        setFabSendOnClickListener();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,ThreadActivity.class);
        return intent;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (item.getItemId()  == R.id.logout){
            sessionManager.eraseSharedPreferences();
            try {

            } catch (Exception e) {}
            try {
                new DeleteLessonTable(getApplicationContext()).execute().get();
                deleteDir(this.getCacheDir());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Toast.makeText(this,"Se ha  cerrado su sesión",Toast.LENGTH_LONG).show();
            startActivity(LoginActivity.getIntent(ThreadActivity.this));
        } else if (item.getItemId() == R.id.to_all_projects) {
            sessionManager.setActualProject(Constants.ALL_PROJECTS_KEY,Constants.ALL_PROJECTS_NAME);
            startActivity(MainActivity.getIntent(ThreadActivity.this));
        } else  if (item.getItemId() == R.id.to_blog) {

        }

        else {
            String map = item.getTitle().toString();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            sessionManager.setActualProject(Integer.toString(item.getItemId()),map);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public void getThreadBlog() {

        VolleyGetThread.volleyGetThread(new VolleyStringCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("ACTUALTHREAD",result);
                try{
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(result.toString()).getAsJsonObject();
                    ArrayList<String> arrayList = new ArrayList<>();
                    threadBlog.setId((String) json.get("id").getAsString());

                    textThreadTitle.setText(json.get("title").getAsString());
                    textThreadText.setText(json.get("text").getAsString());
                    JsonObject jsonUser = json.get("user").getAsJsonObject();
                    final String firstName, lastName;
                    if (!jsonUser.get("first_name").toString().equals("null")){
                        firstName = jsonUser.get("first_name").getAsString();
                    } else {
                        firstName = "";
                    }
                    if (!jsonUser.get("last_name").toString().equals("null")){
                        lastName = jsonUser.get("last_name").getAsString();
                    } else {
                        lastName = "";
                    }
                    userName.setText(firstName + " "+ lastName );
                    userPosition.setText(jsonUser.get("position").getAsString());

                    Log.i("THREADFILES",json.get("thread_files").toString());
                    JsonArray jsonArray = (JsonArray) json.get("thread_files").getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Log.i("FILEKEY",jsonArray.get(i).toString());
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
                        threadBlog.getMultimediaPictureFiles().add(
                                new MultimediaFile(
                                        Constants.S3_THREADS_PATH,
                                        Constants.S3_IMAGES_PATH,
                                        CACHE_FOLDER+ "/"+path.substring(path.lastIndexOf("/")+1,path.length()-1),
                                        transferUtility,
                                        threadBlog.getId(),
                                        notAdded));
                    }
                    multimediaPictureAdapter.notifyDataSetChanged();


                    for (String audioPath: audioPathsList) {
                        threadBlog.getMultimediaAudioFiles().add(
                                new MultimediaFile(
                                        Constants.S3_THREADS_PATH,
                                        Constants.S3_AUDIOS_PATH,
                                        CACHE_FOLDER+ "/"+audioPath.substring(audioPath.lastIndexOf("/")+1,audioPath.length()-1),
                                        transferUtility,
                                        threadBlog.getId(),
                                        notAdded));
                    }
                    multimediaAudioAdapter.notifyDataSetChanged();

                    for (String documentPath: documentPathsList) {
                        threadBlog.getMultimediaDocumentsFiles().add(
                                new MultimediaFile(
                                        Constants.S3_THREADS_PATH,
                                        Constants.S3_DOCS_PATH,
                                        ABSOLUTE_STORAGE_PATH+ "/"+documentPath.substring(documentPath.lastIndexOf("/")+1,documentPath.length()-1),
                                        transferUtility,
                                        threadBlog.getId(),
                                        notAdded));

                    }
                    multimediaDocumentAdapter.notifyDataSetChanged();

                    for (String videoPath: videosPathsList) {
                        threadBlog.getMultimediaVideosFiles().add(
                                new MultimediaFile(
                                        Constants.S3_THREADS_PATH,
                                        Constants.S3_VIDEOS_PATH,
                                        ABSOLUTE_STORAGE_PATH+ "/"+videoPath.substring(videoPath.lastIndexOf("/")+1,videoPath.length()-1),
                                        transferUtility,
                                        threadBlog.getId(),
                                        notAdded));
                    }
                    multimediaVideoAdapter.notifyDataSetChanged();

                    if (!threadBlog.hasMultimediaFiles()) {
                        //linearLayoutMultimedia.setVisibility(View.GONE);
                        textAttachments.setText(Constants.NO_ATTACHMENTS);
                    } else {
                        if (threadBlog.getMultimediaPictureFiles().isEmpty()){
                            textImages.setText(Constants.NO_PICTURES);
                            mPicturesRecyclerView.setVisibility(View.GONE);
                        }
                        if (threadBlog.getMultimediaAudioFiles().isEmpty()){
                            textAudios.setText(Constants.NO_AUDIOS);
                            mAudiosRecyclerView.setVisibility(View.GONE);
                        }
                        if (threadBlog.getMultimediaDocumentsFiles().isEmpty()){
                            textDocuments.setText(Constants.NO_DOCUMENTS);
                            mDocumentsRecyclerView.setVisibility(View.GONE);
                        }
                        if (threadBlog.getMultimediaVideosFiles().isEmpty()){
                            textVideos.setText(Constants.NO_VIDEOS);
                            mVideosRecyclerView.setVisibility(View.GONE);
                        }
                    }
                }
                catch (Exception e) {}
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        }, ThreadActivity.this, getIntent().getStringExtra("ID"));
    }

    public Boolean getEditing() {return editing;}

    public void setFabSendOnClickListener(){
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MultimediaFile> selectedMultimediaFiles = new ArrayList<>();
                selectedMultimediaFiles.addAll(multimediaPictureAdapter.getmMultimediaFiles());
                selectedMultimediaFiles.addAll(multimediaAudioAdapter.getmMultimediaFiles());
                selectedMultimediaFiles.addAll(multimediaDocumentAdapter.getmMultimediaFiles());
                selectedMultimediaFiles.addAll(multimediaVideoAdapter.getmMultimediaFiles());

                VolleyPutThread.volleyPutThread(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.i("PUTRESULT", result);
                        Toast.makeText(ThreadActivity.this, "Post editado", Toast.LENGTH_LONG).show();
                        String start = Constants.S3_THREADS_PATH+"/"+threadBlog.getId()+"/";

                        for (MultimediaFile multimediaFile: threadBlog.getMultimediaPictureFiles()) {
                            if (multimediaFile.getAdded() == 1) {
                                multimediaFile.setExtension(start+Constants.S3_IMAGES_PATH);
                                multimediaFile.initUploadThread();
                            }
                        }
                        for (MultimediaFile multimediaFile: threadBlog.getMultimediaAudioFiles()) {
                            if (multimediaFile.getAdded() == 1) {
                                multimediaFile.setExtension(start+Constants.S3_AUDIOS_PATH);
                                multimediaFile.initUploadThread();
                            }
                        }
                        for (MultimediaFile multimediaFile: threadBlog.getMultimediaDocumentsFiles()) {
                            if (multimediaFile.getAdded() == 1) {
                                multimediaFile.setExtension(start+Constants.S3_DOCS_PATH);
                                multimediaFile.initUploadThread();
                            }
                        }
                        for (MultimediaFile multimediaFile: threadBlog.getMultimediaVideosFiles()) {
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

                    }
                }, ThreadActivity.this, threadTitle, threadText, threadBlog.getId(), selectedMultimediaFiles, threadBlog);
            }
        });
    };


    public void setImageAttachListener() {
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

    public void setFabFilesOnClickListener() {
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
                if (ContextCompat.checkSelfPermission(ThreadActivity.this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        textRecording.setVisibility(View.VISIBLE);
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_NORMAL);
                        fabFiles.setVisibility(View.GONE);
                        fabGallery.setVisibility(View.GONE);
                        fabCamera.setVisibility(View.GONE);
                        fabSend.setVisibility(View.GONE);
                        fabVideo.setVisibility(View.GONE);
                        fabAttach.setVisibility(View.GONE);
                    }
                    else{
                        fabRecordAudio.setSize(FloatingActionButton.SIZE_MINI);
                        textRecording.setVisibility(View.GONE);
                        fabFiles.setVisibility(View.VISIBLE);
                        fabGallery.setVisibility(View.VISIBLE);
                        fabCamera.setVisibility(View.VISIBLE);
                        fabSend.setVisibility(View.VISIBLE);
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

                if (ContextCompat.checkSelfPermission(ThreadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
                if (ContextCompat.checkSelfPermission(ThreadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(ThreadActivity.this, Manifest.permission.CAMERA)
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
                if (ContextCompat.checkSelfPermission(ThreadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    getWriteStoragePermissions();
                }
                else if (ContextCompat.checkSelfPermission(ThreadActivity.this, Manifest.permission.CAMERA)
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
                    if (ContextCompat.checkSelfPermission(ThreadActivity.this,
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
                    if (ContextCompat.checkSelfPermission(ThreadActivity.this,
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
                                    mPath,
                                    transferUtility,
                                    threadBlog.getId(),
                                    added));
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
                                    mPath,
                                    transferUtility,
                                    threadBlog.getId(),
                                    added));
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
                                        mPath,
                                        transferUtility,
                                        threadBlog.getId(),
                                        added));
                        multimediaPictureAdapter.notifyDataSetChanged();


                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Toast.makeText(ThreadActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
                                    mPath,
                                    transferUtility,
                                    threadBlog.getId(),
                                    added));
                    multimediaDocumentAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }


    public void getReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(ThreadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ThreadActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que se vean las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(ThreadActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_REQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ThreadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getWriteStoragePermissions() {
        if (ContextCompat.checkSelfPermission(ThreadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ThreadActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos guardar las fotos, necesitamos acceso a su almacenamiento.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(ThreadActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_REEQUEST);
                    }
                }).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ThreadActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_REEQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getCameraPermissions() {
        if (ContextCompat.checkSelfPermission(ThreadActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ThreadActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos tomar las fotos, necesitamos acceso a su cámara.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(ThreadActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ThreadActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void getRecorAudioPermissions() {
        if (ContextCompat.checkSelfPermission(ThreadActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ThreadActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(mLayout, "Para que podamos grabar audio, necesitamos permiso.",
                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        ActivityCompat.requestPermissions(ThreadActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                RECORD_AUDIO_REQUEST);
                    }
                }).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ThreadActivity.this,
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
                    Constants.S3_THREADS_PATH,
                    Constants.S3_AUDIOS_PATH,
                    ABSOLUTE_STORAGE_PATH + ts.toString() + EXTENSION_AUDIO_FORMAT,
                    transferUtility,
                    threadBlog.getId(),
                    added);

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

    public void setSwipeRefreshLayout() {
        swipeRefreshListener = (new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showComments();
            }
        });
    }

    private void showComments(){
        boolean is_connected = Connectivity.isConnected(getApplicationContext());
        if(is_connected) {
            VolleyGetThread.volleyGetThread(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    Post post;
                    JSONArray jsonPosts;
                    try {
                        JSONObject request = new JSONObject(result);
                        JSONObject user = new JSONObject(request.getString("user"));

                        jsonPosts = new JSONArray(request.getString("posts"));
                        //postsList.clear();

                        mContainerView.removeAllViews();

                        for (int i = 0; i < jsonPosts.length(); i++) {
                            //post = new Post();
                            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View myView = inflater.inflate(R.layout.thread_comments_list_item, null);

                            final JSONObject object = (JSONObject) jsonPosts.get(i);
                            final String post_id = object.get("id").toString();
                            JSONObject object2 = new JSONObject(object.getString("user"));
                            final String postUserId = object2.get("id").toString();

                            final Button editPostButton = myView.findViewById(R.id.btn_edit);
                            final Button deletePostButton = (Button) myView.findViewById(R.id.btn_delete);
                            final Button updatePostButton = myView.findViewById(R.id.btn_update);

                            final TextView textPost = myView.findViewById(R.id.text_post);
                            final TextView textPostTimestamp = myView.findViewById(R.id.text_post_timestamp);
                            final TextView textPostAuthorFullName = myView.findViewById(R.id.text_post_author_fullname);
                            final TextView textPostAuthorPosition = myView.findViewById(R.id.text_post_author_position);

                            final EditText editPostText = myView.findViewById(R.id.edit_post_text);

                            String authorFullName = object2.getString("first_name")+" "+object2.getString("last_name");
                            textPost.setText(object.getString("text"));
                            textPostTimestamp.setText(object.getString("updated_at"));
                            textPostAuthorFullName.setText(authorFullName);
                            textPostAuthorPosition.setText(object2.getString("position"));


                            if (sessionManager.getUserId().equals(postUserId) ||
                                    sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)){
                                editPostButton.setVisibility(myView.VISIBLE);
                                deletePostButton.setVisibility(myView.VISIBLE);
                            }
                            else {
                                editPostButton.setVisibility(myView.GONE);
                                deletePostButton.setVisibility(myView.GONE);
                            }

                            mContainerView.addView(myView);

                            deletePostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    AlertDialog diaBox = AskOptionPost(post_id);
                                    diaBox.show();
                                }

                            });

                            editPostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {

                                    editPostButton.setVisibility(View.GONE);
                                    deletePostButton.setVisibility(View.GONE);
                                    textPost.setVisibility(View.GONE);
                                    updatePostButton.setVisibility(View.VISIBLE);
                                    editPostText.setVisibility(View.VISIBLE);
                                    editPostText.setText(textPost.getText().toString());
                                    editPostText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            if(hasFocus){
                                                editPostText.setSelection(editPostText.getText().length());
                                            }
                                        }
                                    });
                                    editPostText.requestFocus();
                                }

                            });
                            updatePostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {

                                    VolleyPutPost.volleyPutPost(new VolleyStringCallback() {
                                        @Override
                                        public void onSuccess(String result) {
                                            editPostButton.setVisibility(View.VISIBLE);
                                            deletePostButton.setVisibility(View.VISIBLE);
                                            textPost.setVisibility(View.VISIBLE);
                                            updatePostButton.setVisibility(View.GONE);
                                            editPostText.setVisibility(View.GONE);
                                            onRestart();
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError result) {
                                            Toast.makeText(getApplicationContext(),"No se pudo editar el comentario.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    },ThreadActivity.this,editPostText.getText().toString(),Integer.parseInt(post_id));

                                    editPostButton.setVisibility(View.GONE);
                                    deletePostButton.setVisibility(View.GONE);
                                    textPost.setVisibility(View.GONE);
                                    updatePostButton.setVisibility(View.VISIBLE);
                                    editPostText.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View myView = inflater.inflate(R.layout.thread_new_comment, null);
                        mContainerView.addView(myView);

                        createPost = (AppCompatButton) myView.findViewById(R.id.button_newpost);
                        newComment = (EditText) myView.findViewById(R.id.edittext_new_comment);

                        createPost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Log.i("TEXT",newComment.getText().toString());
                                VolleyPostPosts.volleyPostPosts(new VolleyStringCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        onRestart();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError result) {
                                        Toast.makeText(getApplicationContext(),"No se pudo publicar el comentario."
                                                ,Toast.LENGTH_SHORT).show();
                                    }
                                },ThreadActivity.this,newComment.getText().toString());
                            }
                        });
                    } catch (Exception e) {
                    }
                }
                @Override
                public void onErrorResponse(VolleyError result) {

                }
            }, getApplicationContext(), getIntent().getStringExtra("ID"));
        } else {

        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private AlertDialog AskOptionThread()
    {
        AlertDialog myQuittingDialogBoxThread =new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Estás seguro que quieres eliminar este post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        VolleyDeleteThread.volleyDeleteThread(new VolleyJSONCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                sessionManager.setSection(result.toString());
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                                Log.i("ERROR","erroooooor");
                            }
                        },ThreadActivity.this,sessionManager.getSection(),thread_id);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBoxThread;

    }
    private AlertDialog AskOptionPost(final String post_id)
    {
        AlertDialog myQuittingDialogBoxPost =new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Estás seguro que quieres eliminar este comentario?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String postId = post_id;
                        VolleyDeletePost.volleyDeletePost(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {
                                onRestart();
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                                Toast.makeText(getApplicationContext(),"No se pudo eliminar el comentario.",Toast.LENGTH_SHORT).show();
                            }
                        },ThreadActivity.this,postId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBoxPost;

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });
    }

}