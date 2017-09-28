package com.construapp.construapp.models;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class UploadMultimediaAsyncTask extends AsyncTask {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private String s3BucketName;
    private String extension;

    public UploadMultimediaAsyncTask(File file, TransferUtility transferUtility, String fileKey, String s3BucketName, String extension) {
        this.file = file;
        this.transferUtility = transferUtility;
        this.fileKey = fileKey;
        this.s3BucketName = s3BucketName;
        this.extension = extension;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            transferUtility.upload(
                    s3BucketName,     /* The bucket to upload to */
                    extension+"/"+fileKey,    /* The key for the uploaded object */
                    file        /* The file where the data to upload exists */
            );
        }catch (Exception ex){}
        return null;
    }
}
