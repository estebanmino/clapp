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

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetLessonsBySearch;
import com.construapp.construapp.dbTasks.GetLessonsTask;
import com.construapp.construapp.lessons.LessonActivity;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;

import org.json.JSONArray;
import org.json.JSONObject;

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

        final List<Lesson> lesson_list = new ArrayList<Lesson>();

        VolleyGetLessonsBySearch.volleyGetLessonsBySearch(new VolleyStringCallback(){
            @Override
            public void onSuccess(String result) {

                JSONArray jsonLessons;
                try {
                    jsonLessons = new JSONArray(result);
                    for (int i = 0; i < jsonLessons.length(); i++) {
                        Lesson lesson = new Lesson();
                        Log.i("JSON", jsonLessons.get(i).toString());
                        JSONObject object = (JSONObject) jsonLessons.get(i);
                        lesson.setName(object.get("name").toString());
                        lesson.setSummary(object.get("summary").toString());
                        lesson.setId(object.get("id").toString());
                        lesson.setMotivation(object.get("motivation").toString());
                        lesson.setLearning(object.get("learning").toString());
                        lesson.setValidation(object.get("validation").toString());
                        JSONObject objectUser = (JSONObject) object.get("user");
                        lesson.setAuthor_id(objectUser.get("id").toString());
                        lesson.setAuthor_admin(objectUser.get("admin").toString());
                        lesson.setAuthor_email(objectUser.get("email").toString());
                        lesson.setAuthor_first_name(objectUser.get("first_name").toString());
                        lesson.setAuthor_last_name(objectUser.get("last_name").toString());
                        lesson.setAuthor_position(objectUser.get("position").toString());
                        lesson.setProject_id(object.get("project_id").toString());
                        lesson.setCompany_id(object.get("company_id").toString());
                        lesson.setTrigger_id((int) object.get("trigger_id"));
                        lesson.setReject_comment(object.get("reject_comment").toString());
                        lesson_list.add(lesson);


                    }
                    mSearchAdapter = new SearchAdapter(getApplicationContext(), lesson_list);
                    listView.setAdapter(mSearchAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Lesson lesson = (Lesson) mSearchAdapter.getItem(position);
                            startActivity(LessonActivity.getIntent(getApplicationContext(), lesson.getName(),
                                    lesson.getSummary(), lesson.getId()));
                        }
                    });
                } catch (Exception e) {
                }
            }

                @Override
                public void onErrorResponse(VolleyError result) {

                }
            },getApplicationContext(),query);
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