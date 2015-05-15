package com.activetasks.fragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activetasks.activetasks.R;
import com.activetasks.activity.LoginActivity;
import com.activetasks.activity.MemberActivity;
import com.activetasks.util.GroupReader;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.adapter.GroupAdapter;
import com.activetasks.pojo.Group;
import com.activetasks.util.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFragment extends Fragment {

    private List<Group> groups = new ArrayList<>();
    private GroupAdapter groupAdapter;
    private ListView groupListView;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        groupListView = (ListView)rootView.findViewById(R.id.listViewGroup);
        groupListView.setOnItemClickListener(new ListClickHandler());

        groupAdapter = new GroupAdapter(getActivity(), groups);

        groupListView.setAdapter(groupAdapter);

        new GroupReadTask().execute();

        return rootView;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class GroupReadTask implements JsonReaderSupport {

        private String url = "http://10.0.2.2/activetask/data-all-groups/" + Data.userId;

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

                    groupAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    groups.clear();
                    groupAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Group group = groups.get(position);

            int groupId = group.getId();

            Intent i = new Intent(getActivity(), MemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        }
    }
}

