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
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.activity.LoginActivity;
import com.activetasks.adapter.ContactAdapter;
import com.activetasks.pojo.Contact;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.ContactReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactFragment extends Fragment {

    private ContactAdapter contactAdapter;
    private ListView contactView;
    private TextView tvContactLabel;

    private List<Contact> contacts = new ArrayList<>();

    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        contactView = (ListView)rootView.findViewById(R.id.listViewContacts);
        tvContactLabel = (TextView)rootView.findViewById(R.id.tvContactLabel);

        contactView.setOnItemClickListener(new ListClickHandler());

        contactAdapter = new ContactAdapter(getActivity(), contacts);

        contactView.setAdapter(contactAdapter);

        loadContacts();

        return rootView;
    }

    public void loadContacts(){
        new ContactReadTask().execute();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class ContactReadTask implements JsonReaderSupport {

        private String url =  Data.server + "data-all-contacts/" + Data.userId;

        public ContactReadTask() {
        }

        public void execute(){
            ContactReader reader = new ContactReader(this, url);
            reader.execute();
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){

                    contacts.clear();

                    JSONArray jArray = json.getJSONArray("contacts");

                    for(int i=0; i<jArray.length(); i++){
                        JSONObject json_data = jArray.getJSONObject(i);

                        Contact contact = new Contact();

                        contact.setId(json_data.getInt("id"));
                        contact.setName(json_data.getString("name"));

                        contacts.add(contact);
                    }

                    tvContactLabel.setText("Listing your contacts");

                    contactAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("empty")){
                    tvContactLabel.setText("No contacts found");

                    contacts.clear();
                    contactAdapter.notifyDataSetChanged();
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Contact ex", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public class ListClickHandler implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            Contact task = contacts.get(position);

            int peopleId = task.getId();

//            Intent i = new Intent(getActivity(), TaskDataActivity.class);
//            i.putExtra("taskId", taskId);
//            startActivity(i);
        }
    }
}
