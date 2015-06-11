package com.activetasks.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import activetasks.activetasks.R;
import com.activetasks.adapter.MemberAdapter;
import com.activetasks.pojo.GroupMember;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.RemoveGroupMemberReader;
import com.activetasks.util.SimpleDataReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MemberActivity extends ActionBarActivity {

    private ListView membersListView;
    private MemberAdapter adapter;
    private List<GroupMember> groupMembers = new ArrayList<>();
    private Integer groupId;
    private ActionBar actionBar;
    private TextView tvGroupMemberLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Intent intent = getIntent();
        groupId = intent.getIntExtra("groupId", 0);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        tvGroupMemberLabel = (TextView) findViewById(R.id.tvGroupMemberLabel);

        membersListView = (ListView) findViewById(R.id.listViewMembers);
        adapter = new MemberAdapter(this, groupMembers);
        membersListView.setAdapter(adapter);

        new GroupMemberReadTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_members, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_remove_group_member) {

            Set<Integer> groupMemberList = adapter.getSelectedGroupMembers();

            if(groupMemberList.size()>0)
                new RemoveGroupMemberTask(groupMemberList).execute();

            return true;
        }
        else if(id == R.id.logout){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class GroupMemberReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-group-members/" + groupId;

        public GroupMemberReadTask(){
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

                    groupMembers.clear();

                    JSONArray jArray = json.getJSONArray("groupMembers");

                    if(jArray.length()>0) {

                        tvGroupMemberLabel.setText("Listing group members");

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);

                            GroupMember groupMember = new GroupMember();

                            groupMember.setId(json_data.getInt("id"));
                            groupMember.setName(json_data.getString("name"));

                            groupMembers.add(groupMember);
                        }

                        adapter.notifyDataSetChanged();
                    }
                    else
                        tvGroupMemberLabel.setText("No members found in this group");
                }
                else if(message.toLowerCase().contains("empty")){
                    tvGroupMemberLabel.setText("No members found in this group");
                    groupMembers.clear();
                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(MemberActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Group member ex", ex.getMessage());
            }
        }
    }

    class RemoveGroupMemberTask implements JsonReaderSupport {

        private String url = "http://10.0.2.2/activetask/data-remove-group-members/" + Data.userId;
        Set<Integer> groupMemberList;

        public RemoveGroupMemberTask(Set<Integer> groupMemberList) {
            this.groupMemberList = groupMemberList;
        }

        public void execute(){

            Object[] strArray = groupMemberList.toArray();

            StringBuilder strBuilder = new StringBuilder();

            for (Object str : strArray) {
                strBuilder.append(str).append(",");
            }

            if(strBuilder.toString().endsWith(","))
                strBuilder.deleteCharAt(strBuilder.length() - 1);

            String strIds = strBuilder.toString();

            RemoveGroupMemberReader reader = new RemoveGroupMemberReader(this, url, strIds);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String message) {

            try {
                if(message.toLowerCase().contains("removed"))
                    new GroupMemberReadTask().execute();
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(MemberActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Member ex", ex.getMessage());
            }
        }
    }
}
