package com.activetasks.util;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ashutosh on 10/05/2015.
 * Read json data from url without sending any data
 */
public class SimpleDataReader extends AsyncTask<String, Integer, String> {

    private JsonReaderSupport jsonReaderSource;
    private String url;

    public SimpleDataReader(JsonReaderSupport jsonReaderSource, String url){
        this.jsonReaderSource = jsonReaderSource;
        this.url = url;
    }


    @Override
    protected String doInBackground(String... params) {

        StringBuilder builder = new StringBuilder();

        String result = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);

            try{

                HttpResponse response = httpClient.execute(get);
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
