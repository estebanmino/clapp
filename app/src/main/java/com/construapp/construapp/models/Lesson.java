package com.construapp.construapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 18-09-2017.
 */

@Entity
public class Lesson {

    @TypeConverters(MultimediaFile.class)


    public ArrayList<MultimediaFile> getMultimediaPictureFiles() {
        return multimediaPictureFiles;
    }

    private ArrayList<MultimediaFile> multimediaPictureFiles;

    public ArrayList<MultimediaFile> getMultimediaAudioFiles() {
        return multimediaAudioFiles;
    }

    private ArrayList<MultimediaFile> multimediaAudioFiles;
    private ArrayList<MultimediaFile> multimediaDocumentsFiles;
    @PrimaryKey
    private String id;
    private String name;
    //TODO hay que hacer refactoring. Se usa summary y no description
    //private String summary;
    private String description;
    private String motivation;
    private String learning;
    private String user_id;
    private String company_id;

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getLearning() {
        return learning;
    }

    public void setLearning(String learning) {
        this.learning = learning;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<MultimediaFile> getMultimediaDocumentsFiles() {
        return multimediaDocumentsFiles;
    }

    public void setMultimediaDocumentsFiles(ArrayList<MultimediaFile> multimediaDocumentsFiles) {
        this.multimediaDocumentsFiles = multimediaDocumentsFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaPicturesFiles() {
        return multimediaPictureFiles;
    }

    public void setMultimediaPictureFiles(ArrayList<MultimediaFile> multimediaPictureFiles) {
        this.multimediaPictureFiles = multimediaPictureFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaAudiosFiles() {
        return multimediaAudioFiles;
    }

    public void setMultimediaAudioFiles(ArrayList<MultimediaFile> multimediaAudioFiles) {
        this.multimediaAudioFiles = multimediaAudioFiles;
    }

    public void initMultimediaFiles() {
        this.multimediaPictureFiles = new ArrayList<>();
        this.multimediaAudioFiles = new ArrayList<>();
        this.multimediaDocumentsFiles = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

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
