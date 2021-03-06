package com.activetasks.util;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
public class TaskCommentReader extends AsyncTask<String, Integer, String> {

    private JsonReaderSupport jsonReaderSource;
    private String url;

    public TaskCommentReader(JsonReaderSupport jsonReaderSource, String url){
        this.jsonReaderSource = jsonReaderSource;
        this.url = url;
    }


    @Override
    protected String doInBackground(String... params) {

        StringBuilder builder = new StringBuilder();

        String message = params[0];
        String type = params[1];
        String taskItemId = params[2];
        String contactId = params[3];

        String result = null;

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("message", message));
        data.add(new BasicNameValuePair("type", type));
        data.add(new BasicNameValuePair("task_item_id", taskItemId));
        data.add(new BasicNameValuePair("contact_id", contactId));
        data.add(new BasicNameValuePair("user_id", Data.userId.toString()));

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(data));
            try{

                HttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                result = builder.toString();
                System.out.println("Result = " + result);
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
