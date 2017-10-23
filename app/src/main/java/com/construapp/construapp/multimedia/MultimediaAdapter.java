package com.construapp.construapp.multimedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 06-10-2017.
 */

public abstract class MultimediaAdapter  extends RecyclerView.Adapter<MultimediaAdapter.MultimediaViewHolder> {

    private ArrayList<MultimediaFile> mMultimediaFiles;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<MultimediaFile> getmMultimediaFiles() {
        return mMultimediaFiles;
    }

    public void setmMultimediaFiles(ArrayList<MultimediaFile> mMultimediaFiles) {
        this.mMultimediaFiles = mMultimediaFiles;
    }

    public MultimediaAdapter(ArrayList<MultimediaFile> mMultimediaFiles, Context context) {
        this.mMultimediaFiles = mMultimediaFiles;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MultimediaAdapter.MultimediaViewHolder holder, int position) {
        MultimediaFile multimediaFile = mMultimediaFiles.get(position);
        multimediaFile.setArrayPosition(position);


        holder.multimediaFile = multimediaFile;

    }

    @Override
    public int getItemCount() {
        return mMultimediaFiles.size();
    }

    public class MultimediaViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ImageView imageThumbnail;
        public TextView textPath;
        public ImageButton imageButtonDelete;
        public ProgressBar progressBar;
        public Button btnDownload;



        MultimediaFile multimediaFile;
        ArrayList<MultimediaFile> multimediaFileArrayList;

        public MultimediaViewHolder(View view) {
            super(view);
            imageThumbnail = view.findViewById(R.id.image_thumbnail);
            textPath = view.findViewById(R.id.image_path);
            imageButtonDelete = view.findViewById(R.id.image_button_delete);
            progressBar = view.findViewById(R.id.progress_bar);
            btnDownload = view.findViewById(R.id.btn_download);

            if (getContext().getClass() == LessonActivity.class && ((LessonActivity)context).getEditing()) {
                imageButtonDelete.setVisibility(View.GONE);
            }

            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MultimediaAdapter.this.mMultimediaFiles.remove(multimediaFile.getArrayPosition());
                    MultimediaAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
        }
    }

    public void openFile(Context context, Uri uri, String url) throws IOException {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String title = "Elige una aplicación";

        // so Android knew what application to use to open the file
        if (url.contains(".doc") || url.contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.contains(".xls") || url.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.contains(".wav") || url.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        Intent chooser = Intent.createChooser(intent, title);
        if (chooser.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }
}
