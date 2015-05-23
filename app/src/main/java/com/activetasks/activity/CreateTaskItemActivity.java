package com.activetasks.activity;

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
import com.activetasks.util.CreateTaskItemReader;
import com.activetasks.util.CreateTaskReader;
import com.activetasks.util.Data;
import com.activetasks.util.JsonReaderSupport;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see com.activetasks.activity.util.SystemUiHider
 */
public class CreateTaskItemActivity extends Activity {

    private TextView tvCreateTaskMessage;

    private EditText etName;
    private EditText etDescription;

    private Button btnCreateTaskItem;

    private RadioButton rdContact;
    private RadioButton rdGroup;

    private Integer mTaskId;
    private String mSelectedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_task_entry);

        Intent intent = getIntent();
        mTaskId = intent.getIntExtra("taskId", 0);

        etName = (EditText)findViewById(R.id.etCreateTaskName);
        etDescription = (EditText)findViewById(R.id.etCreateTaskDescription);

        tvCreateTaskMessage = (TextView) findViewById(R.id.tvCreateTaskMessage);

        btnCreateTaskItem = (Button) findViewById(R.id.btnCreateTask);

        rdContact = (RadioButton) findViewById(R.id.rdCreateTaskAssignToContact);
        rdGroup = (RadioButton) findViewById(R.id.rdCreateTaskAssignToGroup);

        rdContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateTaskItemActivity.this, ContactSelectorActivity.class);
                startActivityForResult(i, 1);
            }
        });

        rdGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateTaskItemActivity.this, GroupSelectorActivity.class);
                startActivityForResult(i, 2);
            }
        });

        btnCreateTaskItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String description = etDescription.getText().toString();

                String assignedTo = null;
                if (rdContact.isChecked())
                    assignedTo = "contact";
                else if (rdGroup.isChecked())
                    assignedTo = "group";
                else {
                    tvCreateTaskMessage.setText("Please assign this task");
                    return;
                }

                String startDate = etName.getText().toString();
                String endDate = etName.getText().toString();

                new CreateTaskItem(name, description, assignedTo, mSelectedIds, startDate, endDate).execute();
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
    class CreateTaskItem implements JsonReaderSupport {

        private final String mName;
        private final String mDescription;
        private final String mAssignedTo;
        private final String mAssignIds;
        private final String mStartDate;
        private final String mEndDate;
        private String url =  Data.server + "data-save-task-item";

        public CreateTaskItem(String name, String description, String assignedTo, String assignIds, String startDate, String endDate) {
            mName = name;
            mDescription = description;
            mAssignedTo = assignedTo;
            mAssignIds = assignIds;
            mStartDate = startDate;
            mEndDate = endDate;
        }

        public void execute(){

            boolean valid = true;

            if(mName.trim().length()==0)
                valid = false;

            if(valid){
                tvCreateTaskMessage.setText("Creating");

                CreateTaskItemReader reader = new CreateTaskItemReader(this, url);
                reader.execute(new String[]{mName, mDescription, mAssignedTo, mAssignIds, mStartDate, mEndDate, mTaskId.toString()});
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

                    rdContact.setChecked(false);
                    rdGroup.setChecked(false);

                    etName.requestFocus();

                    tvCreateTaskMessage.setText("Task item created");
                }
                else if(result.toLowerCase().contains("duplicate"))
                    tvCreateTaskMessage.setText("Duplicate name");
                else if(result.toLowerCase().contains("invalid session")){
                    Intent i = new Intent(CreateTaskItemActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
            catch(Exception ex){
                tvCreateTaskMessage.setText("Error : " + ex.getMessage());
            }
        }
    }
}
