package com.activetasks.activity;

import com.activetasks.activity.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.util.ContactSelectorReader;
import com.activetasks.util.CreateTaskReader;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class CreateTaskActivity extends Activity {

    private TextView tvCreateTaskMessage;

    private EditText etName;
    private EditText etDescription;

    private Button btnCreateTask;

    private RadioButton rdTaskNormal;
    private RadioButton rdTaskTimed;

    private String mSelectedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task);

        etName = (EditText)findViewById(R.id.etCreateTaskName);
        etDescription = (EditText)findViewById(R.id.etCreateTaskDescription);

        tvCreateTaskMessage = (TextView) findViewById(R.id.tvCreateTaskMessage);

        btnCreateTask = (Button) findViewById(R.id.btnCreateTask);

        rdTaskNormal = (RadioButton) findViewById(R.id.rdCreateTaskNormal);
        rdTaskTimed = (RadioButton) findViewById(R.id.rdCreateTaskTimed);

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                String taskType = null;
                if(rdTaskNormal.isChecked())
                    taskType = "normal";
                else if(rdTaskTimed.isChecked())
                    taskType = "timed";
                else{
                    tvCreateTaskMessage.setText("Please choose task type");
                    return;
                }

                String startDate = etName.getText().toString();
                String endDate = etName.getText().toString();

                new CreateTask(name, description, taskType, startDate, endDate).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here

        if(data!=null){

            if(requestCode==1)
            {
                mSelectedIds = data.getStringExtra("ids");
            }
            else if(requestCode==2){
                mSelectedIds = data.getStringExtra("id");
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    class CreateTask implements JsonReaderSupport {

        private final String mName;
        private final String mDescription;
        private final String mTaskType;
        private final String mStartDate;
        private final String mEndDate;
        private String url =  Data.server + "data-save-task";

        public CreateTask(String name, String description, String taskType, String startDate, String endDate) {
            mName = name;
            mDescription = description;
            mTaskType = taskType;
            mStartDate = startDate;
            mEndDate = endDate;
        }

        public void execute(){

            boolean valid = true;

            if(mName.trim().length()==0)
                valid = false;

            if(valid){
                tvCreateTaskMessage.setText("Creating");

                CreateTaskReader reader = new CreateTaskReader(this, url);
                reader.execute(new String[]{mName, mDescription, mTaskType, mStartDate, mEndDate});
            }
            else
                tvCreateTaskMessage.setText("Form is incomplete");
        }

        @Override
        public void onJsonReadComplete(String result) {

            try {
                if (result.toLowerCase().contains("created")) {
                    etName.setText("");
                    etDescription.setText("");

                    rdTaskTimed.setChecked(false);
                    rdTaskNormal.setChecked(false);

                    etName.requestFocus();

                    tvCreateTaskMessage.setText("Task created");
                }
                else if(result.toLowerCase().contains("duplicate"))
                    tvCreateTaskMessage.setText("Duplicate name");
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateTaskActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tvCreateTaskMessage.setText("Error : " + ex.getMessage());
            }
        }
    }
}
