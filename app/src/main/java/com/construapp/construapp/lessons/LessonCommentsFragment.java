package com.construapp.construapp.lessons;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyDeleteLessonComment;
import com.construapp.construapp.api.VolleyGetLesson;
import com.construapp.construapp.api.VolleyGetThread;
import com.construapp.construapp.api.VolleyPostLessonComment;
import com.construapp.construapp.api.VolleyPostPosts;
import com.construapp.construapp.api.VolleyPutLessonComment;
import com.construapp.construapp.api.VolleyPutPost;
import com.construapp.construapp.db.Connectivity;
import com.construapp.construapp.dbTasks.DeleteLessonCommentTask;
import com.construapp.construapp.dbTasks.GetCommentsTask;
import com.construapp.construapp.dbTasks.GetLessonTask;
import com.construapp.construapp.dbTasks.InsertCommentTask;
import com.construapp.construapp.listeners.VolleyJSONCallback;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.microblog.PostsAdapter;
import com.construapp.construapp.microblog.ThreadActivity;
import com.construapp.construapp.models.Comment;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.Post;
import com.construapp.construapp.models.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
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
    LinearLayout mContainerView;

    private AppCompatButton createPost;
    private EditText newComment;

    SessionManager sessionManager;

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
        mContainerView = view.findViewById(R.id.linear_layout_posts);
        sessionManager = new SessionManager(getActivity());
        initComment(lessonId);


    }

    private void initComment(final String lesson_id){
        try {
            List<Comment> arrayComments = new GetCommentsTask(getActivity(),lesson_id).execute().get();

            mContainerView.removeAllViews();

            for (final Comment comment: arrayComments) {
                //post = new Post();
                LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myView = inflater.inflate(R.layout.thread_comments_list_item, null);

                final Button editPostButton = myView.findViewById(R.id.btn_edit);
                final Button deletePostButton = (Button) myView.findViewById(R.id.btn_delete);
                final Button updatePostButton = myView.findViewById(R.id.btn_update);

                final TextView textPost = myView.findViewById(R.id.text_post);
                final TextView textPostTimestamp = myView.findViewById(R.id.text_post_timestamp);
                final TextView textPostAuthorFullName = myView.findViewById(R.id.text_post_author_fullname);
                final TextView textPostAuthorPosition = myView.findViewById(R.id.text_post_author_position);

                final EditText editPostText = myView.findViewById(R.id.edit_post_text);

                String authorFullName = comment.getFirst_name()+" "+comment.getLast_name();
                textPost.setText(comment.getText());
                textPostTimestamp.setText("");
                textPostAuthorFullName.setText(authorFullName);
                textPostAuthorPosition.setText(comment.getPosition());


                if (sessionManager.getUserId().equals(comment.getAuthorId()) ||
                        sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)){
                    editPostButton.setVisibility(myView.VISIBLE);
                    deletePostButton.setVisibility(myView.VISIBLE);
                }
                else {
                    editPostButton.setVisibility(myView.GONE);
                    deletePostButton.setVisibility(myView.GONE);
                }

                mContainerView.addView(myView);

                deletePostButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        VolleyDeleteLessonComment.volleyDeleteLessonComment(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {
                                //onRestart();
                                try {
                                    new DeleteLessonCommentTask(comment, getActivity()).execute().get();
                                    mContainerView.removeAllViews();
                                    initComment(lesson_id);
                                } catch (Exception e) {}
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {

                            }
                        },getActivity(),lesson_id,String.valueOf(comment.getId()));
                    }

                });

                editPostButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        editPostButton.setVisibility(View.GONE);
                        deletePostButton.setVisibility(View.GONE);
                        textPost.setVisibility(View.GONE);
                        updatePostButton.setVisibility(View.VISIBLE);
                        editPostText.setVisibility(View.VISIBLE);
                        editPostText.setText(textPost.getText().toString());
                        editPostText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus){
                                    editPostText.setSelection(editPostText.getText().length());
                                }
                            }
                        });
                        editPostText.requestFocus();
                    }

                });
                updatePostButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        VolleyPutLessonComment.volleyPutLessonComment(new VolleyJSONCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                editPostButton.setVisibility(View.VISIBLE);
                                deletePostButton.setVisibility(View.VISIBLE);
                                textPost.setVisibility(View.VISIBLE);
                                updatePostButton.setVisibility(View.GONE);
                                editPostText.setVisibility(View.GONE);
                                comment.setText(editPostText.getText().toString());
                                try {
                                    new InsertCommentTask(comment, getActivity()).execute();
                                    mContainerView.removeAllViews();
                                    initComment(lesson_id);
                                } catch (Exception e) {}

                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                                Toast.makeText(getActivity(),"No se pudo editar el comentario.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        },getActivity(),lesson_id,String.valueOf(comment.getId()),editPostText.getText().toString());

                        editPostButton.setVisibility(View.GONE);
                        deletePostButton.setVisibility(View.GONE);
                        textPost.setVisibility(View.GONE);
                        updatePostButton.setVisibility(View.VISIBLE);
                        editPostText.setVisibility(View.VISIBLE);
                    }
                });
            }

            LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.thread_new_comment, null);
            mContainerView.addView(myView);

            createPost = (AppCompatButton) myView.findViewById(R.id.button_newpost);
            newComment = (EditText) myView.findViewById(R.id.edittext_new_comment);

            createPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Connectivity.isConnected(getActivity())) {
                        Log.i("TEXT", newComment.getText().toString());
                        VolleyPostLessonComment.volleyPostLessonComment(new VolleyStringCallback() {
                            @Override
                            public void onSuccess(String result) {
                                JsonParser parser = new JsonParser();
                                JsonObject json = parser.parse(result).getAsJsonObject();
                                Comment comment = new Comment();
                                comment.setId(Integer.parseInt(json.get("id").getAsString()));
                                comment.setText(json.get("text").getAsString());
                                JsonObject jsonObject1 = (JsonObject) json.get("user").getAsJsonObject();
                                comment.setFirst_name(jsonObject1.get("first_name").getAsString());
                                comment.setLast_name(jsonObject1.get("last_name").getAsString());
                                comment.setAuthorId(jsonObject1.get("id").getAsString());
                                comment.setPosition(jsonObject1.get("position").getAsString());
                                comment.setLessonId(lesson_id);
                                try {
                                    new InsertCommentTask(comment, getActivity()).execute().get();
                                    mContainerView.removeAllViews();
                                    initComment(lesson_id);
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError result) {
                                Toast.makeText(getActivity(), "No se pudo publicar el comentario."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }, getActivity(), lesson_id, newComment.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "No tienes conexion a internet."
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            );
        } catch (Exception e) {
        }

        //swipeRefreshLayout.setRefreshing(false);
    }


    private void showComments(final String lesson_id){
        boolean is_connected = Connectivity.isConnected(getActivity());
        if(is_connected) {
            VolleyGetLesson.volleyGetLesson(new VolleyJSONCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Post post;
                    JSONArray jsonPosts;
                    try {
                        List<Comment> arrayComments = new GetCommentsTask(getActivity(),lesson_id).execute().get();

                        mContainerView.removeAllViews();

                        for (final Comment comment: arrayComments) {
                            //post = new Post();
                            LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View myView = inflater.inflate(R.layout.thread_comments_list_item, null);

                            final Button editPostButton = myView.findViewById(R.id.btn_edit);
                            final Button deletePostButton = (Button) myView.findViewById(R.id.btn_delete);
                            final Button updatePostButton = myView.findViewById(R.id.btn_update);

                            final TextView textPost = myView.findViewById(R.id.text_post);
                            final TextView textPostTimestamp = myView.findViewById(R.id.text_post_timestamp);
                            final TextView textPostAuthorFullName = myView.findViewById(R.id.text_post_author_fullname);
                            final TextView textPostAuthorPosition = myView.findViewById(R.id.text_post_author_position);

                            final EditText editPostText = myView.findViewById(R.id.edit_post_text);

                            String authorFullName = comment.getFirst_name()+" "+comment.getLast_name();
                            textPost.setText(comment.getText());
                            textPostTimestamp.setText("");
                            textPostAuthorFullName.setText(authorFullName);
                            textPostAuthorPosition.setText(comment.getPosition());

                            /*
                            if (sessionManager.getUserId().equals(postUserId) ||
                                    sessionManager.getUserAdmin().equals(Constants.S_ADMIN_ADMIN)){
                                editPostButton.setVisibility(myView.VISIBLE);
                                deletePostButton.setVisibility(myView.VISIBLE);
                            }
                            else {
                                editPostButton.setVisibility(myView.GONE);
                                deletePostButton.setVisibility(myView.GONE);
                            }*/

                            mContainerView.addView(myView);

                            /*deletePostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    AlertDialog diaBox = AskOptionPost(post_id);
                                    diaBox.show();
                                }

                            });*/

                            editPostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {

                                    editPostButton.setVisibility(View.GONE);
                                    deletePostButton.setVisibility(View.GONE);
                                    textPost.setVisibility(View.GONE);
                                    updatePostButton.setVisibility(View.VISIBLE);
                                    editPostText.setVisibility(View.VISIBLE);
                                    editPostText.setText(textPost.getText().toString());
                                    editPostText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            if(hasFocus){
                                                editPostText.setSelection(editPostText.getText().length());
                                            }
                                        }
                                    });
                                    editPostText.requestFocus();
                                }

                            });
                            updatePostButton.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {

                                    VolleyPutLessonComment.volleyPutLessonComment(new VolleyJSONCallback() {
                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            editPostButton.setVisibility(View.VISIBLE);
                                            deletePostButton.setVisibility(View.VISIBLE);
                                            textPost.setVisibility(View.VISIBLE);
                                            updatePostButton.setVisibility(View.GONE);
                                            editPostText.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onErrorResponse(VolleyError result) {
                                            Toast.makeText(getActivity(),"No se pudo editar el comentario.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    },getActivity(),lesson_id,String.valueOf(comment.getId()),editPostText.getText().toString());

                                    editPostButton.setVisibility(View.GONE);
                                    deletePostButton.setVisibility(View.GONE);
                                    textPost.setVisibility(View.GONE);
                                    updatePostButton.setVisibility(View.VISIBLE);
                                    editPostText.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        LayoutInflater inflater =(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View myView = inflater.inflate(R.layout.thread_new_comment, null);
                        mContainerView.addView(myView);

                        createPost = (AppCompatButton) myView.findViewById(R.id.button_newpost);
                        newComment = (EditText) myView.findViewById(R.id.edittext_new_comment);

                        createPost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Log.i("TEXT",newComment.getText().toString());
                                VolleyPostLessonComment.volleyPostLessonComment(new VolleyStringCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        //onRestart();
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError result) {
                                        Toast.makeText(getActivity(),"No se pudo publicar el comentario."
                                                ,Toast.LENGTH_SHORT).show();
                                    }
                                },getActivity(),lesson_id,newComment.getText().toString());
                            }
                        });
                    } catch (Exception e) {
                    }
                }
                @Override
                public void onErrorResponse(VolleyError result) {

                }
            }, getActivity(), lesson_id);
        } else {

        }
        //swipeRefreshLayout.setRefreshing(false);
    }


}
