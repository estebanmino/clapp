package com.construapp.construapp.lessons_form;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public class MultimediaAudioAdapter extends MultimediaAdapter {

    public MultimediaAudioAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    };

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);
    }

    @Override
    public MultimediaAdapter.MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_picture_card, parent, false);
        MultimediaAdapter.MultimediaViewHolder vh = new MultimediaAdapter.MultimediaViewHolder(view);
        return vh;
    }

    public class MultimediaViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        Boolean mStartPlaying = true;
        MultimediaFile multimediaFile;

        public void startPlaying(MediaPlayer mPlayer){
            try {
                mPlayer.setDataSource(multimediaFile.getmPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (Exception ex) {
            }
        }
        private void stopPlaying(MediaPlayer mediaPlayer) {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onPlay(boolean start, MediaPlayer mediaPlayer) {
            if (start) {
                mediaPlayer = new MediaPlayer();
                startPlaying(mediaPlayer);
            } else {
                stopPlaying(mediaPlayer);
                //mediaPlayer = new MediaPlayer();
            }
        }

        public MultimediaViewHolder(View view) {
            super(view);

            final MediaPlayer mediaPlayer = new MediaPlayer();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlay(mStartPlaying,mediaPlayer);
                    mStartPlaying = !mStartPlaying;
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }
}
