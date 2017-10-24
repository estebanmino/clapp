package com.construapp.construapp.threading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.view.View;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAdapter;

import java.io.File;
import java.io.IOException;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;

/**
 * Created by ESTEBANFML on 10-10-2017.
 */

public class MultimediaPictureDownloader {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private MultimediaAdapter.MultimediaViewHolder holder;
    private MultimediaFile multimediaFile;


    public MultimediaPictureDownloader(File file, TransferUtility transferUtility,
                                       String fileKey, MultimediaAdapter.MultimediaViewHolder holder,
                                       MultimediaFile multimediaFile)
    {
        this.file = file;
        this.transferUtility = transferUtility;
        this.fileKey = fileKey;
        this.holder = holder;
        this.multimediaFile = multimediaFile;
    }

    public void download(){
        try {
            TransferObserver observer = transferUtility.download(
                    Constants.S3_BUCKET,     /* The bucket to download from */
                    fileKey,    /* The key for the object to download */
                    file        /* The file to download the object to */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == COMPLETED) {
                        Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
                        if (bitmap!=null) {
                            Bitmap rotatedBitmap = rotateBitmap(bitmap, multimediaFile);
                            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(rotatedBitmap, 80, 80));
                            LRUCache.getInstance().getLru().put(fileKey, rotatedBitmap);
                        }
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent/(bytesTotal+1) * 100);
                }

                @Override
                public void onError(int id, Exception ex) {
                }
            });
        }
        catch (Exception e) {}
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, MultimediaFile multimediaFile) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(multimediaFile.getmPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
