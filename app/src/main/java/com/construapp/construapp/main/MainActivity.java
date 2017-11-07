package com.construapp.construapp.main;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetPendingValidations;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.lessons.FavouriteLessonsActivity;
import com.construapp.construapp.lessons.LessonFormActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.SessionManager;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.threading.GetLessons;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    //CONSTANTS
    private General general;
    private ViewPager mViewPager;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private LRUCache lruCache;
    private int userPermission;
    private String[] mProjectTitles;

    private NavigationView navigationView;
    private SessionManager sessionManager;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;
    String pendingValidations;


    private static String PENDING_VALIDATIONS = "lesson_validation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(MainActivity.this);

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

        try {
            new GetLessons(MainActivity.this).execute().get();
        } catch (Exception e) {}

        general = new General();
        pendingValidations = sessionManager.getHasPendingValidations();

        FloatingActionButton fab = findViewById(R.id.fab);
        lruCache = LRUCache.getInstance();
        general.setUserPermission(MainActivity.this);
        userPermission = Integer.parseInt(sessionManager.getActualUserPermission());
        int fabPermission = general.xmlPermissionTagToInt(fab.getTag().toString());

        //Able FloatingActionButton or hide it according to the user permissions
        if (userPermission >= fabPermission && !sessionManager.getActualProjectId().equals("null")){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(LessonFormActivity.getIntent(MainActivity.this));
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
        if (sessionManager.getActualProjectId().equals("null")){
            getSupportActionBar().setTitle(Constants.ALL_PROJECTS_NAME);
        } else {
            getSupportActionBar().setTitle("Proyecto " + sessionManager.getActualProjectName());
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.notifyDataSetChanged();

        TabLayout tabLayout = findViewById(R.id.tabs);
        if (userPermission == 1 && !sessionManager.getHasPendingValidations().equals("true")) {
            tabLayout.setVisibility(View.GONE);
        }
        tabLayout.setupWithViewPager(mViewPager);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.main_username);
        mUserName.setText(sessionManager.getActualProjectName());

        Menu menu = navigationView.getMenu();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject project = (JSONObject) jsonArray.get(i);
                menu.add(0, i+1, Menu.NONE, project.getString("name"));
            }
        } catch (Exception e) {}

        //back toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu_ham));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {

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
            startActivity(LoginActivity.getIntent(MainActivity.this));
        } else if (item.getItemId() == R.id.to_all_projects) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            sessionManager.setActualProject(Constants.ALL_PROJECTS_KEY,Constants.ALL_PROJECTS_NAME);
        } else  if (item.getItemId() == R.id.to_blog) {
            startActivity(MicroblogActivity.getIntent(MainActivity.this));

        } else  if (item.getItemId() == R.id.to_favourites) {
            startActivity(FavouriteLessonsActivity.getIntent(MainActivity.this));
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("PENDINGVALIDATION",pendingValidations.toString());

            if (userPermission >= 3){
                switch (position) {
                    case 0:
                        return new LessonsFragment();
                    case 1:
                        return new MyLessonsFragment();
                    case 2:
                        return new LessonValidateFragment();
                }
            }
            else if (userPermission == 1) {

                if (!pendingValidations.equals("true")) {return new LessonsFragment();}
                else {
                    switch(position) {
                    case 0:
                        return new LessonsFragment();
                    case 1:
                        return new LessonValidateFragment();}
                }
            }
            else {
                switch (position) {
                    case 0:
                        return new LessonsFragment();
                    case 1:
                        return new MyLessonsFragment();
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (userPermission >= 3){
                return 3;
            }
            else if (userPermission == 1) {
                if (!pendingValidations.equals("true")) {return 1;}
                else {return 2; }
            }
            else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (userPermission >= 3){
                switch (position) {
                    case 0:
                        return "Lecciones";
                    case 1:
                        return "Mis lecciones";
                    case 2:
                        return "Validar lecciones";
                }
            }
            else if (userPermission == 1) {
                if (!pendingValidations.equals("true")) {
                    switch (position) {
                        case 0:
                            return null;
                    }
                } else {
                    switch (position) {
                        case 0:
                            return "Lecciones";
                        case 1:
                            return "Validar lecciones";
                    }
                }
            }
            else {
                switch (position) {
                    case 0:
                        return "Lecciones";
                    case 1:
                        return "Mis lecciones";
                }
            }
            return null;
        }
    }

    public static Intent getIntent(Context context) {
        final SessionManager sessionManager = new SessionManager(context);
        if (Connectivity.isConnected(context)) {
            VolleyGetPendingValidations.volleyGetPendingValidations(new VolleyStringCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        Log.i("GETVALIDATIONSPRENDING",result);
                        if (new JSONArray(result).length() > 0) {
                            sessionManager.setHasPendingValidations("true");
                            sessionManager.setPendingValidations(result);
                        } else {
                            sessionManager.setHasPendingValidations("false");
                        }
                    } catch (Exception e) {
                        sessionManager.setHasPendingValidations("false");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError result) {
                    sessionManager.setHasPendingValidations("false");
                }
            }, context, sessionManager.getUserId());
        } else {
            sessionManager.setHasPendingValidations("false");
        }
        Intent intent = new Intent(context,MainActivity.class);
        return intent;
    }
}
