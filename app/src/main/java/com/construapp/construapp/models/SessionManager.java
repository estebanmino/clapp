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

    public Boolean hasProjects() {
        return sharedPreferences.getBoolean(Constants.SP_HAS_PROJECTS, false);
    }

    public void setCurrentProject(String currentProject, String currentProjectName) {
        editor.putString(Constants.SP_ACTUAL_PROJECT, currentProject);
        editor.putString(Constants.SP_ACTUAL_PROJECT_NAME, currentProjectName);
        editor.apply();
    }

    public void setPermissionName(String permissionName) {
        editor.putString(Constants.SP_USER_PERMISSION_NAME, permissionName);
        editor.apply();
    }
}
