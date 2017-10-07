package com.construapp.construapp.multimedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 07-10-2017.
 */

public class MultimediaDocumentAdapter extends MultimediaAdapter {

    public MultimediaDocumentAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    };

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        String path=multimediaFile.getmPath();
        String filename=path.substring(path.lastIndexOf("/")+1);
        holder.textPath.setText(filename);
        holder.multimediaFile =  multimediaFile;

    }

    @Override
    public MultimediaDocumentAdapter.MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_document_card, parent, false);
        MultimediaDocumentAdapter.MultimediaViewHolder vh = new MultimediaDocumentAdapter.MultimediaViewHolder(view);
        return vh;
    }

    public class MultimediaViewHolder
            extends MultimediaAdapter.MultimediaViewHolder
            implements View.OnClickListener {

        public MultimediaViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

}
