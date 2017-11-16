package com.construapp.construapp.microblog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeletePost;
import com.construapp.construapp.api.VolleyDeleteSection;
import com.construapp.construapp.api.VolleyDeleteThread;
import com.construapp.construapp.api.VolleyGetThread;
import com.construapp.construapp.api.VolleyPostPosts;
import com.construapp.construapp.api.VolleyPutPost;
import com.construapp.construapp.api.VolleyPutSection;
import com.construapp.construapp.api.VolleyPutThread;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Post;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jose on 09-11-17.
 */

public class ThreadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ListView postsListListView;
    private ArrayList<Post> postsList;
    private PostsAdapter postsAdapter;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private String[] mProjectTitles;
    private ViewPager sectionsView;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button deleteThreadButton;
    private Button editThreadButton;
    private Button saveEditThreadButton;
    private CoordinatorLayout threadCommentsLayout;
    private LinearLayout editThreadLayout;

    private String title;
    private String text;
    private String fullname;
    private String position;
    private String thread_id;
    private String timestamp;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;
    private General general;

    private TextView threadtext;
    private TextView userName;
    private TextView threadTimestamp;
    private TextView userPosition;

    private LinearLayout mContainerView;

    private AppCompatButton createPost;
    private EditText newComment;
    private EditText editThreadName;
    private EditText editThreadDescription;

    private ConstraintLayout constraintLayoutFvourites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        title = getIntent().getStringExtra("TITLE");
        text = getIntent().getStringExtra("TEXT");
        fullname = "";
        position = "";
        timestamp = "";
        thread_id=getIntent().getStringExtra("ID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.POST);
        setSupportActionBar(toolbar);

        userName = findViewById(R.id.textview_fullname);
        userName.setText(fullname);

        threadTimestamp = findViewById(R.id.textview_post_timestamp);
        threadTimestamp.setText(timestamp);

        userPosition = findViewById(R.id.textview_position);
        userPosition.setText(position);

        editThreadButton = findViewById(R.id.btn_edit);
        deleteThreadButton = findViewById(R.id.btn_delete);

        threadCommentsLayout = findViewById(R.id.main_content);
        editThreadLayout = findViewById(R.id.layout_edit_thread_form);

        editThreadName = findViewById(R.id.text_edit_thread_name);
        editThreadDescription = findViewById(R.id.text_edit_thread_description);

        saveEditThreadButton = findViewById(R.id.button_edit_thread);

        deleteThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

        editThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadCommentsLayout.setVisibility(View.GONE);
                editThreadName.setText(title);
                editThreadDescription.setText(text);
                editThreadLayout.setVisibility(View.VISIBLE);
            }
        });
        saveEditThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyPutThread.volleyPutThread(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        title = editThreadName.getText().toString();
                        text = editThreadDescription.getText().toString();
                        editThreadLayout.setVisibility(View.GONE);
                        threadCommentsLayout.setVisibility(View.VISIBLE);
                        onRestart();
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                        Toast.makeText(getApplicationContext(),"No se pudo editar el post.",Toast.LENGTH_SHORT).show();
                    }
                },ThreadActivity.this,editThreadName.getText().toString(),editThreadDescription.getText().toString(),thread_id);
            }
        });

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

        sessionManager = new SessionManager(ThreadActivity.this);
        sessionManager.setThreadId(thread_id);

        postsListListView = findViewById(R.id.posts_list);
        postsAdapter = new PostsAdapter(getApplicationContext(), postsList);

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

        navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.main_username);
        mUserName.setText(sessionManager.getActualProjectName());

        //back toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_ham));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

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

    public void setSwipeRefreshLayout() {
        swipeRefreshListener = (new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                                updateTextView(user.get("first_name").toString()+ " "+ user.get("last_name").toString(),R.id.textview_fullname);
                                updateTextView(user.get("position").toString(),R.id.textview_position);
                                updateTextView(title,R.id.textview_thread_title);
                                updateTextView(text,R.id.textview_post_message);
                                updateTextView("Comentarios",R.id.textview_comments_tag);
                                //updateTextView(user.get("timestamp").toString(),R.id.textview_post_timestamp);

                                jsonPosts = new JSONArray(request.getString("posts"));
                                //postsList.clear();

                                mContainerView = (LinearLayout)findViewById(R.id.linear_layout_posts);
                                mContainerView.removeAllViews();

                                for (int i = 0; i < jsonPosts.length(); i++) {
                                    //post = new Post();
                                    LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View myView = inflater.inflate(R.layout.thread_comments_list_item, null);

                                    Log.i("JSON", jsonPosts.get(i).toString());
                                    final JSONObject object = (JSONObject) jsonPosts.get(i);
                                    //post.setText(object.get("text").toString());
                                    //post.setId(object.get("id").toString());
                                    final String post_id = object.get("id").toString();
                                    JSONObject object2 = new JSONObject(object.getString("user"));
                                    TextView fullname = myView.findViewById(R.id.textview_fullname);
                                    fullname.setText(object2.getString("first_name")+" "+object2.getString("last_name"));

                                    TextView timestamp = myView.findViewById(R.id.textview_post_timestamp);
                                    timestamp.setText(object.getString("updated_at"));

                                    TextView position = myView.findViewById(R.id.textview_position);
                                    position.setText(object2.getString("position"));

                                    final TextView text = myView.findViewById(R.id.textview_text);
                                    text.setText(object.getString("text"));

                                    final EditText editText = myView.findViewById(R.id.edittext_text);
                                    editText.setText(object.getString("text"));

                                    final Button edit = myView.findViewById(R.id.btn_edit);
                                    final Button delete = (Button) myView.findViewById(R.id.btn_delete);
                                    final Button update = myView.findViewById(R.id.btn_update);

                                    mContainerView.addView(myView);

                                    delete.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {

                                            VolleyDeletePost.volleyDeletePost(new VolleyStringCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    onRestart();
                                                }

                                                @Override
                                                public void onErrorResponse(VolleyError result) {
                                                    Toast.makeText(getApplicationContext(),"No se pudo eliminar el comentario.",Toast.LENGTH_SHORT).show();
                                                }
                                            },ThreadActivity.this,post_id.toString().toString());
                                        }

                                    });

                                    edit.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {

                                            edit.setVisibility(View.GONE);
                                            delete.setVisibility(View.GONE);
                                            text.setVisibility(View.GONE);
                                            update.setVisibility(View.VISIBLE);
                                            editText.setVisibility(View.VISIBLE);
                                            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    if(hasFocus){
                                                        editText.setSelection(editText.getText().length());
                                                    }
                                                }
                                            });
                                            editText.requestFocus();
                                        }

                                    });
                                    update.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {

                                            VolleyPutPost.volleyPutPost(new VolleyStringCallback() {
                                                @Override
                                                public void onSuccess(String result) {
                                                    edit.setVisibility(View.VISIBLE);
                                                    delete.setVisibility(View.VISIBLE);
                                                    text.setVisibility(View.VISIBLE);
                                                    update.setVisibility(View.GONE);
                                                    editText.setVisibility(View.GONE);
                                                    onRestart();
                                                }

                                                @Override
                                                public void onErrorResponse(VolleyError result) {
                                                    Toast.makeText(getApplicationContext(),"No se pudo editar el comentario.",Toast.LENGTH_SHORT).show();
                                                }
                                            },ThreadActivity.this,editText.getText().toString(),Integer.parseInt(post_id));

                                            edit.setVisibility(View.GONE);
                                            delete.setVisibility(View.GONE);
                                            text.setVisibility(View.GONE);
                                            update.setVisibility(View.VISIBLE);
                                            editText.setVisibility(View.VISIBLE);


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
                                                Toast.makeText(getApplicationContext(),"No se pudo publicar el comentario.",Toast.LENGTH_SHORT).show();
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
                    }, getApplicationContext());
                } else {

                }
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    @Override
    public void onBackPressed()
    {
        if (navigationView.isShown())
        {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            sessionManager.clearThreadId();
            finish();
        }
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
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
        return myQuittingDialogBox;

    }

    public void updateTextView(String toThis,int tv) {
        TextView textView = (TextView) findViewById(tv);
        textView.setText(toThis);
    }
}