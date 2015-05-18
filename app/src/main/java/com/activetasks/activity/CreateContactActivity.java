package com.activetasks.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.util.CreateGroupReader;
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

    private TextView tvCreateContactMessage;
    private EditText etEmail;
    private Button btnFindContact;

    private TextView tvContactFirstName;
    private TextView tvContactLastName;
    private TextView tvContactGender;
    private TextView tvContactCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_contact);

        etEmail = (EditText)findViewById(R.id.etCreateGroupName);

        tvCreateContactMessage = (TextView) findViewById(R.id.tvCreateContactMessage);
        tvContactFirstName = (TextView) findViewById(R.id.tvContactFirstName);
        tvContactLastName = (TextView) findViewById(R.id.tvContactLastName);
        tvContactGender = (TextView) findViewById(R.id.tvContactGender);
        tvContactCountry = (TextView) findViewById(R.id.tvContactCountry);

        btnFindContact = (Button) findViewById(R.id.btnFindContact);

        btnFindContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();

                new FindContactTask(email).execute();
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

                tvCreateContactMessage.setText("Creating");
                FindContactReader reader = new FindContactReader(this, url);
                reader.execute(mEmail);
            }
            else
                tvCreateContactMessage.setText("Email cannot be blank");
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String message = json.getString("message");

                if(message.toLowerCase().contains("found")){
                    etEmail.setText("");
                    tvCreateContactMessage.setText("");

                    JSONObject contact = new JSONObject(json.getString("user"));

                    tvContactFirstName.setText(contact.getString("first_name"));
                    tvContactLastName.setText(contact.getString("last_name"));
                    tvContactCountry.setText(contact.getString("country"));
                    tvContactGender.setText(contact.getString("gender"));
                }
                else if(result.toLowerCase().contains("same"))
                    tvCreateContactMessage.setText("You found yourself :)");
                else if(result.toLowerCase().contains("n/a"))
                    tvCreateContactMessage.setText("No record found");
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateContactActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tvCreateContactMessage.setText("Error : " + ex.getMessage());
            }
        }
    }

}
