package com.activetasks.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.adapter.ContactAdapter;
import com.activetasks.adapter.ContactSelectorAdapter;
import com.activetasks.pojo.Contact;
import com.activetasks.util.ContactReader;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.SimpleDataReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContactSelectorActivity extends ActionBarActivity {

    private List<Contact> contacts = new ArrayList<>();
    private ContactSelectorAdapter contactAdapter;

    private ListView contactView;

    private TextView tvContactLabel;

    private Button btnContactSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_selector);

        contactView = (ListView)findViewById(R.id.lvContactSelector);
        tvContactLabel = (TextView)findViewById(R.id.tvContactSelectorLabel);
        btnContactSelector = (Button) findViewById(R.id.btnChooseContact);

        btnContactSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<Integer> selectedContacts = contactAdapter.getSelectedContacts();

                if(selectedContacts!=null && selectedContacts.size()>0) {
                    Object[] strArray = selectedContacts.toArray();

                    StringBuilder strBuilder = new StringBuilder();

                    for (Object str : strArray) {
                        strBuilder.append(str).append(",");
                    }

                    if (strBuilder.toString().endsWith(","))
                        strBuilder.deleteCharAt(strBuilder.length() - 1);

                    String ids = strBuilder.toString();
Log.d("selected contacts = ", ids);
                    Intent intent = new Intent();
                    intent.putExtra("ids", ids);
                    setResult(1, intent);
                    finish();
                }
            }
        });

        contactAdapter = new ContactSelectorAdapter(ContactSelectorActivity.this, contacts);

        contactView.setAdapter(contactAdapter);

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
                    Intent i = new Intent(ContactSelectorActivity.this, LoginActivity.class);
                    startActivity(i);
                }

            }
            catch(Exception ex){
                Log.d("Contact ex", ex.getMessage());
            }
        }
    }
}
