package com.activetasks.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.activetasks.activetasks.R;
import com.activetasks.adapter.MemberAdapter;
import com.activetasks.adapter.TaskItemAdapter;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.GroupMember;
import com.activetasks.pojo.TaskItem;
import com.activetasks.util.Data;
import com.activetasks.util.GroupMemberReader;
import com.activetasks.util.JsonReaderSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskDataActivity extends ActionBarActivity {

    private ListView taskItemsListView;
    private TaskItemAdapter adapter;
    private List<TaskItem> taskItems = new ArrayList<>();
    private Integer taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_data);

        Intent intent = getIntent();

        taskId = intent.getIntExtra("groupId", 0);

        taskItemsListView = (ListView) findViewById(R.id.listViewMembers);

        adapter = new TaskItemAdapter(this, taskItems);

        taskItemsListView.setAdapter(adapter);

        new TaskItemReadTask().execute();
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

                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    taskItems.clear();
                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(TaskDataActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }

}
