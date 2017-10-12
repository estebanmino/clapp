package com.construapp.construapp.models;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class Constants {
    public CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:4990ac44-6c36-4b4c-8193-70148fbd35d6", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
    }

    public void setBackground(ImageView view, int flag){
        flag = flag%2;
        if (flag == 0){
            view.setBackgroundColor(Color.GREEN);
        }
        else {
            view.setBackgroundColor(Color.RED);
        }
    }

    public boolean setPermissionsBoolean(int flag){
        flag = flag%2;
        boolean permissions = false;
        if (flag == 0){
            permissions = true;
        }
        else {
            permissions = false;
        }
        return  permissions;
    }

}
