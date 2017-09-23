package com.construapp.construapp.models;

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

        JSONObject object = new JSONObject();
        JSONArray Session = new JSONArray();



        try {

            object.put("email", email);
            object.put("password", pass);

        } catch (Exception ex) {

        }
        Session.put(object);
        JSONObject obj= new JSONObject();

        try {

            obj.put("Session",Session.toString());

        } catch (Exception ex) {

        }



        try {
            URL url = new URL("http://construapp-api.ing.puc.cl/sessions/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            String input = obj.toString();
            //urlConnection.connect();
            //urlConnection.getOutputStream().write(input.getBytes("UTF-8"));


            OutputStream os = urlConnection.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            //writer.write(input);
            //writer.flush();
            //writer.close();
            //os.close();
            os.write(input.getBytes("UTF-8"));
            os.close();


            //InputStream response=urlConnection.getInputStream();
            InputStream _is;
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            //String result2 = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            //JSONObject jsonObject = new JSONObject(result);


            in.close();
            urlConnection.disconnect();




            if (urlConnection.getResponseCode() / 100 == 2) { // 2xx code means success
                _is = urlConnection.getInputStream();
            } else {

                _is = urlConnection.getErrorStream();

                String result2 = _is.toString();
                Log.i("Error != 2xx", result2);
            }





            //BufferedReader br = new BufferedReader(new InputStreamReader(
              //      (response)));

            String output;
            //System.out.println("Output from Server .... \n");
            //while ((output = br.readLine()) != null) {
                //System.out.println(output);
            //}

            urlConnection.disconnect();

            if (urlConnection.getResponseCode() == 200) {
                // Success
                // Further processing here
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
