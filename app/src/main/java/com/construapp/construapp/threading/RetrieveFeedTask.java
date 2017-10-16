package com.construapp.construapp.threading;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;

import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import android.util.Log;
import android.widget.Toast;

import com.construapp.construapp.LoginActivity;
import com.construapp.construapp.models.Lesson;

import java.io.BufferedInputStream;
import java.util.ArrayList;

/**
 * Created by jose on 22-09-17.
 */

public class RetrieveFeedTask extends AsyncTask<String, Integer, String> {

    private Exception exception;
    public String out;
    //type values: login, send-lesson
    private String type;

    public RetrieveFeedTask(String type) {
        this.type = type;
    }


    protected void onPreExecute() {

    }

    protected String doInBackground(String... str) {

        if(type == "fetch-lessons")
        {
            String company_id = str[0];
            try {
                String url_string = "http://construapp-api.ing.puc.cl/companies/"+company_id+"/lessons";
                URL url = new URL(url_string);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setDoInput(false);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                //String input = "{\"session\":{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}}";
                urlConnection.connect();

                int responsecode = urlConnection.getResponseCode();
                if (responsecode != 200) {
                    urlConnection.disconnect();
                    out = "error";
                    return out;
                } else {}

                InputStream response = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader((response)));

                String output = "";
                String aux = "";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    aux += output;
                }
                if (urlConnection.getResponseCode() == 200) {
                    urlConnection.disconnect();
                }
                return aux;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

        }

        else
        {
            return "not implemented";
        }
    }

    protected void onPostExecute(String response) {
        if(response == "error") {}

    }
}

