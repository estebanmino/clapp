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
    private String author_id;
    private String author_first_name;
    private String author_last_name;
    private String author_position;
    private String author_email;
    private String author_admin;
    private String project_id;
    private String reject_comment;
    private String company_id;
    private int trigger_id;
    private String validator;
    private String validator_sec;
    private String comments;
    private String disciplines;
    private String departments;
    private String classifications;
    private String tags;

    public String getAuthor_first_name() {
        return author_first_name;
    }

    public void setAuthor_first_name(String author_first_name) {
        this.author_first_name = author_first_name;
    }

    public String getAuthor_last_name() {
        return author_last_name;
    }

    public void setAuthor_last_name(String author_last_name) {
        this.author_last_name = author_last_name;
    }

    public String getAuthor_position() {
        return author_position;
    }

    public void setAuthor_position(String author_position) {
        this.author_position = author_position;
    }

    public String getAuthor_email() {
        return author_email;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getAuthor_admin() {
        return author_admin;
    }

    public void setAuthor_admin(String author_admin) {
        this.author_admin = author_admin;
    }

    public int getTrigger_id() {
        return trigger_id;
    }

    public void setTrigger_id(int trigger_id) {
        this.trigger_id = trigger_id;
    }

    public String getDisciplines() {
        return disciplines;
    }

    public String getDepartments() {
        return departments;
    }

    public String getClassifications() {
        return classifications;
    }

    public String getTags() {
        return tags;
    }

    public String[] getDisciplinesArray() {
        if (disciplines == null || disciplines.isEmpty()) {
            return new String[0];
        } else {
            return disciplines.substring(1).split("/");
        }
    }

    public void setDisciplines(String disciplines) {
        this.disciplines = disciplines;
    }

    public String[] getDepartmentsArray() {
        if (departments == null ||  departments.isEmpty()){
            return new String[0];
        } else {
            return departments.substring(1).split("/");
        }
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String[] getClassificationsArray() {
        if (classifications == null || classifications.isEmpty()) {
            return new String[0];
        } else {
            return classifications.substring(1).split("/");
        }
    }

    public void setClassifications(String classifications) {
        this.classifications = classifications;
    }

    public String[] getTagsArray() {
        if (tags == null ||  tags.isEmpty()){
            return new String[0];
        } else {
            return tags.substring(1).split("/");
        }
    }

    public String getTagsSpaced() {
        if (tags == null ||  tags.isEmpty()){
            return "";
        } else {
            return tags.substring(1).replace("/"," ");
        }
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReject_comment() {
        return reject_comment;
    }

    public void setReject_comment(String reject_comment) {
        this.reject_comment = reject_comment;
    }


    public String getValidator_sec() {
        return validator_sec;
    }

    public void setValidator_sec(String validator_sec) {
        this.validator_sec = validator_sec;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
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


    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
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

    public Boolean hasMultimediaFiles() {
        return
                multimediaPictureFiles.size() > 0 ||
                multimediaAudioFiles.size() > 0 ||
                multimediaVideosFiles.size() > 0 ||
                multimediaDocumentsFiles.size() > 0 ;
    }
}
