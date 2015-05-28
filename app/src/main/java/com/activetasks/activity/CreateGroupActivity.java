package com.activetasks.activity;

import com.activetasks.activity.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.util.CreateGroupReader;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;
import com.activetasks.util.LoginReader;

import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class CreateGroupActivity extends Activity {

    private TextView tvCreateGroupMessage;
    private EditText etName;
    private Button btnCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_group);

        etName = (EditText)findViewById(R.id.etCreateGroupName);
        tvCreateGroupMessage = (TextView) findViewById(R.id.tvCreateGroupMessage);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();

                new CreateGroupTask(name).execute();
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class CreateGroupTask implements JsonReaderSupport {

        private final String mName;
        private String url =  Data.server + "data-save-group";

        public CreateGroupTask(String name) {
            mName = name;
        }

        public void execute(){

            if(mName.trim().length()>0) {
                tvCreateGroupMessage.setText("Creating");

                CreateGroupReader reader = new CreateGroupReader(this, url);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mName);
                else
                    reader.execute(mName);
            }
            else
                tvCreateGroupMessage.setText("Name cannot be blank");
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                if (result.toLowerCase().contains("created")) {
                    etName.setText("");
                    tvCreateGroupMessage.setText("Group created");
                }
                else if(result.toLowerCase().contains("duplicate"))
                    tvCreateGroupMessage.setText("Duplicate name");
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateGroupActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tvCreateGroupMessage.setText("Error : " + ex.getMessage());
            }
        }
    }

}
