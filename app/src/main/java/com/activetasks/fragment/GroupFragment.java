package com.activetasks.fragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activetasks.activetasks.R;
import com.activetasks.util.GroupReader;
import com.activetasks.util.LoginReader;
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
    private ListView groupView;

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        groupView = (ListView)rootView.findViewById(R.id.listViewGroup);

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
            execute();
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
//                    JSONObject groupJson = json.getJSONObject("groups");


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

                }
                else if(message.toLowerCase().contains("invalid session")){
//                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Group ex", ex.getMessage());
            }
        }
    }

}

