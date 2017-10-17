package com.construapp.construapp.models;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */


public class MultimediaFile {

    private String mPath;
    private UploadMultimediaAsyncTask uploadThread;
    private DownloadMultimediaAsyncTask downloadThread;
    private String s3BucketName;
    private String fileKey;
    private TransferUtility transferUtility;
    private String extension;
    private int arrayPosition;



    public int getArrayPosition() {
        return arrayPosition;
    }

    public void setArrayPosition(int arrayPosition) {
        this.arrayPosition = arrayPosition;
    }

    public String getExtension() {
        return extension;
    }

    public UploadMultimediaAsyncTask getUploadThread() {
        return uploadThread;
    }

    public DownloadMultimediaAsyncTask getDownloadThread() {return downloadThread; }

    public void setUploadThread(UploadMultimediaAsyncTask uploadThread) {
        this.uploadThread = uploadThread;
    }

    public void setDownloadThread(DownloadMultimediaAsyncTask downloadThread) {
        this.downloadThread = downloadThread;
    }

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
        this.uploadThread = new UploadMultimediaAsyncTask(file,transferUtility,fileKey,s3BucketName, extension);
        try {
            this.uploadThread.execute().get();
        } catch (ExecutionException e) {

        } catch (InterruptedException es) {

        }
    }

    public boolean initDownloadThread() {
        File file = new File(mPath);
        fileKey = file.getName();
        this.downloadThread = new DownloadMultimediaAsyncTask(file,transferUtility,fileKey,s3BucketName);
        try {
            return (Boolean) this.downloadThread.execute().get();
        } catch (ExecutionException e) {

        } catch (InterruptedException es) {

        }
        return false;
    }


}
