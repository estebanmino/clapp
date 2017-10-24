package com.construapp.construapp.multimedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
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
import com.construapp.construapp.threading.MultimediaPictureDownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public class MultimediaAudioAdapter extends MultimediaAdapter {

    private TransferUtility transferUtility;

    public MultimediaAudioAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context,Lesson thisLesson) {
        super(mMultimediaFiles, context,thisLesson);
    };

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        if (multimediaFile.getFileS3Key() != null && LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()) != null) {
        }
        else if (multimediaFile.getmPath() != null && new File(multimediaFile.getmPath()).exists()) {
        }
        else {
            General constants = new General();
            AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(getContext()));
            transferUtility = new TransferUtility(s3, getContext());
            MultimediaAudioDownloader downloadAudioMultimedia = new MultimediaAudioDownloader(
                    new File(multimediaFile.getmPath()),
                    transferUtility,
                    multimediaFile.getFileS3Key(),
                    holder,
                    multimediaFile);
            holder.progressBar.setVisibility(View.VISIBLE);

            downloadAudioMultimedia.download();
        }
    }

    @Override
    public MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_audio_card, parent, false);
        MultimediaViewHolder vh = new MultimediaViewHolder(view);
        return vh;
    }

    public class MultimediaViewHolder
            extends MultimediaAdapter.MultimediaViewHolder
            implements View.OnClickListener {

        Boolean mStartPlaying = true;
        MediaPlayer mPlayer = null;

        private void startPlaying() {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(MultimediaViewHolder.super.multimediaFile.getmPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
            }
        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
        }

        public void onPlay(boolean start) {
            if (start) {
                mPlayer = new MediaPlayer();
                startPlaying();
            } else {
                stopPlaying();
            }
        }

        public MultimediaViewHolder(View view) {
            super(view);

            final MediaPlayer mediaPlayer = new MediaPlayer();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlay(mStartPlaying);
                    mStartPlaying = !mStartPlaying;
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }
}
