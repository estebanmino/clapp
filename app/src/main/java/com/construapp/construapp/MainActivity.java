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

import com.construapp.construapp.microblog.MicroblogFragment;

import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SharedPreferences sharedpreferences = getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        String token = sharedpreferences.getString("token", "");
        String user_id = sharedpreferences.getString("user_id", "");
        String company_id = sharedpreferences.getString("company_id", "");
        //Toast.makeText(this,"El token es:"+token,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"El User id es:"+user_id,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"El company id es:"+company_id,Toast.LENGTH_SHORT).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(LessonFormActivity.getIntent(MainActivity.this));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout)
        {
            SharedPreferences mySPrefs =getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.remove("token");
            editor.apply();


            boolean token_exists = mySPrefs.contains("token");
            if(!token_exists)
            {
                Toast.makeText(this,"Se ha  cerrado su sesión",Toast.LENGTH_LONG).show();

            }
            else
            {
                //Toast.makeText(this,"El token NO SE BORRO",Toast.LENGTH_LONG).show();

            }

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
            switch (position) {
                case 0:
                    return new LessonsFragment();
                case 1:
                    return new MyLessonsFragment();
                case 2:
                    return new MicroblogFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Lecciones";
                case 1:
                    return "Mis lecciones";
                case 2:
                    return "Blog";
            }
            return null;
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context,MainActivity.class);
        return intent;
    }
}
