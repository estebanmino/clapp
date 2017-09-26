package com.construapp.construapp.lessons_form;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 25-09-2017.
 */

public class MultimediaImageAdapter extends RecyclerView.Adapter<MultimediaImageAdapter.MultimediaImageViewHolder> {

    private ArrayList<MultimediaFile> mMultimediaFiles;

    public MultimediaImageAdapter(ArrayList<MultimediaFile> mMultimediaFiles) {
        this.mMultimediaFiles = mMultimediaFiles;

    }

    @Override
    public MultimediaImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_card, parent, false);
        MultimediaImageViewHolder vh = new MultimediaImageViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MultimediaImageViewHolder holder, int position) {
        MultimediaFile multimediaFile = mMultimediaFiles.get(position);

        switch (multimediaFile.getExtension()){
            case "PICTURE":
                Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
                holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
                holder.imageThumbnail.setRotation(90);
                holder.multimediaFile = multimediaFile;
                break;

            case "AUDIO":
                holder.multimediaFile =  multimediaFile;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMultimediaFiles.size();
    }

    public static final class MultimediaImageViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView imageThumbnail;
        public Boolean mStartPlaying = true;
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
            Log.d("PLAYING", "stopPlaying: ");
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
                Log.i("PLAYING", "TRUE");
                mediaPlayer = new MediaPlayer();
                startPlaying(mediaPlayer);
            } else {
                Log.i("PLAYING", "FALSE");
                stopPlaying(mediaPlayer);
                //mediaPlayer = new MediaPlayer();
            }
        }

        public MultimediaImageViewHolder(View view) {
            super(view);

            imageThumbnail = view.findViewById(R.id.image_thumbnail);
            final MediaPlayer mediaPlayer = new MediaPlayer();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (multimediaFile.getExtension()) {
                        case "PICTURE":
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(
                                    Uri.fromFile(new File(multimediaFile.getmPath())).toString()), "image/*");
                            view.getContext().startActivity(intent);
                            break;
                        case "AUDIO":
                            //CHECK FOR CORRECT MEDIA PLAYER STOP
                            onPlay(mStartPlaying,mediaPlayer);
                            mStartPlaying = !mStartPlaying;
                            break;
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }
}

