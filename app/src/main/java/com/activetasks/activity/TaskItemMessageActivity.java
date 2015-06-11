package com.activetasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import activetasks.activetasks.R;

public class TaskItemMessageActivity extends ActionBarActivity {

    private TextView tvUpdateTaskStatusMessage;
    private EditText etUpdateTaskStatus;
    private Button btnUpdateTaskStatus;
    private Button btnUpdateTaskStatusCancel;

    private RadioButton rdUpdateTaskStatusMessage;
    private RadioButton rdUpdateTaskStatusComplete;
    private RadioButton rdUpdateTaskStatusFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_item_message);

        tvUpdateTaskStatusMessage = (TextView) findViewById(R.id.tvMessageUpdateTaskStatus);
        etUpdateTaskStatus = (EditText) findViewById(R.id.etUpdateTaskStatus);
        btnUpdateTaskStatus = (Button) findViewById(R.id.btnUpdateTaskStatus);
        btnUpdateTaskStatusCancel = (Button) findViewById(R.id.btnUpdateTaskStatusCancel);

        rdUpdateTaskStatusMessage = (RadioButton) findViewById(R.id.rdUpdateTaskStatusMessage);
        rdUpdateTaskStatusComplete = (RadioButton) findViewById(R.id.rdUpdateTaskStatusComplete);
        rdUpdateTaskStatusFailed = (RadioButton) findViewById(R.id.rdUpdateTaskStatusFailed);


        btnUpdateTaskStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        btnUpdateTaskStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = etUpdateTaskStatus.getText().toString();
                String type = null;

                if (rdUpdateTaskStatusMessage.isChecked()) {
                    type = "message";
                } else if (rdUpdateTaskStatusComplete.isChecked()) {
                    type = "complete";
                } else if (rdUpdateTaskStatusFailed.isChecked()) {
                    type = "failed";
                } else {
                    tvUpdateTaskStatusMessage.setText("Please choose update type");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("message", message);
                intent.putExtra("type", type);
                setResult(1, intent);
                finish();

            }
        });
    }
}
