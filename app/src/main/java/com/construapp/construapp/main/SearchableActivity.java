package com.construapp.construapp.main;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;

import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends Activity {
    private SearchAdapter mSearchAdapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.search_list);
        handleIntent(getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query){
        List<Lesson> lesson_list = new ArrayList<Lesson>();
        Lesson l1 = new Lesson();
        l1.setName("Jose");
        l1.setSummary("nada");
        Lesson l2 = new Lesson();
        l2.setName("Jose");
        l2.setSummary("nada");
        lesson_list.add(l1);
        lesson_list.add(l2);
        mSearchAdapter = new SearchAdapter(getApplicationContext(), lesson_list);
        listView.setAdapter(mSearchAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        SearchView searchView =
                (SearchView) menuItem.getActionView();
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("SEARCHACTIVITY","ENTRE A onoptionsitem");
        switch (item.getItemId()) {
            case R.id.menuSearch:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}