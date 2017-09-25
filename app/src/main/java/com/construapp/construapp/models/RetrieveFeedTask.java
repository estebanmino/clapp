package com.construapp.construapp.models;

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

import java.io.BufferedInputStream;

/**
 * Created by jose on 22-09-17.
 */

public class RetrieveFeedTask extends AsyncTask<String, Integer, String> {

    private Exception exception;
    public String out;
    private OnTaskCompleted listener;
    //type values: login, send-lesson
    private String type;

    public RetrieveFeedTask(String type) {
        this.type = type;
    }


    protected void onPreExecute() {
        //progressBar.setVisibility(View.VISIBLE);
        //responseView.setText("");
    }

    protected String doInBackground(String... str) {

        if(type == "login")
        {


            boolean result = false;
            String email = str[0];
            String pass = str[1];

            // JSONObject object = new JSONObject();
            //JSONArray Session = new JSONArray();

            try {
                URL url = new URL("http://construapp-api.ing.puc.cl/sessions");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(1500);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                String input = "{\"session\":{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}}";
                urlConnection.connect();


                OutputStream os = urlConnection.getOutputStream();
                os.write(input.getBytes("UTF-8"));
                os.close();


                int responsecode = urlConnection.getResponseCode();

                if (responsecode != 200) {
                    // Success
                    // Further processing here
                    urlConnection.disconnect();
                    out = "error";
                    return out;
                } else {
                    //continue
                }


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
                    // Success
                    // Further processing here
                    urlConnection.disconnect();

                }


                JSONObject object = (JSONObject) new JSONTokener(aux).nextValue();
                String query = object.getString("auth_token");

                out = query;

                return out;

            } catch (Exception e) {

                e.printStackTrace();
                return "error";

            }
        }
        else if(type == "send-lesson")
        {
            String token = str[0];
            String name = str[1];
            String description = str[2];

            try {
                //URL url = new URL("http://construapp-api.ing.puc.cl/lessons");
                //HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setDoOutput(true);
                //urlConnection.setDoInput(true);
                //urlConnection.setConnectTimeout(1500);
                //urlConnection.setRequestMethod("POST");
                //urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                String input = "{\"lesson\":{\"token\":\"" + token + "\",\"nombre\":\"" +name + "\",\"descripcion\":\"" + description + "\"}}";
                //urlConnection.connect();

                //OutputStream os = urlConnection.getOutputStream();
                // os.write(input.getBytes("UTF-8"));
                //os.close();


                //int responsecode = urlConnection.getResponseCode();

                /*if (responsecode != 200) {
                    // Success
                    // Further processing here
                    urlConnection.disconnect();
                    out = "error";
                    return out;
                }
                else
                {
                    //continue
                }*/


                /*InputStream response = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader((response)));

                String output = "";
                String aux = "";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    aux += output;
                }

                if (urlConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    urlConnection.disconnect();

                }


                JSONObject object = (JSONObject) new JSONTokener(aux).nextValue();
                String query = object.getString("auth_token");

                out = query;

                return out;*/

                return "input";

            }
            catch (Exception e) {

                e.printStackTrace();
                return "error";

            }

        }
        else
        {
            return "not implemented";
        }


    }

    protected void onPostExecute(String response) {
        if(response == "error") {
            //response = "THERE WAS AN ERROR";
        }
        else
        {

        }

    }
}

