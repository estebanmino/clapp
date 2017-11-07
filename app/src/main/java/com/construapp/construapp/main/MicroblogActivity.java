package com.construapp.construapp.main;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
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
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.api.VolleyGetSections;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.lessons.LessonViewFragment;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.microblog.MicroblogFragment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Section;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MicroblogActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ListView sectionsListListView;
    private ArrayList<Section> sectionsList;
    private SectionsAdapter sectionsAdapter;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private String[] mProjectTitles;
    private ViewPager sectionsView;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ConstraintLayout constraintLayoutFvourites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microblog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Microblog");
        setSupportActionBar(toolbar);



        //TODO jose fix obtener y hacer get
        sectionsList = new ArrayList<Section>();
        sectionsList.add(new Section("1","General","Cosas generales"));
        sectionsList.add(new Section("2","Otros","Cosas varias"));

        sessionManager = new SessionManager(MicroblogActivity.this);
        sectionsListListView = findViewById(R.id.sections_list);
        sectionsAdapter = new SectionsAdapter(getApplicationContext(), sectionsList);

        try {
            projects.put(Constants.ALL_PROJECTS_NAME,Constants.ALL_PROJECTS_KEY);
        } catch (Exception e) {}


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

        sectionsListListView.setAdapter(sectionsAdapter);
        sectionsListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Section section = (Section) sectionsAdapter.getItem(position);

                Intent intent = new Intent(getBaseContext(),SectionActivity.class);
                intent.putExtra("NAME",section.getName());
                intent.putExtra("DESCRIPTION",section.getDescription());
                intent.putExtra("ID",section.getId());
                Bundle bndlanimation = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),R.anim.animation,R.anim.animation2).toBundle();
                startActivity(intent,bndlanimation);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_sections);
        setSwipeRefreshLayout();


    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean is_connected = Connectivity.isConnected(getApplicationContext());
                if(is_connected) {
                    VolleyGetSections.volleyGetSections(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Section section = new Section("","","");
                            JSONArray jsonSections;
                            try {
                                jsonSections = new JSONArray(result);
                                for (int i = 0; i < jsonSections.length(); i++) {
                                    Log.i("JSON", jsonSections.get(i).toString());
                                    JSONObject object = (JSONObject) jsonSections.get(i);
                                    section.setName(object.get("name").toString());
                                    section.setId(object.get("id").toString());
                                    sectionsList.add(section);

                                }

                                sectionsAdapter = new SectionsAdapter(getApplicationContext(), sectionsList);
                                sectionsListListView.setAdapter(sectionsAdapter);
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

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,MicroblogActivity.class);
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
            startActivity(LoginActivity.getIntent(MicroblogActivity.this));
        } else if (item.getItemId() == R.id.to_all_projects) {
            sessionManager.setActualProject(Constants.ALL_PROJECTS_KEY,Constants.ALL_PROJECTS_NAME);
            startActivity(MainActivity.getIntent(MicroblogActivity.this));
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

}