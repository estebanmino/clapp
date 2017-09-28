package com.construapp.construapp.models;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

/**
 * Created by ESTEBANFML on 27-09-2017.
 */

public class DownloadMultimediaAsyncTask extends AsyncTask {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private String s3BucketName;

    public DownloadMultimediaAsyncTask(File file, TransferUtility transferUtility, String fileKey, String s3BucketName) {
        this.file = file;
        this.transferUtility = transferUtility;
        this.fileKey = fileKey;
        this.s3BucketName = s3BucketName;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            TransferObserver observer = transferUtility.download(
                    s3BucketName,     /* The bucket to download from */
                    fileKey,    /* The key for the object to download */
                    file        /* The file to download the object to */
            );
        }
        catch (Exception e) {

        }

        return null;
    }
}
