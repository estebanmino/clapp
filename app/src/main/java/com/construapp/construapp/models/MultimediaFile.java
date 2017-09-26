package com.construapp.construapp.models;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class MultimediaFile {

    private String mPath;
    private UploadThread uploadThread;
    private String s3BucketName;
    private String fileKey;
    private TransferUtility transferUtility;

    public String getExtension() {
        return extension;
    }

    private String extension;

    public MultimediaFile(String extension, String mPath, TransferUtility transferUtility, String s3BucketName){
        this.extension = extension;
        this.mPath = mPath;
        this.transferUtility = transferUtility;
        this.s3BucketName = s3BucketName;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public void initUploadThread() {
        File file = new File(mPath);
        fileKey = file.getName();
        this.uploadThread = new UploadThread(file,transferUtility,fileKey,s3BucketName);
        this.uploadThread.start();
    }

}
