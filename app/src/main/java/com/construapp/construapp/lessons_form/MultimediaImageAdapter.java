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
 * Created by ESTEBANFML on 25-09-2017.
 */

public class MultimediaImageAdapter extends RecyclerView.Adapter<MultimediaImageAdapter.MultimediaImageViewHolder> {

    private ArrayList<MultimediaFile> mMultimediaFiles;
    private Context context;

    public MultimediaImageAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        this.mMultimediaFiles = mMultimediaFiles;
        this.context = context;

    }

    @Override
    public MultimediaImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch(mMultimediaFiles.get(0).getExtension()){
            case "PICTURE":
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_picture_card, parent, false);
                break;

            case "AUDIO":
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_audio_card, parent, false);
                break;

            case "DOCUMENT":
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_document_card, parent, false);
                break;
        }
        MultimediaImageViewHolder vh = new MultimediaImageViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MultimediaImageViewHolder holder, int position) {
        MultimediaFile multimediaFile = mMultimediaFiles.get(position);
        multimediaFile.setArrayPosition(position);

        switch (multimediaFile.getExtension()){
            case "PICTURE":
                if (context.getClass() != LessonActivity.class) {

                    Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
                    holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
                    holder.imageThumbnail.setRotation(90);
                } else {
                    holder.imageButtonDelete.setVisibility(View.GONE);
                }
                holder.multimediaFile = multimediaFile;

                break;

            case "AUDIO":
                if (context.getClass() == LessonActivity.class) {
                    holder.imageButtonDelete.setVisibility(View.GONE);
                }
                holder.multimediaFile =  multimediaFile;
                break;

            case "DOCUMENT":
                if (context.getClass() == LessonActivity.class) {
                    holder.imageButtonDelete.setVisibility(View.GONE);
                }
                String path=multimediaFile.getmPath();
                String filename=path.substring(path.lastIndexOf("/")+1);
                holder.textPath.setText(filename);
                holder.multimediaFile =  multimediaFile;
                holder.multimediaFileArrayList = mMultimediaFiles;

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMultimediaFiles.size();
    }

    public class MultimediaImageViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView imageThumbnail;
        public TextView textPath;
        public ImageButton imageButtonDelete;
        public Boolean mStartPlaying = true;

        MultimediaFile multimediaFile;
        ArrayList<MultimediaFile> multimediaFileArrayList;

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
            textPath = view.findViewById(R.id.image_path);
            imageButtonDelete = view.findViewById(R.id.image_button_delete);

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MultimediaImageAdapter.this.mMultimediaFiles.remove(multimediaFile.getArrayPosition());
                    MultimediaImageAdapter.this.notifyDataSetChanged();
                }
            });

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

