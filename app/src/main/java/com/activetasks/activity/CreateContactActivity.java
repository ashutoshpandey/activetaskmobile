package com.activetasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.util.AddContactReader;
import com.activetasks.util.Data;
import com.activetasks.util.FindContactReader;
import com.activetasks.util.JsonReaderSupport;

import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see com.activetasks.activity.util.SystemUiHider
 */
public class CreateContactActivity extends Activity {

    private TextView tvFindContactMessage;
    private EditText etEmail;
    private Button btnFindContact;
    private Button btnAddToContact;

    private String foundUserId;

    private TextView tvContactFirstName;
    private TextView tvContactLastName;
    private TextView tvContactGender;
    private TextView tvContactCountry;

    private View tableLayout;       // that contains found record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_contact);

        etEmail = (EditText)findViewById(R.id.etCreateGroupName);

        tvFindContactMessage = (TextView) findViewById(R.id.tvFindContactMessage);
        tvContactFirstName = (TextView) findViewById(R.id.tvContactFirstName);
        tvContactLastName = (TextView) findViewById(R.id.tvContactLastName);
        tvContactGender = (TextView) findViewById(R.id.tvContactGender);
        tvContactCountry = (TextView) findViewById(R.id.tvContactCountry);

        tableLayout = findViewById(R.id.tableContact);
        tableLayout.setVisibility(View.INVISIBLE);

        btnFindContact = (Button) findViewById(R.id.btnFindContact);
        btnFindContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();

                new FindContactTask(email).execute();
            }
        });

        btnAddToContact = (Button) findViewById(R.id.btnAddToContact);
        btnAddToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvFindContactMessage.setText("Adding...");
                new AddContactTask(foundUserId).execute();
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class FindContactTask implements JsonReaderSupport {

        private final String mEmail;
        private String url =  Data.server + "find-contact-by-email";

        public FindContactTask(String email) {
            mEmail = email;
        }

        public void execute(){

            if(mEmail.trim().length()>0) {

                tvFindContactMessage.setText("Finding...");
                FindContactReader reader = new FindContactReader(this, url);
                reader.execute(mEmail);
            }
            else
                tvFindContactMessage.setText("Email cannot be blank");
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){
                    etEmail.setText("");
                    tvFindContactMessage.setText("");

                    JSONObject contact = new JSONObject(json.getString("user"));

                    foundUserId = contact.getString("id");

                    tvContactFirstName.setText(contact.getString("first_name"));
                    tvContactLastName.setText(contact.getString("last_name"));
                    tvContactCountry.setText(contact.getString("country"));
                    tvContactGender.setText(contact.getString("gender"));
                    tableLayout.setVisibility(View.VISIBLE);
                    btnAddToContact.setVisibility(View.VISIBLE);
                }
                else if(message.toLowerCase().contains("exists")){
                    etEmail.setText("");
                    tvFindContactMessage.setText("This person is already in your contact");
                    btnAddToContact.setVisibility(View.INVISIBLE);

                    JSONObject contact = new JSONObject(json.getString("user"));

                    tvContactFirstName.setText(contact.getString("first_name"));
                    tvContactLastName.setText(contact.getString("last_name"));
                    tvContactCountry.setText(contact.getString("country"));
                    tvContactGender.setText(contact.getString("gender"));
                    tableLayout.setVisibility(View.VISIBLE);
                }
                else if(result.toLowerCase().contains("same")) {
                    tvFindContactMessage.setText("You found yourself :)");
                    tableLayout.setVisibility(View.INVISIBLE);
                }
                else if(result.toLowerCase().contains("n/a")) {
                    tvFindContactMessage.setText("No record found");
                    tableLayout.setVisibility(View.INVISIBLE);
                }
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateContactActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tableLayout.setVisibility(View.INVISIBLE);
                tvFindContactMessage.setText("Error : " + ex.getMessage());
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class AddContactTask implements JsonReaderSupport {

        private String url =  Data.server + "data-add-contact";
        private String foundUserId;

        public AddContactTask(String foundUserId) {
            this.foundUserId = foundUserId;
        }

        public void execute(){
            tvFindContactMessage.setText("");
            AddContactReader reader = new AddContactReader(this, url);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, foundUserId);
            else
                reader.execute(foundUserId);
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                if(result.toLowerCase().contains("done")){
                    tvFindContactMessage.setText("");
                    tvContactFirstName.setText("");
                    tvContactLastName.setText("");
                    tvContactCountry.setText("");
                    tvContactGender.setText("");

                    tableLayout.setVisibility(View.INVISIBLE);

                    tvFindContactMessage.setText("Contact added successfully");
                }
                else if(result.toLowerCase().contains("n/a"))
                    ;
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateContactActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tvFindContactMessage.setText("Error : " + ex.getMessage());
            }
        }
    }
}
