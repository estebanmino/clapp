package com.construapp.construapp.threading;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.construapp.construapp.models.Constants;

import java.io.File;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class UploadMultimediaAsyncTask extends AsyncTask {
    private File file;
    private TransferUtility transferUtility;
    private String apiFileKey;

    public UploadMultimediaAsyncTask(File file, TransferUtility transferUtility, String apiFileKey) {
        this.file = file;
        this.transferUtility = transferUtility;
        this.apiFileKey = apiFileKey;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.i("UPLOADINGTO",apiFileKey);
            transferUtility.upload(
                    Constants.S3_BUCKET,     /* The bucket to upload to */
                    apiFileKey,    /* The key for the uploaded object */
                    file        /* The file where the data to upload exists */
            );
        }catch (Exception ex){}
        return null;
    }
}
