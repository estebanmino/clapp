package com.construapp.construapp.models;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;

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
        Log.i("STARTING DOWNLOAD", "DOWNLOADINT");
        try {
            TransferObserver observer = transferUtility.download(
                    "construapp",     /* The bucket to download from */
                    "PICTURE/1234.jpg",    /* The key for the object to download */
                    file        /* The file to download the object to */
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == COMPLETED) {
                        Log.i("DOWNLOAD","COMPLETED");
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    int percentage = (int) (bytesCurrent/(bytesTotal+1) * 100);
                    Log.i("DPWNLOADING", Integer.toString(percentage));
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.i("DPWNLOADING", "error");

                }
            });
            return true;
        }
        catch (Exception e) {
        }

        return false;
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.i("TASK COMPLETED","idhuewhieugdwiye");
    }
}
