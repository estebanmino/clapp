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

        if(type == "login")
        {

            String email = str[0];
            String pass = str[1];

            try {
                URL url = new URL("http://construapp-api.ing.puc.cl/sessions");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                String input = "{\"session\":{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}}";
                JSONObject obj = new JSONObject(input);
                input = obj.toString();
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
                    Log.i("CODE",String.valueOf(urlConnection.getResponseCode()));
                    // Success
                    // Further processing here
                    urlConnection.disconnect();

                }


                JSONObject object = (JSONObject) new JSONTokener(aux).nextValue();
                String t = object.getString("auth_token");
                String id = object.getString("id");
                JSONObject company = object.getJSONObject("company");
                //String company_id = object.getString("company_id");
                String company_id = company.getString("id");
                String query = t+";"+id+";"+company_id;

                out = query;

                return out;

            } catch (Exception e) {

                e.printStackTrace();
                return "error";

            }
        }
        else if(type == "send-lesson")
        {

            String lesson_name = str[0];
            String lesson_summary = str[1];
            String lesson_motivation = str[2];
            String lesson_learning = str[3];
            String token = str[4];
            int user_id = Integer.parseInt(str[5]);
            int company_id = Integer.parseInt(str[6]);
            int project_id = Integer.parseInt(str[7]);

            try {

                String url_string = "http://construapp-api.ing.puc.cl/companies/"+company_id+"/lessons";
                URL url = new URL(url_string);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                String input = "{\"lesson\":{\"name\":\"" + lesson_name + "\",\"summary\":\"" + lesson_summary + "\",\"motivation\":\""+lesson_motivation + "\",\"learning\":\""+lesson_learning + "\",\"user_id\":\""+user_id + "\",\"company_id\":\""+company_id + "\",\"project_id\":\"" + project_id + "\"}}";
                JSONObject N = new JSONObject(input);
                input = N.toString();
                urlConnection.connect();

                OutputStream os = urlConnection.getOutputStream();
                os.write(input.getBytes("UTF-8"));
                os.close();

                int responsecode = urlConnection.getResponseCode();

                if (responsecode != 201) {
                    // Success
                    // Further processing here
                    urlConnection.disconnect();
                    out = "error";
                    return out;
                }
                else
                {
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
                String query = object.getString("id");

                out = query;

                return out;



            }
            catch (Exception e) {

                e.printStackTrace();
                return e.getMessage();

            }

        }

        else if(type == "fetch-lessons")
        {
            String company_id = str[0];
            Log.i("company_id",company_id);
            try {
                String url_string = "http://construapp-api.ing.puc.cl/companies/"+company_id+"/lessons";
                URL url = new URL(url_string);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setDoOutput(true);
                //urlConnection.setDoInput(false);
                urlConnection.setConnectTimeout(1500);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                //String input = "{\"session\":{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}}";
                urlConnection.connect();


                //OutputStream os = urlConnection.getOutputStream();
                //os.write(input.getBytes("UTF-8"));
                //os.close();


                int responsecode = urlConnection.getResponseCode();

                Log.i("responsecode",String.valueOf(responsecode));

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


                //JSONObject object = (JSONObject) new JSONTokener(aux).nextValue();
                //String t = object.getString("auth_token");
                //String id = object.getString("id");
                //JSONObject company = (JSONObject) object.getJSONObject("company");
                //String company_id = company.getString("id");
                String query = aux;
                Log.i("outputLessonsthread",aux);
                return aux;

                //out = query;

                //return out;

            } catch (Exception e) {

                e.printStackTrace();
                return e.getMessage();

            }

        }
        else if(type == "fetch-s3")
        {
            Log.i("S3","estoy en S3");
            String company_id = str[0];
            String lesson_id = str[1];
            String paths_string = str[2];
            String[] paths_array = paths_string.split(";");
            Log.i("company_id",company_id);
            try {
                Log.i("LESSON_ID",lesson_id);
                String url_string = "http://construapp-api.ing.puc.cl/companies/"+company_id+"/lessons/"+lesson_id+"/save_key";
                URL url = new URL(url_string);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //TODO utilizar JSONObject para armar String y no manual
                JSONArray routes_array = new JSONArray();

                JSONObject paths = new JSONObject();

                for(int i=0;i<paths_array.length;i++)
                {
                    routes_array.put(paths_array[i]);
                }

                paths.put("array_file_path",routes_array);
                String rutas_string = paths.toString();
                Log.i("jsontoprint",rutas_string);
                urlConnection.connect();


                OutputStream os = urlConnection.getOutputStream();
                os.write(rutas_string.getBytes("UTF-8"));
                os.close();


                int responsecode = urlConnection.getResponseCode();

                Log.i("responsecode",String.valueOf(responsecode));

                if (responsecode != 200) {
                    // Success
                    // Further processing here
                    urlConnection.disconnect();
                    out = "error: "+responsecode;
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


                //JSONObject object = (JSONObject) new JSONTokener(aux).nextValue();
                //String t = object.getString("auth_token");
                //String id = object.getString("id");
                //JSONObject company = (JSONObject) object.getJSONObject("company");
                //String company_id = company.getString("id");
                String query = aux;
                Log.i("outputLessonsthread",aux);
                aux = "OK";
                return aux;

                //out = query;

                //return out;

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
        if(response == "error") {
            //response = "THERE WAS AN ERROR";
        }
        else
        {

        }

    }
}

