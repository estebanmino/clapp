package com.construapp.construapp.threading;

import android.util.Log;
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

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;

/**
 * Created by ESTEBANFML on 11-10-2017.
 */

public class MultimediaDocumentDownloader {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private MultimediaAdapter.MultimediaViewHolder holder;
    private MultimediaFile multimediaFile;


    public MultimediaDocumentDownloader(File file, TransferUtility transferUtility,
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
                        LRUCache.getInstance().getLru().put(fileKey,file);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnDownload.setVisibility(View.GONE);
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
        catch (Exception e) {
            Log.i("ERROR",e.toString());}
    }
}
