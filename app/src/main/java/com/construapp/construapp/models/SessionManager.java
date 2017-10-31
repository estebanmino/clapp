package com.construapp.construapp.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;

/**
 * Created by ESTEBANFML on 30-10-2017.
 */

public class SessionManager {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor  editor;
    int PRIVATE_MODE = 0;
    JsonArray projectsArray = null;

    private static final String PREF_NAME = Constants.SP_CONSTRUAPP;

    Context context;

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void saveLogInData(String auth_token, String admin, String user_id, String company_id) {
        editor.putString(Constants.SP_TOKEN, auth_token);
        editor.putString(Constants.SP_USER, user_id);
        editor.putString(Constants.SP_COMPANY, company_id);
        editor.putString(Constants.SP_ADMIN, admin);
        editor.apply();
    }

    public Boolean isLoggedIn() {
        return sharedPreferences.contains(Constants.SP_TOKEN);
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.SP_TOKEN,"");
    }

    public String getUserAdmin() {
        return sharedPreferences.getString(Constants.SP_ADMIN,"");
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.SP_USER,"");
    }

    public void setProjects(JsonArray projects) {
        if (projects.size() != 0) {
            projectsArray = projects;
            editor.putBoolean(Constants.SP_HAS_PROJECTS, true);
            editor.putString(Constants.SP_PROJECTS, projects.toString());

        } else {
            editor.putBoolean(Constants.SP_HAS_PROJECTS, false);
        }
        editor.apply();
    }

    public String getProjects() {
        return sharedPreferences.getString(Constants.SP_PROJECTS, "");
    }

    public Boolean hasProjects() {
        return sharedPreferences.getBoolean(Constants.SP_HAS_PROJECTS, false);
    }

    public void setCurrentProject(String currentProjectId, String currentProjectName) {
        editor.putString(Constants.SP_ACTUAL_PROJECT, currentProjectId);
        editor.putString(Constants.SP_ACTUAL_PROJECT_NAME, currentProjectName);
        editor.putString(Constants.SP_USER_PERMISSION, getProjectPermission(currentProjectId));
        editor.apply();
    }

    public void setPermissionName(String permissionName) {
        editor.putString(Constants.SP_USER_PERMISSION_NAME, permissionName);
        editor.apply();
    }

    public String getActualUserPermission() {
        return sharedPreferences.getString(Constants.SP_USER_PERMISSION, "");
    }

    public String getActualProjectId() {
        return sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT, "");
    }

    public String getActualProjectName() {
        return sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT_NAME, "");
    }

    public void setProjectPermission(String projectId, String projectPermission) {
        editor.putString(Constants.SP_PERMISSION_PROJECT+projectId,  projectPermission);
        editor.apply();
    }

    public String getProjectPermission(String projectId) {
        if (projectId.equals("null")) {
            if (sharedPreferences.getString(Constants.SP_ADMIN, "").equals(Constants.S_ADMIN_ADMIN)) {
                return Constants.P_ADMIN;
            } else {
                return Constants.P_READ;
            }
        }
        return sharedPreferences.getString(Constants.SP_PERMISSION_PROJECT+projectId, "");
    }

    public void eraseSharedPreferences() {
        editor.clear();
        editor.apply();
    }

}
