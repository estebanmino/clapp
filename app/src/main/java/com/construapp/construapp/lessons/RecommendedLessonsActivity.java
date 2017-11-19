package com.construapp.construapp.lessons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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
import com.construapp.construapp.api.VolleyGetFavouriteLessons;
import com.construapp.construapp.api.VolleyGetRecommendedLessons;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.LessonsAdapter;
import com.construapp.construapp.main.MainActivity;
import com.construapp.construapp.microblog.MicroblogActivity;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecommendedLessonsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private SessionManager sessionManager;
    private ListView listRecommendedLessons;

    TextView mUserName;

    private String user_id;
    private String project_id;

    private List<Lesson> lessonList;

    private LessonsAdapter lessonsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_lessons);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.TITLE_RECOMMENDED_LESSONS);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(RecommendedLessonsActivity.this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //back toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_ham));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        listRecommendedLessons = findViewById(R.id.list_recomended_lessons);
        lessonList = new ArrayList<>();
        lessonsAdapter = new LessonsAdapter(RecommendedLessonsActivity.this, lessonList);

        listRecommendedLessons.setAdapter(lessonsAdapter);
        listRecommendedLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Lesson lesson = (Lesson) lessonsAdapter.getItem(position);
                startActivity(LessonActivity.getIntent(RecommendedLessonsActivity.this, lesson.getName(),
                        lesson.getSummary(),lesson.getId()));
            }
        });


        swipeRefreshLayout = findViewById(R.id.swipe_refresh_my_lessons);
        setSwipeRefreshLayout();
        refreshData();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,RecommendedLessonsActivity.class);
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
            startActivity(LoginActivity.getIntent(RecommendedLessonsActivity.this));
        } else if (item.getItemId() == R.id.to_all_projects) {
            sessionManager.setActualProject(Constants.ALL_PROJECTS_KEY,Constants.ALL_PROJECTS_NAME);
            startActivity(MainActivity.getIntent(RecommendedLessonsActivity.this));
        } else  if (item.getItemId() == R.id.to_blog) {
            startActivity(MicroblogActivity.getIntent(RecommendedLessonsActivity.this));
        } else  if (item.getItemId() == R.id.to_favourites) {
            startActivity(FavouriteLessonsActivity.getIntent(RecommendedLessonsActivity.this));
        } else  if (item.getItemId() == R.id.to_recommended) {
            startActivity(RecommendedLessonsActivity.getIntent(RecommendedLessonsActivity.this));
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

    public void refreshData(){
        boolean is_connected = Connectivity.isConnected(RecommendedLessonsActivity.this);
        user_id = sessionManager.getUserId();
        project_id = sessionManager.getActualProjectId();
        lessonList.clear();
        if(is_connected) {
            VolleyGetRecommendedLessons.volleyGetRecommendedLessons(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    JSONArray jsonLessons;
                    try {
                        jsonLessons = new JSONArray(result);
                        for (int i = 0; i < jsonLessons.length(); i++) {
                            Lesson lesson = new Lesson();
                            JSONObject object = (JSONObject) jsonLessons.get(i);
                            Log.i("oo",object.toString());
                            lesson.setName(object.get("name").toString());
                            lesson.setSummary(object.get("summary").toString());
                            lesson.setId(object.get("id").toString());
                            lesson.setMotivation(object.get("motivation").toString());
                            lesson.setLearning(object.get("learning").toString());
                            lesson.setValidation(object.get("validation").toString());
                            lesson.setAuthor_id(object.get("user_id").toString());
                            lesson.setProject_id(object.get("project_id").toString());
                            lesson.setCompany_id(object.get("company_id").toString());
                            lesson.setReject_comment(object.get("reject_comment").toString());
                            if (lesson.getValidation() == Constants.R_VALIDATED) {
                                lessonList.add(lesson);
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onErrorResponse(VolleyError result) {
                    try {
                        lessonList = new GetLessonsTask(RecommendedLessonsActivity.this,
                                project_id, user_id, Constants.R_VALIDATED).execute().get();
                        lessonsAdapter = new LessonsAdapter(RecommendedLessonsActivity.this, lessonList);
                        listRecommendedLessons.setAdapter(lessonsAdapter);
                    }catch (Exception e) {}
                }
            }, RecommendedLessonsActivity.this);
        } else {
            try {
                lessonList = new GetLessonsTask(RecommendedLessonsActivity.this, project_id,
                        user_id,Constants.R_VALIDATED).execute().get();
                lessonsAdapter = new LessonsAdapter(RecommendedLessonsActivity.this, lessonList);
                listRecommendedLessons.setAdapter(lessonsAdapter);
            } catch (Exception e) {}

        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (navigationView.isShown())
        {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            finish();

        }


    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

}
