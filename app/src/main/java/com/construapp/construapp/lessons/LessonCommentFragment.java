package com.construapp.construapp.lessons;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Constants;

import java.util.ArrayList;


public class LessonCommentFragment extends Fragment {

    private String lessonComment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lessonComment = getArguments().getString(Constants.B_LESSON_COMMENT);
        return inflater.inflate(R.layout.fragment_lesson_comment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textLessonComment = view.findViewById(R.id.text_lesson_comment);
        textLessonComment.setText(lessonComment);
    }
}
