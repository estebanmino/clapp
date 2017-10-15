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

    public int getUserPermission(){
        //TODO: acá va la consulta que retorna el string con el permiso del usuario
        //mientras harcodeamos un tipo de permiso
        int userPermission = userPermissionsToInt("admin");
        return userPermission;
    }

    public int xmlPermissionTagToInt(String tag){
        int xmlPermission = Integer.parseInt(tag);
        return xmlPermission;
    }

    public int userPermissionsToInt(String permission){
        if (permission.equals("admin")){
            return 4;
        }
        else if (permission.equals("validate")){
            return 3;
        }
        else if (permission.equals("create")){
            return 2;
        }
        else if (permission.equals("read")){
            return 1;
        }
        else {
            return 0;
        }
    }
}
