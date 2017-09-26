package com.construapp.construapp.lessons_form;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.construapp.construapp.R;

/**
 * Created by ESTEBANFML on 25-09-2017.
 */

public class MultimediaImageAdapter extends RecyclerView.Adapter<MultimediaImageAdapter.MultimediaImageViewHolder> {
    private String[] mToppings;

    public MultimediaImageAdapter(String[] mToppings) {
        this.mToppings = mToppings;
    }

    @Override
    public MultimediaImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //LessonFormActivity.MultimediaImageViewHolder vh = new LessonFormActivity.MultimediaImageViewHolder(v);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multimedia_image_card, parent, false);
        MultimediaImageViewHolder vh = new MultimediaImageViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MultimediaImageViewHolder holder, int position) {
        holder.imagePath.setText(mToppings[position]);
    }

    @Override
    public int getItemCount() {
        return mToppings.length;
    }

    public static final class MultimediaImageViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView imagePath;
        public ImageView imageThumbnail;


        public MultimediaImageViewHolder(View view) {
            super(view);

            imagePath = (TextView) view.findViewById(R.id.image_path);
            imageThumbnail = (ImageView) view.findViewById(R.id.image_thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.i("CLICKHORIZONTAL", ((TextView) view).getText().toString());
        }
    }

}

