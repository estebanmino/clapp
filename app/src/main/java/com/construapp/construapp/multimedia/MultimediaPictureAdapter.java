package com.construapp.construapp.multimedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.cache.LRUCache;
import com.construapp.construapp.models.Constants;
import com.construapp.construapp.models.MultimediaFile;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.thumb;
import static com.amazonaws.mobileconnectors.s3.transferutility.TransferState.COMPLETED;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public class MultimediaPictureAdapter extends MultimediaAdapter {

    private static String BUCKET_NAME = "construapp";
    private TransferUtility transferUtility;

    private static String FILE_TYPE = "image/*";

    public MultimediaPictureAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        super(mMultimediaFiles, context);
    };

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MultimediaFile multimediaFile = super.getmMultimediaFiles().get(position);
        multimediaFile.setArrayPosition(position);

        if (super.getContext().getClass() != LessonActivity.class) {
            Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
            holder.imageThumbnail.setRotation(90);
        }
        else {
            if((Bitmap) LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()) == null) {
                Constants constants = new Constants();
                AmazonS3 s3 = new AmazonS3Client(constants.getCredentialsProvider(getContext()));

                transferUtility = new TransferUtility(s3, getContext());

                DownloadMultimedia downloadMultimedia = new DownloadMultimedia(
                        new File(multimediaFile.getmPath()),
                        transferUtility,
                        multimediaFile.getFileS3Key(),
                        BUCKET_NAME,
                        holder,
                        multimediaFile);
                holder.progressBar.setVisibility(View.VISIBLE);

                downloadMultimedia.execute();
            }
            else {

                Bitmap bitmap =  (Bitmap)LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key());
                holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
            }
        }
    }

    @Override
    public MultimediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_picture_card, parent, false);
        MultimediaViewHolder vh = new MultimediaViewHolder(view);
        return vh;
    }

    public class MultimediaViewHolder
            extends MultimediaAdapter.MultimediaViewHolder
            implements View.OnClickListener {

        public MultimediaViewHolder(View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                            (Bitmap)LRUCache.getInstance().getLru().get(multimediaFile.getFileS3Key()), "Title", null);
                    intent.setDataAndType(Uri.parse(path), FILE_TYPE);
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
        }

    }

    public class DownloadMultimedia extends AsyncTask {
        private File file;
        private TransferUtility transferUtility;
        private String fileKey;
        private String s3BucketName;
        private MultimediaAdapter.MultimediaViewHolder holder;
        private MultimediaFile multimediaFile;

        public DownloadMultimedia(File file, TransferUtility transferUtility,
                                  String fileKey, String s3BucketName, MultimediaAdapter.MultimediaViewHolder holder,
                                    MultimediaFile multimediaFile)
        {
            this.file = file;
            this.transferUtility = transferUtility;
            this.fileKey = fileKey;
            this.s3BucketName = s3BucketName;
            this.holder = holder;
            this.multimediaFile = multimediaFile;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                TransferObserver observer = transferUtility.download(
                        BUCKET_NAME,     /* The bucket to download from */
                        fileKey,    /* The key for the object to download */
                        file        /* The file to download the object to */
                );

                observer.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == COMPLETED) {
                            Bitmap bitmap = BitmapFactory.decodeFile(multimediaFile.getmPath());
                            holder.imageThumbnail.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 80, 80));
                            LRUCache.getInstance().getLru().put(fileKey,bitmap);
                            holder.progressBar.setVisibility(View.GONE);
                            //file.delete();
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


}
