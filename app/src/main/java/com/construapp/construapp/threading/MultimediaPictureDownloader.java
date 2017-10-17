package com.construapp.construapp.threading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.models.MultimediaFile;
import com.construapp.construapp.multimedia.MultimediaAdapter;

import java.io.File;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;

/**
 * Created by ESTEBANFML on 10-10-2017.
 */

public class MultimediaPictureDownloader {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private String s3BucketName;
    private MultimediaAdapter.MultimediaViewHolder holder;
    private MultimediaFile multimediaFile;


    public MultimediaPictureDownloader(File file, TransferUtility transferUtility,
                                String fileKey, String s3BucketName, MultimediaAdapter.MultimediaViewHolder holder,
                                MultimediaFile multimediaFile)
    {
        this.file = file;
        this.transferUtility = transferUtility;
        this.fileKey = fileKey;
        this.s3BucketName = s3BucketName;
        this.holder = holder;
        this.multimediaFile = multimediaFile;
    }

    public void download(){
        try {
            TransferObserver observer = transferUtility.download(
                    s3BucketName,     /* The bucket to download from */
                    fileKey,    /* The key for the object to download */
                    file        /* The file to download the object to */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == COMPLETED) {
                        Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
                        holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
                        LRUCache.getInstance().getLru().put(fileKey,bitmap);
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
}
