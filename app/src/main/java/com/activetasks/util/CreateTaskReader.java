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
public class CreateTaskReader extends AsyncTask<String, Integer, String> {

    private JsonReaderSupport jsonReaderSource;
    private String url;

    public CreateTaskReader(JsonReaderSupport jsonReaderSource, String url){
        this.jsonReaderSource = jsonReaderSource;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder builder = new StringBuilder();

        String mName = params[0];
        String mDescription = params[1];
        String mtaskType = params[2];
        String mStartDate = params[3];
        String mEndDate = params[4];

        String result = null;

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("name", mName));
        data.add(new BasicNameValuePair("description", mDescription));
        data.add(new BasicNameValuePair("task_type", mtaskType));
        data.add(new BasicNameValuePair("start_date", mStartDate));
        data.add(new BasicNameValuePair("end_date", mEndDate));
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
