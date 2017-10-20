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
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.threading.MultimediaDocumentDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 20-10-2017.
 */

public class MultimediaVideoAdapter extends MultimediaAdapter {

    private TransferUtility transferUtility;

    public MultimediaVideoAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    };

    @Override
    public void onBindViewHolder(final MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);


        if (super.getContext().getClass() == LessonActivity.class) {

            if (LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()) == null) {

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
                    }
                });
            } else {
                File file = (File) LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key());
            }
        }
        else {
            holder.btnDownload.setVisibility(View.GONE);
        }
        holder.multimediaFile = multimediaFile;
    }

    @Override
    public MultimediaVideoAdapter.MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_video_card, parent, false);
        MultimediaVideoAdapter.MultimediaViewHolder vh = new MultimediaVideoAdapter.MultimediaViewHolder(view);
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
                    if (multimediaFile.getFileS3Key() != null) {
                        Log.i("SELECTING","IN SET ACTION"+multimediaFile.getmPath());

                        intent.setDataAndType(Uri.parse(
                                //Uri.fromFile((File)LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key())).toString()),
                                Uri.fromFile(new File(multimediaFile.getmPath())).toString()),

                                "video/*");
                        Intent chooser3 = Intent.createChooser(intent, "Elige una aplicación");

                        view.getContext().startActivity(chooser3);
                    } else {
                        intent.setDataAndType(Uri.parse(
                                Uri.fromFile(new File(multimediaFile.getmPath())).toString()
                        ), "video/*");
                        Intent chooser3 = Intent.createChooser(intent, "Elige una aplicación");

                        view.getContext().startActivity(chooser3);

                    }

                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }

}
