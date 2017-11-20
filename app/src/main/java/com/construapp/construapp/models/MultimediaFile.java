package com.construapp.construapp.models;

import android.util.Log;

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
    private String apiFileKey;
    private TransferUtility transferUtility;
    private int added;
    private String fromObject;

    public MultimediaFile(String fromObject, String extension, String mPath, TransferUtility transferUtility,String lesson_id, int added){
        this.fromObject = fromObject;
        this.extension = extension;
        this.mPath = mPath;
        this.transferUtility = transferUtility;
        this.apiFileKey = fromObject+File.separator+lesson_id+File.separator+extension+ File.separator +
                mPath.substring(mPath.lastIndexOf(File.separator) + 1);
        this.added = added;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }


    public void setExtension(String extension) {
        this.extension = extension;
    }

    private String extension;
    private int arrayPosition;


    public String getApiFileKey() {
        return apiFileKey;
    }

    public void setApiFileKey(String apiFileKey) {
        this.apiFileKey = apiFileKey;
    }


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

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public void initUploadThread() {
        File file = new File(mPath);
        fileKey = file.getName();
        this.uploadThread = new UploadMultimediaAsyncTask(file,transferUtility,apiFileKey);
        try {
            this.uploadThread.execute().get();
        } catch (ExecutionException e) {

        } catch (InterruptedException es) {

        }
    }

    public String getFileName() {
        return  mPath.substring(mPath.lastIndexOf("/")+1);
    }



}
