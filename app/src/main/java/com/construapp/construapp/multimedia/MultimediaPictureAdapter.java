package com.construapp.construapp.multimedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.construapp.construapp.PanoramicViewActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.General;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.threading.MultimediaPictureDownloader;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public class MultimediaPictureAdapter extends MultimediaAdapter {

    private TransferUtility transferUtility;

    private static String FILE_TYPE = "image/*";


    public MultimediaPictureAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    }

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(multimediaFile.getmPath()));
            for (Directory directory : metadata.getDirectories()) {
                if(directory.getName().equals(Constants.IMAGE_XMP)) {
                    holder.isPanoramic = true;
                    holder.imagePanoramic.setVisibility(View.VISIBLE);
                    break;
                }
            }
        } catch (Exception e) {}

        if (multimediaFile.getApiFileKey() != null && LRUCache.getInstance().getLru().get(multimediaFile.getApiFileKey()) != null) {
            Bitmap bitmap =  (Bitmap)LRUCache.getInstance().getLru().get(multimediaFile.getApiFileKey());
            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
        }
        else if (multimediaFile.getmPath() != null && new File(multimediaFile.getmPath()).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
            Bitmap bmRotated = rotateBitmap(bitmap, multimediaFile);
            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bmRotated, 80, 80));
        }
        else {
            General constants = new General();
            AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(getContext()));
            transferUtility = new TransferUtility(s3, getContext());

            MultimediaPictureDownloader downloadPictureMultimedia = new MultimediaPictureDownloader(
                    new File(multimediaFile.getmPath()),
                    transferUtility,
                    multimediaFile.getApiFileKey(),
                    holder,
                    multimediaFile);
            holder.progressBar.setVisibility(View.VISIBLE);
            downloadPictureMultimedia.download();
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

                    //IF CACHE
                    if (MultimediaViewHolder.super.isPanoramic) {
                        view.getContext().startActivity(PanoramicViewActivity.getIntent(getContext(),multimediaFile.getmPath()));
                    } else {
                        Intent intent = new Intent();

                        intent.setAction(Intent.ACTION_VIEW);

                        if (multimediaFile.getApiFileKey() != null && LRUCache.getInstance().getLru().get(multimediaFile.getApiFileKey()) != null) {
                            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                    (Bitmap) LRUCache.getInstance().getLru().get(multimediaFile.getApiFileKey()), "Title", null);
                            intent.setDataAndType(Uri.parse(path), FILE_TYPE);
                        } else {
                            File file = new File(getContext().getCacheDir(), multimediaFile.getFileName());
                            intent.setDataAndType(Uri.parse(multimediaFile.getmPath()
                            ), FILE_TYPE);
                        }
                        view.getContext().startActivity(intent);
                    }
                }

            });
        }

        @Override
        public void onClick(View view) {
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, MultimediaFile multimediaFile) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(multimediaFile.getmPath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            try {
                Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                //bitmap.recycle();
                return bmRotated;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
