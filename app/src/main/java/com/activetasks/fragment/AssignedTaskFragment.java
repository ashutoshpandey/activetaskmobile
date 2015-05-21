package com.activetasks.fragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.activity.LoginActivity;
import com.activetasks.activity.TaskDataActivity;
import com.activetasks.activity.TaskDataUpdateActivity;
import com.activetasks.adapter.TaskAdapter;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.Task;
import com.activetasks.util.Data;
import com.activetasks.util.GroupReader;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.SimpleDataReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class AssignedTaskFragment extends Fragment {

    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private ListView taskListView;
    private TextView tvTaskList;

    private Handler handler;

    private Timer taskTimer;

    public AssignedTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_assigned_tasks, container, false);

        taskListView = (ListView)rootView.findViewById(R.id.listViewAssignedTask);
        tvTaskList = (TextView)rootView.findViewById(R.id.tvAssignedTaskList);

        taskListView.setOnItemClickListener(new ListClickHandler());

        taskAdapter = new TaskAdapter(getActivity(), tasks);

        taskListView.setAdapter(taskAdapter);

        handler = new Handler() {

            public void handleMessage(Message msg) {
                new AssignedTaskReadTask().execute();
            }
        };

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        startTaskTimer();
    }

    public void startTaskTimer(){
        taskTimer = new Timer();
        taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 5000, 30000);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class AssignedTaskReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-assigned-tasks/" + Data.userId;

        public AssignedTaskReadTask() {
        }

        public void execute(){
            SimpleDataReader reader = new SimpleDataReader(this, url);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){

                    tasks.clear();

                    JSONArray jArray = json.getJSONArray("tasks");

                    for(int i=0; i<jArray.length(); i++){
                        JSONObject json_data = jArray.getJSONObject(i);

                        Task task = new Task();

                        task.setId(json_data.getInt("id"));
                        task.setName(json_data.getString("name"));
                        task.setStartDate(DateHelper.formatStringDate(json_data.getString("start_date")));
                        task.setEndDate(DateHelper.formatStringDate(json_data.getString("end_date")));
                        task.setDescription(json_data.getString("description"));

                        tasks.add(task);
                    }

                    tvTaskList.setText(tasks.size() + " tasks pending");

                    taskAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    tvTaskList.setText("No active tasks");
                    tasks.clear();
                    taskAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Assigned ex", ex.getMessage());
            }
        }
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Task task = tasks.get(position);

            int taskId = task.getId();

            Intent i = new Intent(getActivity(), TaskDataUpdateActivity.class);
            i.putExtra("taskId", taskId);
            startActivity(i);
        }
    }
}
