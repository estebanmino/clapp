package com.construapp.construapp.models;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 18-09-2017.
 */

public class Lesson {

    private ArrayList<MultimediaFile> multimediaPictureFiles;
    private ArrayList<MultimediaFile> multimediaAudioFiles;
    private ArrayList<MultimediaFile> multimediaDocumentsFiles;
    private String name;
    private String description;

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

    public ArrayList<MultimediaFile> getMultimediaAudioFiles() {
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

}
