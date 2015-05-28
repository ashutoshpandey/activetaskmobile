package com.activetasks.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.adapter.TaskItemAdapter;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.TaskItem;
import com.activetasks.util.Data;
import com.activetasks.util.GroupMemberReader;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.SimpleDataReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TaskItemsActivity extends ActionBarActivity {

    private ListView taskItemsListView;
    private TextView tvActivityTaskData;

    private TaskItemAdapter adapter;
    private List<TaskItem> taskItems = new ArrayList<>();
    private Integer mTaskId;

    private Handler handler;

    private Timer taskTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_data);

        Intent intent = getIntent();

        mTaskId = intent.getIntExtra("taskId", 0);

        taskItemsListView = (ListView) findViewById(R.id.listViewTaskData);
        tvActivityTaskData = (TextView) findViewById(R.id.tvActivityTaskData);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_data, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_assign_task) {
            Intent i = new Intent(TaskItemsActivity.this, CreateTaskItemActivity.class);
            i.putExtra("taskId", mTaskId);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        private String url =  Data.server + "data-all-task-items/" + mTaskId;

        public TaskItemReadTask() {
        }

        public void execute(){

            SimpleDataReader reader = new SimpleDataReader(this, url);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
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
                        taskItem.setAssignedTo(json_data.getString("assigned_to"));
                        taskItem.setAssignedName(json_data.getString("assigned_name"));
                        taskItem.setDescription(json_data.getString("description"));
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
                    Intent i = new Intent(TaskItemsActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Task data ex", ex.getMessage());
            }
        }
    }

}
