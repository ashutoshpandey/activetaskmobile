package com.activetasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.pojo.Group;
import com.activetasks.util.GroupReader;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupSelectorActivity extends ActionBarActivity {

    private List<Group> groups = new ArrayList<>();
    ArrayAdapter<Group> adapter;

    private ListView groupView;

    private TextView tvGroupLabel;

    private Button btnGroupSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selector);

        groupView = (ListView)findViewById(R.id.lvGroupSelector);
        tvGroupLabel = (TextView)findViewById(R.id.tvGroupSelectorLabel);

        adapter = new ArrayAdapter<Group>(GroupSelectorActivity.this,
                android.R.layout.simple_list_item_1, groups);

        groupView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                String groupId = groups.get(position).getId().toString();

                Intent intent = new Intent();
                intent.putExtra("id", groupId);
                setResult(2, intent);
                finish();
            }

        });

        groupView.setAdapter(adapter);

        new GroupReadTask().execute();
    }

    /**
     * Represents an asynchronous call to server
     */
    class GroupReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-groups/" + Data.userId;

        public GroupReadTask() {
        }

        public void execute(){
            GroupReader reader = new GroupReader(this, url);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){

                    groups.clear();

                    JSONArray jArray = json.getJSONArray("groups");

                    for(int i=0; i<jArray.length(); i++){
                        JSONObject json_data = jArray.getJSONObject(i);

                        Group group = new Group();

                        group.setId(json_data.getInt("id"));
                        group.setName(json_data.getString("name"));

                        groups.add(group);
                    }

                    tvGroupLabel.setText("Listing your groups");

                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    tvGroupLabel.setText("No groups found");
                    groups.clear();
                    adapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(GroupSelectorActivity.this, LoginActivity.class);
                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }
}
