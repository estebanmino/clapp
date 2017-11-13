package com.construapp.construapp.microblog;

/**
 * Created by jose on 06-11-17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeleteFavouriteLesson;
import com.construapp.construapp.api.VolleyDeleteSection;
import com.construapp.construapp.api.VolleyGetThreads;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.construapp.construapp.models.Threadblog;

public class SectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ListView threadsListListView;
    private ArrayList<Threadblog> threadsList;
    private ThreadsAdapter threadsAdapter;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private String[] mProjectTitles;
    private ViewPager sectionsView;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String name;
    private String description;
    private String section_id;
    private Button editSectionButton;
    private Button deleteSectionButton;
    private FloatingActionButton newThread;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;
    private General general;


    private ConstraintLayout constraintLayoutFvourites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);


        name = getIntent().getStringExtra("NAME");
        description=getIntent().getStringExtra("DESCRIPTION");
        section_id=getIntent().getStringExtra("ID");

        sessionManager = new SessionManager(SectionActivity.this);
        sessionManager.setSection(section_id);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        TextView sectionDescription = findViewById(R.id.section_description);
        sectionDescription.setText(description);

        editSectionButton = findViewById(R.id.btn_edit);
        deleteSectionButton = findViewById(R.id.btn_delete);
        newThread = findViewById(R.id.fab_new_thread);

        deleteSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleyDeleteSection.volleyDeleteSection(new VolleyStringCallback() {
                    @Override
                    public void onSuccess(String result) {
                        sessionManager.setSection(result);
                    }

                    @Override
                    public void onErrorResponse(VolleyError result) {
                    }
                },SectionActivity.this,section_id);
                finish();
            }
        });

        threadsList = new ArrayList<Threadblog>();

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_threads);

        setSwipeRefreshLayout();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);




        sessionManager = new SessionManager(SectionActivity.this);
        threadsListListView = findViewById(R.id.threads_list);
        threadsAdapter = new ThreadsAdapter(getApplicationContext(), threadsList);

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

        threadsListListView.setAdapter(threadsAdapter);
        threadsListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Threadblog thread = (Threadblog) threadsAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(),ThreadActivity.class);
                intent.putExtra("TITLE",thread.getTitle());
                intent.putExtra("TEXT",thread.getAllText());
                intent.putExtra("ID",thread.getId());
                Bundle bndlanimation = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),R.anim.animation,R.anim.animation2).toBundle();
                startActivity(intent,bndlanimation);
            }
        });


    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,SectionActivity.class);
        return intent;
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
            startActivity(LoginActivity.getIntent(SectionActivity.this));
        } else if (item.getItemId() == R.id.to_all_projects) {
            sessionManager.setActualProject(Constants.ALL_PROJECTS_KEY,Constants.ALL_PROJECTS_NAME);
            startActivity(MainActivity.getIntent(SectionActivity.this));
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
                    VolleyGetThreads.volleyGetThreads(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Threadblog thread;
                            JSONArray jsonThreads;
                            try {
                                JSONObject request = new JSONObject(result);
                                jsonThreads = new JSONArray(request.getString("microblog_threads"));
                                threadsList.clear();

                                for (int i = 0; i < jsonThreads.length(); i++) {
                                    thread = new Threadblog("","","");
                                    Log.i("JSON", jsonThreads.get(i).toString());
                                    JSONObject object = (JSONObject) jsonThreads.get(i);
                                    thread.setTitle(object.get("title").toString());
                                    //TODO jose verificar que funcione substring
                                    thread.setText(object.get("text").toString());
                                    thread.setId(object.get("id").toString());
                                    threadsList.add(thread);

                                }
                                Log.i("REQ","HACIENDO REQ");
                                threadsAdapter = new ThreadsAdapter(getApplicationContext(), threadsList);
                                threadsListListView.setAdapter(threadsAdapter);
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
            sessionManager.clearSection(section_id);
            //super.onBackPressed();
            //overridePendingTransition(R.anim.animation_back1,R.anim.animation_back2);
            finish();
        }
    }

}