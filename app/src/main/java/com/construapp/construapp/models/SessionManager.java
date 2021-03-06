package com.construapp.construapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public String getCompany() {
        return sharedPreferences.getString(Constants.SP_COMPANY,"");
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

    public void setActualProject(String currentProjectId, String currentProjectName) {
        editor.putString(Constants.SP_ACTUAL_PROJECT, currentProjectId);
        editor.putString(Constants.SP_ACTUAL_PROJECT_NAME, currentProjectName);
        editor.putString(Constants.SP_USER_PERMISSION, getProjectPermission());
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

    public String getProjectPermission() {
        String projectId = sharedPreferences.getString(Constants.SP_ACTUAL_PROJECT, "");
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
        String FCM = sharedPreferences.getString(Constants.FCM_TOKEN,"");
        editor.clear();
        editor.apply();
        editor.putString(Constants.FCM_TOKEN,FCM);
        editor.apply();
    }

    public String getFCMToken() {

        return sharedPreferences.getString(Constants.FCM_TOKEN, "");

    }
    public void setFCMToken(String fcmToken) {
        editor.putString(Constants.FCM_TOKEN,  fcmToken);
        editor.apply();
    }

    public void setSection(String section) {
        editor.putString(Constants.SP_ACTUAL_SECTION, section);
        editor.apply();
    }

    public String getSection() {
        return sharedPreferences.getString(Constants.SP_ACTUAL_SECTION, "");
    }

    public void clearSection(String section)
    {
        editor.remove(section);
        editor.commit();
    }


    public String getHasPendingValidations() {
        if (!sharedPreferences.contains(Constants.SP_HAS_PENDING_VALIDATIONS)){
            return "false";
        } else {
            return sharedPreferences.getString(Constants.SP_HAS_PENDING_VALIDATIONS, "");
        }
    }

    public void setHasPendingValidations(String pendingValidations) {
        editor.putString(Constants.SP_HAS_PENDING_VALIDATIONS,  pendingValidations);
        editor.apply();
    }

    public void setPendingValidations(String pendingValidations) {
        editor.putString(Constants.SP_PENDING_VALIDATIONS,  pendingValidations);
        Log.i("PENDINGVALIDATIONS1",pendingValidations);

        editor.apply();
    }

    public ArrayList<String> getPendingValidations() {
        JSONArray jsonLessons;
        ArrayList<String> idsList = new ArrayList<>();
        try {
            jsonLessons = new JSONArray(sharedPreferences.getString(Constants.SP_PENDING_VALIDATIONS, ""));
            Log.i("PENDINGVALIDATIONS",jsonLessons.toString());

            for (int i = 0; i < jsonLessons.length(); i++) {
                JSONObject object = (JSONObject) jsonLessons.get(i);
                Log.i("LESSONVALIDATE",object.toString());
                idsList.add(object.get("id").toString());
            }
        } catch (Exception e) {}
        return idsList;
    }

    public ArrayList<String> getFavouriteLessonsIds() {
        JSONArray jsonLessons;
        ArrayList<String> idsList = new ArrayList<>();
        try {
            jsonLessons = new JSONArray(sharedPreferences.getString(Constants.SP_FAVOURITE_LESSONS, ""));
            Log.i("SP_FAVOURITE_LESSONS",jsonLessons.toString());

            for (int i = 0; i < jsonLessons.length(); i++) {
                JSONObject object = (JSONObject) jsonLessons.get(i);
                Log.i("LESSONVALIDATE",object.toString());
                idsList.add(object.get("id").toString());
            }
        } catch (Exception e) {}
        return idsList;
    }

    public void setFavouriteLessons(String favouriteLessons) {
        editor.putString(Constants.SP_FAVOURITE_LESSONS,  favouriteLessons);
        Log.i("SP_FAVOURITE_LESSONS",favouriteLessons);

        editor.apply();
    }

    public void setThreadId(String threadId)
    {
        editor.putString(Constants.SP_THREAD_ID,  threadId);
        editor.apply();
    }

    public String getThreadId()
    {
        return sharedPreferences.getString(Constants.SP_THREAD_ID, "");
    }

    public void clearThreadId()
    {
        editor.remove(Constants.SP_THREAD_ID);
        editor.commit();
    }

    public void setDisciplines(String disciplines) {
        editor.putString(Constants.SP_DISCIPLINES,  disciplines);
        editor.apply();
    }
    public void setClassifications(String classifications) {

        Log.i("SETCLASSIFICATIONS",classifications);
        editor.putString(Constants.SP_CLASSIFICATIONS,  classifications);
        editor.apply();
    }
    public void setDepartments(String departments) {
        editor.putString(Constants.SP_DEPARTMENTS,  departments);
        editor.apply();
    }

    public String[] getDisciplines() {
        String disciplines =  sharedPreferences.getString(Constants.SP_DISCIPLINES, "");
        if (!disciplines.isEmpty()) {
            return disciplines.substring(1).split("/");
        } else {
            return new String[0];
        }
    }
    public String[] getClassifications() {
        String classifications = sharedPreferences.getString(Constants.SP_CLASSIFICATIONS, "");
        Log.i("CLASSIFICATIONS",classifications);
        if (!classifications.isEmpty()) {
            return classifications.substring(1).split("/");
        } else {
            return new String[0];
        }
    }
    public String[] getDepartments() {
        String departments = sharedPreferences.getString(Constants.SP_DEPARTMENTS, "");
        if (!departments.isEmpty()) {
            return departments.substring(1).split("/");
        } else {
            return new String[0];
        }
    }
}
