package com.construapp.construapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

/**
 * Created by ESTEBANFML on 18-09-2017.
 */

@Entity
public class Lesson {

    @Ignore
    private ArrayList<MultimediaFile> multimediaPictureFiles;

    @Ignore
    private ArrayList<MultimediaFile> multimediaAudioFiles;
    @Ignore
    private ArrayList<MultimediaFile> multimediaDocumentsFiles;
    @Ignore
    private ArrayList<MultimediaFile> multimediaVideosFiles;
    @PrimaryKey
    private String id;
    private String name;
    //TODO hay que hacer refactoring. Se usa summary y no description
    //private String summary;
    private String description;
    private String motivation;
    private String learning;
    private String validation;
    private String user_id;
    private String project_id;
    private String company_id;
    private boolean validator = false;
    private boolean validator_sec = false;

    public boolean isValidator_sec() {
        return validator_sec;
    }

    public void setValidator_sec(boolean validator_sec) {
        this.validator_sec = validator_sec;
    }

    public boolean isValidator() {
        return validator;
    }

    public void setValidator(boolean validator) {
        this.validator = validator;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

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

    //Getter & Setter MultimediaDocuments
    public ArrayList<MultimediaFile> getMultimediaDocumentsFiles() {
        return multimediaDocumentsFiles;
    }

    public void setMultimediaDocumentsFiles(ArrayList<MultimediaFile> multimediaDocumentsFiles) {
        this.multimediaDocumentsFiles = multimediaDocumentsFiles;
    }

    //Getter & Setter MultimediaPictures
    public ArrayList<MultimediaFile> getMultimediaPicturesFiles() {
        return multimediaPictureFiles;
    }

    public void setMultimediaPictureFiles(ArrayList<MultimediaFile> multimediaPictureFiles) {
        this.multimediaPictureFiles = multimediaPictureFiles;
    }

    //Getter & Setter MultimediaVideos
    public ArrayList<MultimediaFile> getMultimediaVideosFiles() {
        return multimediaVideosFiles;
    }

    public void setMultimediaVideosFiles(ArrayList<MultimediaFile> multimediaVideosFiles) {
        this.multimediaVideosFiles = multimediaVideosFiles;
    }

    //Getter & Setter MultimediaDocuments
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
        this.multimediaVideosFiles = new ArrayList<>();
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
