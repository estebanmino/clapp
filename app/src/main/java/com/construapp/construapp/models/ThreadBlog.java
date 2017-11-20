package com.construapp.construapp.models;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jose on 06-11-17.
 */

public class ThreadBlog {


    private String id;
    private String userThreadId;
    private String title;
    private String text;

    private ArrayList<MultimediaFile> multimediaPictureFiles;
    private ArrayList<MultimediaFile> multimediaAudioFiles;
    private ArrayList<MultimediaFile> multimediaDocumentsFiles;
    private ArrayList<MultimediaFile> multimediaVideosFiles;

    private String[] savedMultimediaFileKeys;

    public ArrayList<MultimediaFile> getMultimediaPictureFiles() {
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

    public ArrayList<MultimediaFile> getMultimediaDocumentsFiles() {
        return multimediaDocumentsFiles;
    }

    public void setMultimediaDocumentsFiles(ArrayList<MultimediaFile> multimediaDocumentsFiles) {
        this.multimediaDocumentsFiles = multimediaDocumentsFiles;
    }

    public ArrayList<MultimediaFile> getMultimediaVideosFiles() {
        return multimediaVideosFiles;
    }

    public void setMultimediaVideosFiles(ArrayList<MultimediaFile> multimediaVideosFiles) {
        this.multimediaVideosFiles = multimediaVideosFiles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        if(text.length()>40) {
            return text.substring(0, 40) + "....";
        }
        else return text;
    }

    public String getAllText()
    {
        return text;
    }

    public String getUserThreadId(){return userThreadId;}

    public void setText(String text) {
        this.text = text;
    }

    public void setUserThreadId (String userThreadId) { this.userThreadId = userThreadId;}

    public ArrayList<String> getAddedMultimediaKeysS3(){
        ArrayList<String> addedKeys = new ArrayList<>();
        String start = Constants.S3_THREADS_PATH+"/"+id+"/";
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

    public Boolean hasMultimediaFiles() {
        return
                multimediaPictureFiles.size() > 0 ||
                        multimediaAudioFiles.size() > 0 ||
                        multimediaVideosFiles.size() > 0 ||
                        multimediaDocumentsFiles.size() > 0 ;
    }

    public void initMultimediaFiles() {
        this.multimediaPictureFiles = new ArrayList<>();
        this.multimediaAudioFiles = new ArrayList<>();
        this.multimediaDocumentsFiles = new ArrayList<>();
        this.multimediaVideosFiles = new ArrayList<>();
    }

    public String getMultimediaFileKeys(String new_lesson_id) {
        String path_input = "";
        for (MultimediaFile multimediaFile : this.getMultimediaPictureFiles()) {
            String apiFileKey = Constants.S3_THREADS_PATH + "/" + new_lesson_id + "/" +
                    Constants.S3_IMAGES_PATH + "/" +multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1);
            multimediaFile.setApiFileKey(apiFileKey);
            path_input += apiFileKey + ";";
        }
        for (MultimediaFile multimediaFile : this.getMultimediaAudioFiles()) {
            String apiFileKey = Constants.S3_THREADS_PATH + "/" + new_lesson_id + "/" +
                    Constants.S3_AUDIOS_PATH + "/" +multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1);
            multimediaFile.setApiFileKey(apiFileKey);
            path_input += apiFileKey + ";";
        }
        for (MultimediaFile multimediaFile : this.getMultimediaDocumentsFiles()) {
            String apiFileKey = Constants.S3_THREADS_PATH + "/" + new_lesson_id + "/" +
                    Constants.S3_DOCS_PATH +"/" + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1);
            multimediaFile.setApiFileKey(apiFileKey);
            path_input += apiFileKey + ";";
        }

        for (MultimediaFile multimediaFile : this.getMultimediaVideosFiles()) {
            String apiFileKey = Constants.S3_THREADS_PATH + "/" + new_lesson_id + "/" +
                    Constants.S3_VIDEOS_PATH +"/" + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf("/") + 1);
            multimediaFile.setApiFileKey(apiFileKey);
            path_input += apiFileKey + ";";
        }
        return path_input;
    }

    public String getMultimediaOriginalFileKeys(String new_lesson_id) {
        String path_input = "";
        for (MultimediaFile multimediaFile : this.getMultimediaPictureFiles()) {
            if (multimediaFile.getAdded() == 0) {
                String apiFileKey = Constants.S3_THREADS_PATH + File.separator + new_lesson_id + File.separator +
                        Constants.S3_IMAGES_PATH + File.separator + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf(File.separator) + 1);
                multimediaFile.setApiFileKey(apiFileKey);
                path_input += apiFileKey + ";";
            }
        }
        for (MultimediaFile multimediaFile : this.getMultimediaAudioFiles()) {
            if (multimediaFile.getAdded() == 0) {
                String apiFileKey = Constants.S3_THREADS_PATH + File.separator + new_lesson_id + File.separator +
                        Constants.S3_AUDIOS_PATH + File.separator + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf(File.separator) + 1);
                multimediaFile.setApiFileKey(apiFileKey);
                path_input += apiFileKey + ";";
            }
        }
        for (MultimediaFile multimediaFile : this.getMultimediaDocumentsFiles()) {
            if (multimediaFile.getAdded() == 0) {
                String apiFileKey = Constants.S3_THREADS_PATH + File.separator + new_lesson_id + File.separator +
                        Constants.S3_DOCS_PATH + File.separator + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf(File.separator) + 1);
                multimediaFile.setApiFileKey(apiFileKey);
                path_input += apiFileKey + ";";
            }
        }

        for (MultimediaFile multimediaFile : this.getMultimediaVideosFiles()) {
            if (multimediaFile.getAdded() == 0) {
                String apiFileKey = Constants.S3_THREADS_PATH + File.separator + new_lesson_id + File.separator +
                        Constants.S3_VIDEOS_PATH + File.separator + multimediaFile.getmPath().substring(multimediaFile.getmPath().lastIndexOf(File.separator) + 1);
                multimediaFile.setApiFileKey(apiFileKey);
                path_input += apiFileKey + ";";
            }
        }
        return path_input;
    }

    public String[] getMultimediaOriginalFileKeysArray() {
        if (getMultimediaOriginalFileKeys(id) == null ||  getMultimediaOriginalFileKeys(id).isEmpty()){
            return new String[0];
        } else {
            return getMultimediaOriginalFileKeys(id).split(";");
        }
    }

    public void setSavedMultimediaFileKeys() {
        savedMultimediaFileKeys = getMultimediaOriginalFileKeysArray();
    }

    public String[] getSavedMultimediaFileKeys() {
        return savedMultimediaFileKeys;
    }


}
