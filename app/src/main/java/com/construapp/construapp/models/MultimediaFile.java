package com.construapp.construapp.models;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.construapp.construapp.threading.UploadMultimediaAsyncTask;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */


public class MultimediaFile {

    private String mPath;
    private UploadMultimediaAsyncTask uploadThread;
    private String fileKey;
    private String fileS3Key;
    private TransferUtility transferUtility;

    public void setExtension(String extension) {
        this.extension = extension;
    }

    private String extension;
    private int arrayPosition;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

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

    public void setUploadThread(UploadMultimediaAsyncTask uploadThread) {
        this.uploadThread = uploadThread;
    }


    public MultimediaFile(String extension, String mPath, String fileS3Key, TransferUtility transferUtility){
        this.extension = extension;
        this.mPath = mPath;
        this.transferUtility = transferUtility;
        this.fileS3Key = fileS3Key;
    }

    public String getFileS3Key() {
        return fileS3Key;
    }

    public void setFileS3Key(String fileS3Key) {
        this.fileS3Key = fileS3Key;
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
        this.uploadThread = new UploadMultimediaAsyncTask(file,transferUtility,fileKey, extension);
        try {
            this.uploadThread.execute().get();
        } catch (ExecutionException e) {

        } catch (InterruptedException es) {

        }
    }


}
