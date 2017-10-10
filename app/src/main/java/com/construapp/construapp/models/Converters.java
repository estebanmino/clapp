package com.construapp.construapp.models;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jose on 10-10-17.
 */

public class Converters {
    @TypeConverter
    public static ArrayList<MultimediaFile> fromString(String value) {
        Type listType = new TypeToken<ArrayList<MultimediaFile>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<MultimediaFile> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
