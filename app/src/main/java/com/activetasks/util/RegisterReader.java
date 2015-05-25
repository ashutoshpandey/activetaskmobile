package com.activetasks.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashutosh on 10/05/2015.
 */
public class RegisterReader extends AsyncTask<String, Integer, String> {

    private JsonReaderSupport jsonReaderSource;
    private String url;

    public RegisterReader(JsonReaderSupport jsonReaderSource, String url){
        this.jsonReaderSource = jsonReaderSource;
        this.url = url;
    }


    @Override
    protected String doInBackground(String... params) {

        StringBuilder builder = new StringBuilder();

        String email = params[0];
        String firstName = params[1];
        String lastName = params[2];
        String password = params[3];
        String gender = params[4];

        String result = null;
        Log.d("url registration = ", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("email", email));
            data.add(new BasicNameValuePair("first_name", firstName));
            data.add(new BasicNameValuePair("last_name", lastName));
            data.add(new BasicNameValuePair("password", password));
            data.add(new BasicNameValuePair("gender", gender));
            data.add(new BasicNameValuePair("country", ""));
            data.add(new BasicNameValuePair("display_name", ""));

            try{
                post.setEntity(new UrlEncodedFormEntity(data));

                HttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                result = builder.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(final String result) {
        jsonReaderSource.onJsonReadComplete(result);
    }

}
