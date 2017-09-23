package com.construapp.construapp.models;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.AsyncTask;
import android.widget.EditText;

import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONObject;
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



    protected void onPreExecute() {
        //progressBar.setVisibility(View.VISIBLE);
        //responseView.setText("");
    }

    protected String doInBackground(String... str) {

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
            urlConnection.setConnectTimeout(150);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            String input = "{\"session\":{\"email\":\""+email+"\",\"password\":\""+pass+"\"}}";
            urlConnection.connect();

            //urlConnection.getOutputStream().write(input.getBytes("UTF-8"));


            OutputStream os = urlConnection.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            //writer.write(input);
            //writer.flush();
            //writer.close();
            //os.close();
            os.write(input.getBytes("UTF-8"));
            os.close();


            InputStream response=urlConnection.getInputStream();
            //InputStream _is;
            int respondecode = urlConnection.getResponseCode();
            //InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //String result2 = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            //JSONObject jsonObject = new JSONObject(result);


            //in.close();
            //urlConnection.disconnect();




            //if (urlConnection.getResponseCode() / 100 == 2) { // 2xx code means success
              //  _is = urlConnection.getInputStream();
            //} else {

//                _is = urlConnection.getErrorStream();

  //              String result2 = _is.toString();
    //            Log.i("Error != 2xx", result2);
      //      }





            BufferedReader br = new BufferedReader(new InputStreamReader((response)));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }


            if (urlConnection.getResponseCode() == 200) {
                // Success
                // Further processing here
                urlConnection.disconnect();
            }

            else {
                // Error handling code goes here
            }

            //out= output;
            return null;

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        }


    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        //progressBar.setVisibility(View.GONE);
        //Log.i("INFO", response);
        //responseView.setText(response);
    }
}
