package com.construapp.construapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.graphics.Color;
import android.widget.ImageView;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 23-09-2017.
 */

public class General {
    public CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:d7b13ecc-c9d1-405c-a1ae-bb15abd67859", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
    }

    public void setUserPermission(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SP_CONSTRUAPP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        int permissionInt = userPermissionsToInt(sharedpreferences.getString(Constants.SP_USER_PERMISSION,""));
        String permissionString = Integer.toString(permissionInt);
        editor.putString("user_permission", permissionString);
        editor.apply();
    }

    public int xmlPermissionTagToInt(String tag){
        return Integer.parseInt(tag);
    }

    private int userPermissionsToInt(String permission){
        Log.i("PROJECTPERMISSION",permission);
        switch (permission){
            case "4": return 4;
            case "3": return 3;
            case "2": return 2;
            case "1": return 1;
            default: return 0;
        }
    }

    public String urlFrom(String[] strings) {
        return TextUtils.join("/",strings);
    }
}
