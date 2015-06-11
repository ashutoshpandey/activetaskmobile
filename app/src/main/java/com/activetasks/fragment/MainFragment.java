package com.activetasks.fragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import activetasks.activetasks.R;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.PendingTasksCountReader;

import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private TextView tvTasksRemaining;
    private TextView tvGroupsCreated;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        tvTasksRemaining = (TextView) rootView.findViewById(R.id.tvTasksRemaining);
        tvGroupsCreated = (TextView) rootView.findViewById(R.id.tvGroupsCreated);

        new RemainingTaskReadTask().execute();

        new GroupCountReadTask().execute();

        return rootView;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class RemainingTaskReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-pending-tasks-count/" + Data.userId;

        public RemainingTaskReadTask() {
        }

        public void execute(){
            PendingTasksCountReader reader = new PendingTasksCountReader(this, url);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String count = json.getString("count");

                tvTasksRemaining.setText("Tasks remaining = " + count);
            }
            catch(Exception ex){
                Log.d("Remaining task ex", ex.getMessage());
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GroupCountReadTask implements JsonReaderSupport {

        private String url = Data.server + "data-all-groups-count/" + Data.userId;

        public GroupCountReadTask() {
            execute();
        }

        public void execute(){
            PendingTasksCountReader reader = new PendingTasksCountReader(this, url);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String count = json.getString("count");

                tvGroupsCreated.setText("Groups created = " + count);
            }
            catch(Exception ex){
                Log.d("Group count ex", ex.getMessage());
            }
        }
    }
}
