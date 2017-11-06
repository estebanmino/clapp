package com.construapp.construapp.microblog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.construapp.construapp.R;
import com.construapp.construapp.main.LessonsAdapter;
import com.construapp.construapp.models.Constants;

import java.util.ArrayList;


public class MicroblogFragment extends Fragment {


    ArrayList<String> sectionsArray;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sectionsArray = getArguments().getStringArrayList(Constants.B_SECTION_ARRAY_LIST);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_microblog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
