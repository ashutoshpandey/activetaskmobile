package com.activetasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.adapter.TaskItemAdapter;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.TaskItem;
import com.activetasks.util.Data;
import com.activetasks.util.GroupMemberReader;
import com.activetasks.util.JsonReaderSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This activity will be used to update the status of the task
 */
public class TaskDataUpdateActivity extends ActionBarActivity {

    private ListView taskItemsListView;
    private TextView tvActivityTaskData;

    private TaskItemAdapter adapter;
    private List<TaskItem> taskItems = new ArrayList<>();
    private Integer taskId;

    private Handler handler;

    private Timer taskTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_data_update);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("taskId", 0);

        taskItemsListView = (ListView) findViewById(R.id.listViewTaskDataUpdate);
        tvActivityTaskData = (TextView) findViewById(R.id.tvActivityTaskDataUpdate);

        tvActivityTaskData.setText("Loading...");

        adapter = new TaskItemAdapter(this, taskItems);

        taskItemsListView.setAdapter(adapter);

        handler = new Handler() {

            public void handleMessage(Message msg) {
                new TaskItemReadTask().execute();
            }
        };

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


    class TaskItemReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-task-items/" + taskId;

        public TaskItemReadTask() {
        }

        public void execute(){

            GroupMemberReader reader = new GroupMemberReader(this, url);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){

                    taskItems.clear();

                    JSONArray jArray = json.getJSONArray("taskItems");

                    for(int i=0; i<jArray.length(); i++){
                        JSONObject json_data = jArray.getJSONObject(i);

                        TaskItem taskItem = new TaskItem();

                        taskItem.setId(json_data.getInt("id"));
                        taskItem.setContent(json_data.getString("content"));
                        taskItem.setStartDate(DateHelper.formatStringDate(json_data.getString("start_date")));
                        taskItem.setEndDate(DateHelper.formatStringDate(json_data.getString("end_date")));

                        taskItems.add(taskItem);
                    }

                    tvActivityTaskData.setText("Listing task items");

                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    tvActivityTaskData.setText("No task items added");
                    taskItems.clear();
                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(TaskDataUpdateActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }

}
