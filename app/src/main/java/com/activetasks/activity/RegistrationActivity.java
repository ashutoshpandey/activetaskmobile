package com.activetasks.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.RegisterReader;
import com.activetasks.util.RemoveGroupMemberReader;

import java.util.Set;
import java.util.regex.Pattern;

public class RegistrationActivity extends ActionBarActivity {

    private Button btnRegister;

    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private RadioButton rdMale;
    private RadioButton rdFemale;

    private TextView tvRegistrationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etEmail = (EditText)findViewById(R.id.etRegistrationEmail);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etPassword = (EditText)findViewById(R.id.etRegistrationPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etRegistrationConfirmPassword);

        rdMale = (RadioButton)findViewById(R.id.rdRegistrationMale);
        rdFemale = (RadioButton)findViewById(R.id.rdRegistrationFemale);

        btnRegister = (Button)findViewById(R.id.btnRegistration);

        tvRegistrationMessage = (TextView)findViewById(R.id.tvRegistrationMessage);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register(){

        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        String gender;
        if(rdMale.isChecked())
            gender = "male";
        else
            gender = "female";

        tvRegistrationMessage.setText("Checking...");

        if(email.length()==0 || !isValidEmail(email)){
            tvRegistrationMessage.setText("Invalid email!");
            return;
        }

        if(firstName.length()==0){
            tvRegistrationMessage.setText("Invalid first name!");
            return;
        }

        if(password.length()==0){
            tvRegistrationMessage.setText("Invalid password!");
            return;
        }
        else {
            if (!confirmPassword.equals(password)) {
                tvRegistrationMessage.setText("Password mismatch!");
                return;
            }
        }

        if(email.length()==0){
            tvRegistrationMessage.setText("Invalid email!");
            return;
        }

        new RegisterTask(email, firstName, lastName, password, gender).execute();
    }

    public boolean isValidEmail(String email){

        return email==null ? false : Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    class RegisterTask implements JsonReaderSupport {

        private String url = Data.server + "data-save-user";

        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private String gender;

        public RegisterTask(String email, String firstName, String lastName, String password, String gender) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.password = password;
            this.gender = gender;
        }

        public void execute(){

            RegisterReader reader = new RegisterReader(this, url);
            reader.execute(email, firstName, lastName, password, gender);
        }

        @Override
        public void onJsonReadComplete(String message) {

            try {
                if(message.toLowerCase().contains("created")) {
                    Intent i = new Intent(RegistrationActivity.this, RegisteredActivity.class);
                    startActivity(i);
                }
                else if(message.toLowerCase().contains("duplicate")) {
                    tvRegistrationMessage.setText("Email already taken");
                }
                else if(message.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                Log.d("Member ex", ex.getMessage());
            }
        }
    }
}
