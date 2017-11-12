package com.construapp.construapp.lessons;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.services.s3.internal.RestUtils;
import com.android.volley.VolleyError;
import com.construapp.construapp.R;
import com.construapp.construapp.api.VolleyGetLessonComments;
import com.construapp.construapp.listeners.VolleyStringCallback;
import com.construapp.construapp.models.Constants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LessonCommentsFragment extends Fragment {


    public LessonCommentsFragment() {
        // Required empty public constructor
    }

    String lessonId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lessonId = getArguments().getString(Constants.B_LESSON_ID);

        return inflater.inflate(R.layout.fragment_lesson_comments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listLessonComments = view.findViewById(R.id.list_lesson_comments);
        TextView textComments = view.findViewById(R.id.text_title);

        VolleyGetLessonComments.volleyGetLessonComments(new VolleyStringCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("RESULT", result);
            }

            @Override
            public void onErrorResponse(VolleyError result) {

            }
        },getActivity(),lessonId);


    }

}
