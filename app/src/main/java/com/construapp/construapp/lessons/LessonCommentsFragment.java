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
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.GetCommentsTask;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.main.PostsAdapter;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Post;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonCommentsFragment extends Fragment {

    public LessonCommentsFragment() {
    }

    String lessonComments;
    List<Comment> arrayComments;

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
        final ListView listLessonComments = view.findViewById(R.id.list_lesson_comments);
        final TextView textComments = view.findViewById(R.id.text_title);
        Button btnComment = view.findViewById(R.id.btn_comment);
        final EditText editComment = view.findViewById(R.id.edit_comment);
        try {
            arrayComments = new GetCommentsTask(getActivity(), lessonId).execute().get();
        } catch (Exception e) {
            arrayComments = null;
        }

        final CommentsAdapter commentsAdapter = new CommentsAdapter(getActivity(), arrayComments);
        listLessonComments.setAdapter(commentsAdapter);
        if (arrayComments.size() == 0) {
            textComments.setText("No hay comentarios para esta lección");
        }
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editComment.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Debe escribir un comentario", Toast.LENGTH_LONG).show();
                } else if (!Connectivity.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), "Debe estar conectado a internet para comentar", Toast.LENGTH_LONG).show();
                } else {
                    VolleyPostLessonComment.volleyPostLessonComment(new VolleyStringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            JsonParser parser = new JsonParser();
                            JsonObject json = parser.parse(result).getAsJsonObject();
                            Comment comment = new Comment();
                            comment.setId(json.get("id").toString());
                            comment.setText(json.get("text").toString());
                            JsonObject jsonObject1 = (JsonObject) json.get("user").getAsJsonObject();
                            comment.setFirst_name(jsonObject1.get("first_name").toString());
                            comment.setLast_name(jsonObject1.get("last_name").toString());
                            comment.setAuthorId(jsonObject1.get("id").toString());
                            comment.setPosition(jsonObject1.get("position").toString());
                            comment.setLessonId(lessonId);
                            try {
                                new InsertCommentTask(comment, getActivity()).execute().get();
                                arrayComments.add(comment);
                                commentsAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                            }
                            editComment.setText("");
                            textComments.setText("Comentarios");
                        }

                        @Override
                        public void onErrorResponse(VolleyError result) {

                        }
                    }, getActivity(), lessonId, editComment.getText().toString());
                }
            }
        });


    }}
