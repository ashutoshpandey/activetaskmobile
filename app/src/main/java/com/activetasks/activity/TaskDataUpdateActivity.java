package com.activetasks.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import activetasks.activetasks.R;
import com.activetasks.adapter.TaskDataUpdateAdapter;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.TaskUpdateItem;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.TaskAssignedReader;
import com.activetasks.util.TaskCommentReader;

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

    private TaskDataUpdateAdapter adapter;
    private List<TaskUpdateItem> taskItems = new ArrayList<>();
    private Integer taskId;
    private Integer taskItemId;
    private Integer contactId;

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

        taskItemsListView.setOnItemClickListener(new ListClickHandler());

        tvActivityTaskData.setText("Loading...");

        adapter = new TaskDataUpdateAdapter(this, taskItems);

        taskItemsListView.setAdapter(adapter);

        handler = new Handler() {

            public void handleMessage(Message msg) {
                new AssignedTaskItemReadTask().execute();
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


    class AssignedTaskItemReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-assigned-task-items";

        public AssignedTaskItemReadTask() {
        }

        public void execute(){

            TaskAssignedReader reader = new TaskAssignedReader(this, url);
            reader.execute(taskId.toString());
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

                        TaskUpdateItem taskUpdateItem = new TaskUpdateItem();

                        taskUpdateItem.setId(json_data.getInt("id"));
                        taskUpdateItem.setDescription(json_data.getString("description"));
                        taskUpdateItem.setStartDate(DateHelper.formatStringDate(json_data.getString("start_date")));
                        taskUpdateItem.setEndDate(DateHelper.formatStringDate(json_data.getString("end_date")));
                        taskUpdateItem.setContactId(json_data.getInt("contact_id"));

                        taskItems.add(taskUpdateItem);
                    }

                    tvActivityTaskData.setText("Listing your assigned items");

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
                Log.d("Task data update ex", ex.getMessage());
            }
        }
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            TaskUpdateItem taskItem = taskItems.get(position);

            taskItemId = taskItem.getId();
            contactId = taskItem.getContactId();

            Intent i = new Intent(TaskDataUpdateActivity.this, TaskItemMessageActivity.class);
            startActivityForResult(i, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here

        if(data!=null){

            if(requestCode==1)
            {
                String message = data.getStringExtra("message");
                String type = data.getStringExtra("type");

                new UpdateTaskMessageTask(message, type, taskItemId.toString(), contactId.toString()).execute();
            }
            else if(requestCode==0){
            }
        }
    }

    class UpdateTaskMessageTask implements JsonReaderSupport {

        private String url =  Data.server + "data-task-update-message";

        private String message;
        private String type;
        private String taskItemId;
        private String contactId;

        public UpdateTaskMessageTask(String message, String type, String taskItemId, String contactId) {
            this.message = message;
            this.type = type;
            this.taskItemId = taskItemId;
            this.contactId = contactId;
        }

        public void execute(){
            TaskCommentReader reader = new TaskCommentReader(this, url);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{message, type, taskItemId, contactId});
            else
                reader.execute(new String[]{message, type, taskItemId, contactId});
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {

                if(result.toLowerCase().contains("saved")){

                }
                else if(result.toLowerCase().contains("empty")){

                }
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(TaskDataUpdateActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Task message update ex", ex.getMessage());
            }
        }
    }
}
