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

    @Ignore
    private ArrayList<String> deletedMultimediaFilesS3Keys;

    @PrimaryKey
    private String id;

    private String name;
    private String summary;
    private String motivation;
    private String learning;
    private String validation;
    private String userId;
    private String projectId;
    private String rejectComment;
    private String companyId;
    private String validator;
    private String validatorSec;


    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }


    public String getValidatorSec() {
        return validatorSec;
    }

    public void setValidatorSec(String validatorSec) {
        this.validatorSec = validatorSec;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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
        this.deletedMultimediaFilesS3Keys = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public ArrayList<String> getDeletedMultimediaFilesS3Keys() {
        return deletedMultimediaFilesS3Keys;
    }

    public void setDeletedMultimediaFilesS3Keys(ArrayList<String> deletedMultimediaFilesS3Keys) {
        this.deletedMultimediaFilesS3Keys = deletedMultimediaFilesS3Keys;
    }

    public ArrayList<String> getAddedMultimediaKeysS3(){
        ArrayList<String> addedKeys = new ArrayList<>();
        String start = Constants.S3_LESSONS_PATH+"/"+id+"/";
        String key = "";

        for (MultimediaFile mmVideo: multimediaVideosFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_VIDEOS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaDocumentsFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_DOCS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaAudioFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_AUDIOS_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        for (MultimediaFile mmVideo: multimediaPictureFiles) {
            if (mmVideo.getAdded() == 1) {
                key = Constants.S3_IMAGES_PATH+"/"+mmVideo.getmPath().substring(mmVideo.getmPath().lastIndexOf("/")+1);
                addedKeys.add(start+key);
            }
        }
        return addedKeys;
    }

    public Boolean isEmptyMultimedia() {
        return multimediaAudioFiles.size() == 0 && multimediaPictureFiles.size() == 0
                && multimediaDocumentsFiles.size() == 0 && multimediaVideosFiles.size() == 0;
    }

    public ArrayList<String> getFormAttributes() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(name);
        arrayList.add(summary);
        arrayList.add(learning);
        arrayList.add(motivation);
        return arrayList;
    }
}
