package com.construapp.construapp.models;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 18-09-2017.
 */

public class Lesson {

    private ArrayList<MultimediaFile> multimediaFiles;
    private String name;
    private String description;


    public ArrayList<MultimediaFile> getMultimediaFiles() {
        return multimediaFiles;
    }

    public void setMultimediaFiles(ArrayList<MultimediaFile> multimediaFiles) {
        this.multimediaFiles = multimediaFiles;
    }

    public void initMultimediaFiles() {
        this.multimediaFiles = new ArrayList<>();
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

    public void addMultimediaFile(String path) {
        MultimediaFile multimediaFile  = new MultimediaFile();
        multimediaFile.setmPath(path);
        multimediaFiles.add(multimediaFile);
    }

}
