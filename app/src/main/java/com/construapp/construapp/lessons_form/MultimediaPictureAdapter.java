package com.construapp.construapp.lessons_form;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

public class MultimediaPictureAdapter extends MultimediaAdapter {

    private static String FILE_TYPE = "image/*";

    public MultimediaPictureAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    };

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        if (super.getContext().getClass() != LessonActivity.class) {
            Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
            holder.imageThumbnail.setRotation(90);
        }
    }

    @Override
    public MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_picture_card, parent, false);
        MultimediaViewHolder vh = new MultimediaViewHolder(view);
        return vh;
    }

    public class MultimediaViewHolder
            extends MultimediaAdapter.MultimediaViewHolder
            implements View.OnClickListener {

        MultimediaFile multimediaFile;
        ArrayList<MultimediaFile> multimediaFileArrayList;

        public MultimediaViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(
                            Uri.fromFile(new File(MultimediaViewHolder.super.multimediaFile.getmPath())).toString()), FILE_TYPE);
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

}
