package com.construapp.construapp.models;

import android.content.Context;
import android.content.SharedPreferences;
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

public class Constants {
    public CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:4990ac44-6c36-4b4c-8193-70148fbd35d6", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
    }

    public void setUserPermission(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences("ConstruApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        //TODO: acá va la consulta que retorna el string con el permiso del usuario
        //mientras harcodeamos un tipo de permiso
        int permissionInt = userPermissionsToInt("create");
        String permissionString = Integer.toString(permissionInt);
        editor.putString("user_permisson", permissionString);
        editor.commit();
    }

    public int xmlPermissionTagToInt(String tag){
        return Integer.parseInt(tag);
    }

    private int userPermissionsToInt(String permission){
        switch (permission){
            case "admin": return 4;
            case "validate": return 3;
            case "create": return 2;
            case "read": return 1;
            default: return 0;
        }
    }
}
