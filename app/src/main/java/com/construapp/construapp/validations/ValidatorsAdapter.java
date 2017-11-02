package com.construapp.construapp.validations;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;
import com.construapp.construapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ESTEBANFML on 31-10-2017.
 */

public class ValidatorsAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<User> usersList;

    public ValidatorsAdapter(Context context, ArrayList<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @Override
    public int getCount() {
        if(usersList != null) {
            return usersList.size();
        }

        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.validator_list_item,null);
        }

        TextView textUserFullName = convertView.findViewById(R.id.user_full_name);
        TextView textUserPosition = convertView.findViewById(R.id.user_position);

        final String userFirstName = usersList.get(position).getFirstName();
        final String userLastName = usersList.get(position).getLastName();
        final String userPosition = usersList.get(position).getPosition();

        textUserFullName.setText(userFirstName+" "+userLastName);
        textUserPosition.setText(userPosition);

        return convertView;
    }
}
