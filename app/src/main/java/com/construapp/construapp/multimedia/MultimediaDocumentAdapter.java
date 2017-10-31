package com.construapp.construapp.multimedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.threading.MultimediaAudioDownloader;
import com.construapp.construapp.threading.MultimediaDocumentDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 07-10-2017.
 */

public class MultimediaDocumentAdapter extends MultimediaAdapter {

    private TransferUtility transferUtility;

    public MultimediaDocumentAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context, Lesson thisLesson) {
        super(mMultimediaFiles, context, thisLesson);
    }

    @Override
    public void onBindViewHolder(final MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        if (multimediaFile.getmPath() != null && new File(multimediaFile.getmPath()).exists()) {
            holder.btnDownload.setVisibility(View.GONE);
        }
        else {
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.btnDownload.setVisibility(View.GONE);
                    General constants = new General();
                    AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(getContext()));
                    transferUtility = new TransferUtility(s3, getContext());
                    MultimediaDocumentDownloader downloadDocumentMultimedia = new MultimediaDocumentDownloader(
                            new File(multimediaFile.getmPath()),
                            transferUtility,
                            multimediaFile.getFileS3Key(),
                            holder,
                            multimediaFile);
                    downloadDocumentMultimedia.download();
                    holder.btnDownload.setVisibility(View.GONE);
                }
            });
        }
        String path = multimediaFile.getmPath();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        holder.textPath.setText(filename);
        holder.multimediaFile = multimediaFile;
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
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    //IF CACHE

                    try {
                        openFile(getContext(),
                                Uri.parse(
                                        Uri.fromFile(new File(multimediaFile.getmPath())).toString()),
                                multimediaFile.getmPath());
                    } catch (Exception e) {}
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}
