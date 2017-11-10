package com.construapp.construapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetSections;
import com.construapp.construapp.api.VolleyGetThread;
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Post;
import com.construapp.construapp.models.Section;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.lessons.LessonViewFragment;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.microblog.MicroblogFragment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Section;
import com.construapp.construapp.models.SessionManager;
import com.construapp.construapp.models.Threadblog;

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
        //todo jose VAMOS A PROBAR SI QUEDA BIEN EL TITULO
        //toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        //todo jose terminar de pasar el codigo

        //TextView threadTitle = findViewById(R.id.textview_thread_title);
        //threadTitle.setText(title);

        //threadtext = findViewById(R.id.textview_post_message);
        //threadtext.setText(text);

        userName = findViewById(R.id.textview_fullname);
        userName.setText(fullname);

        threadTimestamp = findViewById(R.id.textview_post_timestamp);
        threadTimestamp.setText(timestamp);

        userPosition = findViewById(R.id.textview_position);
        userPosition.setText(position);

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
                //
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
            //startActivity(MicroblogActivity.getIntent(FavouriteLessonsActivity.this));
        } //else  if (item.getItemId() == R.id.to_favourites) {
        //startActivity(FavouriteLessonsActivity.getIntent(FavouriteLessonsActivity.this));
        //IMPLEMENT
        //}
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
                                postsList.clear();

                                for (int i = 0; i < jsonPosts.length(); i++) {
                                    post = new Post();
                                    Log.i("JSON", jsonPosts.get(i).toString());
                                    JSONObject object = (JSONObject) jsonPosts.get(i);
                                    post.setText(object.get("text").toString());
                                    post.setId(object.get("id").toString());
                                    JSONObject object2 = new JSONObject(object.getString("user"));
                                    post.setTimestamp(object2.getString("created_at"));
                                    post.setPosition(object2.getString("position"));
                                    post.setFirst_name(object2.getString("first_name"));
                                    post.setLast_name(object2.getString("last_name"));

                                    postsList.add(post);

                                }
                                Log.i("REQ","HACIENDO REQ");
                                postsAdapter = new PostsAdapter(getApplicationContext(), postsList);
                                postsListListView.setAdapter(postsAdapter);
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

        sessionManager.clearThreadId();
        //super.onBackPressed();
        //overridePendingTransition(R.anim.animation_back1,R.anim.animation_back2);
        finish();

    }

    public void updateTextView(String toThis,int tv) {
        TextView textView = (TextView) findViewById(tv);
        textView.setText(toThis);
    }
}