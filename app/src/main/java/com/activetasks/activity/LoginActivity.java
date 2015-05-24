package com.activetasks.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activetasks.util.LoginReader;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.Data;

import com.activetasks.activetasks.R;

import org.json.JSONObject;

public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mEmailLoginFormView;

    private Button mSignInButton;
    private Button btnRegister;

    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInButton = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        mPasswordView = (EditText) findViewById(R.id.password);

        mEmailView = (AutoCompleteTextView)findViewById(R.id.email);
        mPasswordView = (EditText)findViewById(R.id.password);

        tvMessage = (TextView)findViewById(R.id.tvMessage);

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuthTask = new UserLoginTask(email, password);
        mAuthTask.execute();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class UserLoginTask implements JsonReaderSupport{

        private final String mEmail;
        private final String mPassword;
        private String url = Data.server + "is-valid-user";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        public void execute(){

            tvMessage.setText("Checking");
            LoginReader reader = new LoginReader(this, url);
            reader.execute(new String[]{mEmail, mPassword});
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                JSONObject json = new JSONObject(result);

                String loginResult = json.getString("message");

                if (loginResult.toLowerCase().contains("correct")) {

                    Data.userId = Integer.parseInt(json.getString("id"));

                    tvMessage.setText("Authenticated");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if(loginResult.toLowerCase().contains("pending"))
                    tvMessage.setText("Account not activated");
                else if(loginResult.toLowerCase().contains("wrong"))
                    tvMessage.setText("Invalid username or password");

            }
            catch(Exception ex){
                tvMessage.setText("Error : " + ex.getMessage());
            }
        }
    }
}



