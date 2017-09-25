package com.construapp.construapp.lessons_form;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ESTEBANFML on 25-09-2017.
 */

public class MultimediaImageAdapter extends RecyclerView.Adapter {
    final String[] mToppings = new String[3];

    @Override
    public MultimediaImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mToppings[0] = "Cheese";
        mToppings[1] = "Pepperoni";
        mToppings[2] = "Black Olives";
        View v = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false);
        MultimediaImageViewHolder vh = new MultimediaImageViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView tv = (TextView) holder.itemView;
        tv.setText(mToppings[position]);
    }

    @Override
    public int getItemCount() {
        return mToppings.length;
    }

}

