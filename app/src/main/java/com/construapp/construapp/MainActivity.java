package com.construapp.construapp;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.microblog.MicroblogFragment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;

import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    //CONSTANTS
    private General constants;
    private ViewPager mViewPager;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private LRUCache lruCache;
    private int userPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);

        constants = new General();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        lruCache = LRUCache.getInstance();
        constants.setUserPermission(MainActivity.this);
        userPermission = Integer.parseInt(sharedpreferences.getString(Constants.SP_USER_PERMISSION,""));
        int fabPermission = constants.xmlPermissionTagToInt(fab.getTag().toString());

        //Able FloatingActionButton or hide it according to the user permissions
        if (userPermission >= fabPermission){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(LessonFormActivity.getIntent(MainActivity.this));
                }
            });
        }
        else {
            fab.setVisibility(View.GONE);
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout)
        {
            SharedPreferences mySPrefs = getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(this,"Se ha  cerrado su sesión",Toast.LENGTH_LONG).show();
            startActivity(LoginActivity.getIntent(MainActivity.this));
        }

        return super.onOptionsItemSelected(item);
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
                        return new MicroblogFragment();
                    case 3:
                        return new ValidateFragment();
                }
            }
            else {
                switch (position) {
                    case 0:
                        return new LessonsFragment();
                    case 1:
                        return new MyLessonsFragment();
                    case 2:
                        return new MicroblogFragment();
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (userPermission >= 3){
                return 4;
            }
            else {
                return 3;
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
                        return "Blog";
                    case 3:
                        return "Validar lecciones";
                }
            }
            else {
                switch (position) {
                    case 0:
                        return "Lecciones";
                    case 1:
                        return "Mis lecciones";
                    case 2:
                        return "Blog";
                }
            }
            return null;
        }
    }

    public void onBackPressed()
    {
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,MainActivity.class);
        return intent;
    }
}
