package com.construapp.construapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.construapp.construapp.api.VolleyGetLessons;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.microblog.MicroblogFragment;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Project;
import com.construapp.construapp.sidebar.SidebarAdapter;
import com.construapp.construapp.dbTasks.DeleteLessonTable;
import com.construapp.construapp.dbTasks.InsertLessonTask;
import com.construapp.construapp.threading.GetLessons;
import com.construapp.construapp.validations.ValidateFragment;

import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ListView;
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
    private General constants;
    private ViewPager mViewPager;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private LRUCache lruCache;
    private int userPermission;
    private String[] mProjectTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private NavigationView navigationView;
    private SidebarAdapter sidebarAdapter;
    private SharedPreferences sharedpreferences;

    JSONArray jsonArray;
    Map<String, String> projects;

    TextView mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);

        try {
            jsonArray = new JSONArray(sharedpreferences.getString(Constants.SP_PROJECTS, ""));
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



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        constants = new General();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        lruCache = LRUCache.getInstance();
        constants.setUserPermission(MainActivity.this);
        userPermission = Integer.parseInt(sharedpreferences.getString(Constants.SP_USER_PERMISSION,""));
        int fabPermission = constants.xmlPermissionTagToInt(fab.getTag().toString());

        //Able FloatingActionButton or hide it according to the user permissions
        if (userPermission >= fabPermission && !sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT,"").equals("null")){
            getSupportActionBar().setTitle("Proyecto " + sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT_NAME,""));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(LessonFormActivity.getIntent(MainActivity.this));
                }
            });
        }
        else {
            getSupportActionBar().setTitle(Constants.ALL_PROJECTS_NAME);
            fab.setVisibility(View.GONE);
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (userPermission == 1) {
            tabLayout.setVisibility(View.GONE);
        }
        tabLayout.setupWithViewPager(mViewPager);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_username);
        mUserName.setText(sharedpreferences.getString(Constants.SP_ACTUAL_PROJECT_NAME, ""));

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
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            SharedPreferences mySPrefs = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.clear();
            editor.apply();
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
            String map = item.getTitle().toString();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            final SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SP_ACTUAL_PROJECT,Constants.ALL_PROJECTS_KEY);
            editor.putString(Constants.SP_ACTUAL_PROJECT_NAME,Constants.ALL_PROJECTS_NAME);
            editor.apply();
        } else  if (item.getItemId() == R.id.to_blog) {
            //
        }
        else {
            String map = item.getTitle().toString();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            final SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.SP_ACTUAL_PROJECT, Integer.toString(item.getItemId()));
            editor.putString(Constants.SP_ACTUAL_PROJECT_NAME, map);
            editor.apply();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            if (userPermission >= 3){
                switch (position) {
                    case 0:
                        return new LessonsFragment();
                    case 1:
                        return new MyLessonsFragment();
                    case 2:
                        return new ValidateFragment();
                }
            }
            else if (userPermission == 1) {
                return new LessonsFragment();
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
                return 1;
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
                switch (position) {
                    case 0:
                        return null;
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
        Intent intent = new Intent(context,MainActivity.class);
        return intent;
    }
}
