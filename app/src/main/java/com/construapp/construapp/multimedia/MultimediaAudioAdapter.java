package com.construapp.construapp.multimedia;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

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
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);
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

        public void startPlaying(MediaPlayer mPlayer){
            try {
                mPlayer.setDataSource(MultimediaViewHolder.super.multimediaFile.getmPath());
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
