package com.construapp.construapp.lessons;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyPostLessonComment;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.PostsAdapter;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonCommentsFragment extends Fragment {

    public LessonCommentsFragment() {
    }

    String lessonComments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lessonComments = getArguments().getString(Constants.B_LESSON_COMMENTS);

        return inflater.inflate(R.layout.fragment_lesson_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String lessonId = getArguments().getString(Constants.B_LESSON_ID);
        ListView listLessonComments = view.findViewById(R.id.list_lesson_comments);
        TextView textComments = view.findViewById(R.id.text_title);
        Button btnComment = view.findViewById(R.id.btn_comment);
        final EditText editComment = view.findViewById(R.id.edit_comment);

        final ArrayList<Post> arrayComments = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray json = parser.parse(lessonComments).getAsJsonArray();
        Log.i("LESSONCOMMENTSORIGINAL", lessonComments);
        for (int i = 0; i < json.size(); i++) {
            JsonElement jsonObject = json.get(i);
            Post post = new Post();
            post.setText(jsonObject.getAsJsonObject().get("text").toString());
            JsonObject jsonObject1 = (JsonObject) jsonObject.getAsJsonObject().get("user").getAsJsonObject();
            post.setFirst_name(jsonObject1.get("first_name").toString());
            post.setLast_name(jsonObject1.get("last_name").toString());
            post.setPosition(jsonObject1.get("position").toString());
            arrayComments.add(post);
        }

        final PostsAdapter postsAdapter = new PostsAdapter(getActivity(), arrayComments);
        listLessonComments.setAdapter(postsAdapter);
        Log.i("COMMENTSIZE",Integer.toString(arrayComments.size()));
        if (arrayComments.size() == 0) {
            textComments.setText("No hay comentarios para esta lección");
        }
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editComment.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),"Debe escribir un comentario",Toast.LENGTH_LONG).show();
                } else {
                    VolleyPostLessonComment.volleyPostLessonComment(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            JsonParser parser = new JsonParser();
                            JsonObject json = parser.parse(result).getAsJsonObject();
                            Post newPost = new Post();
                            newPost.setId(json.get("id").toString());
                            newPost.setText(json.get("text").toString());
                            JsonObject jsonObject1 = (JsonObject) json.get("user").getAsJsonObject();
                            newPost.setFirst_name(jsonObject1.get("first_name").toString());
                            newPost.setLast_name(jsonObject1.get("last_name").toString());
                            newPost.setAuthorId(jsonObject1.get("id").toString());
                            newPost.setPosition(jsonObject1.get("position").toString());
                            arrayComments.add(newPost);
                            postsAdapter.notifyDataSetChanged();
                            try {
                                Lesson lesson = new GetLessonTask(getActivity(), lessonId).execute().get();
                                lesson.setComments(arrayComments.toString());
                                Log.i("LESSONCOMMENTSARRAY",arrayComments.toString());
                            } catch (Exception e) {}

                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {

                        }
                    }, getActivity(), lessonId, editComment.getText().toString());
                }
            }
        });

    }

}
