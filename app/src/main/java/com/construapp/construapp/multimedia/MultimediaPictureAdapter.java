package com.construapp.construapp.multimedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.construapp.construapp.threading.MultimediaPictureDownloader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public class MultimediaPictureAdapter extends MultimediaAdapter {

    private static String BUCKET_NAME = "construapp";
    private TransferUtility transferUtility;

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
        else {
            if(LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()) == null) {
                General constants = new General();
                AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(getContext()));
                transferUtility = new TransferUtility(s3, getContext());
                MultimediaPictureDownloader downloadPictureMultimedia = new MultimediaPictureDownloader(
                        new File(multimediaFile.getmPath()),
                        transferUtility,
                        multimediaFile.getFileS3Key(),
                        holder,
                        multimediaFile);
                holder.progressBar.setVisibility(View.VISIBLE);

                downloadPictureMultimedia.download();
            }
            else {

                Bitmap bitmap =  (Bitmap)LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key());
                holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
            }
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

        public MultimediaViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    //IF CACHE
                    if (multimediaFile.getFileS3Key() != null) {
                        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                (Bitmap) LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()), "Title", null);
                        intent.setDataAndType(Uri.parse(path), FILE_TYPE);
                    } else {
                        intent.setDataAndType(Uri.parse(
                                Uri.fromFile(new File(MultimediaViewHolder.super.multimediaFile.getmPath())).toString()
                        ), FILE_TYPE);
                    }
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }




}
