package com.construapp.construapp.lessons_form;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ESTEBANFML on 25-09-2017.
 */

public class MultimediaImageViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public MultimediaImageViewHolder(View view) {
        super(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.i("CLICKHORIZONTAL", ((TextView) view).getText().toString());
    }
}

