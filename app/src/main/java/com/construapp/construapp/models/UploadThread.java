package com.construapp.construapp.models;

import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class UploadThread extends Thread {
    private File file;
    private TransferUtility transferUtility;
    private String fileKey;
    private String s3BucketName;

    public UploadThread(File file, TransferUtility transferUtility, String fileKey, String s3BucketName) {
        this.file = file;
        this.transferUtility = transferUtility;
        this.fileKey = fileKey;
        this.s3BucketName = s3BucketName;
    }

    public void run() {
        try {
            transferUtility.upload(
                    s3BucketName,     /* The bucket to upload to */
                    fileKey,    /* The key for the uploaded object */
                    file        /* The file where the data to upload exists */
            );
        }catch (Exception ex){}
    }
}
