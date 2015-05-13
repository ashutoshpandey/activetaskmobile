package com.activetasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.activetasks.activetasks.R;
import com.activetasks.adapter.MemberAdapter;
import com.activetasks.pojo.GroupMember;
import com.activetasks.util.GroupMemberReader;
import com.activetasks.util.GroupReader;
import com.activetasks.util.JsonReaderSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends Activity {

    private ListView membersListView;
    private MemberAdapter adapter;
    private List<GroupMember> groupMembers = new ArrayList<>();
    private Integer groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Intent intent = getIntent();

        groupId = intent.getIntExtra("groupId", 0);

        membersListView = (ListView) findViewById(R.id.listViewMembers);

        adapter = new MemberAdapter(this, groupMembers);

        membersListView.setAdapter(adapter);

        new GroupMemberReadTask().execute();
    }

    class GroupMemberReadTask implements JsonReaderSupport {

        private String url = "http://10.0.2.2/activetask/data-all-group-members/" + groupId;

        public GroupMemberReadTask() {
            execute();
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

                    groupMembers.clear();

                    JSONArray jArray = json.getJSONArray("groupMembers");

                    for(int i=0; i<jArray.length(); i++){
                        JSONObject json_data = jArray.getJSONObject(i);

                        GroupMember groupMember = new GroupMember();

                        groupMember.setId(json_data.getInt("id"));
                        groupMember.setName(json_data.getString("name"));

                        groupMembers.add(groupMember);
                    }

                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    groupMembers.clear();
                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(MemberActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }

}
