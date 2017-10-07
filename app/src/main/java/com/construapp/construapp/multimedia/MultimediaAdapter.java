package com.construapp.construapp.multimedia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.construapp.construapp.LessonActivity;
import com.construapp.construapp.R;
import com.construapp.construapp.models.MultimediaFile;

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

        if (context.getClass() == LessonActivity.class) {
            holder.imageButtonDelete.setVisibility(View.GONE);
        }
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

        MultimediaFile multimediaFile;
        ArrayList<MultimediaFile> multimediaFileArrayList;

        public MultimediaViewHolder(View view) {
            super(view);
            imageThumbnail = view.findViewById(R.id.image_thumbnail);
            textPath = view.findViewById(R.id.image_path);
            imageButtonDelete = view.findViewById(R.id.image_button_delete);

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
}
