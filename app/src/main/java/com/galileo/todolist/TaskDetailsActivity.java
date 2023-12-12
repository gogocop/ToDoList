package com.galileo.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.galileo.todolist.model.ToDoModel;
import com.galileo.todolist.utils.DatabaseHandler;

public class TaskDetailsActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        db = new DatabaseHandler(this);

        // Retrieve task details from the intent
        Intent intent = getIntent();
        if (intent != null) {
            taskId = intent.getIntExtra("taskId", -1);
            String taskName = intent.getStringExtra("taskName");
            String taskDescription = intent.getStringExtra("taskDescription");
            String taskType = intent.getStringExtra("taskType");
            int taskStatus = intent.getIntExtra("taskStatus", 0); // Default to 0 if not provided

            // Now you have task details, you can use them as needed
            TextView titleTextView = findViewById(R.id.titleTextView);
            TextView descriptionTextView = findViewById(R.id.descriptionTextView);
            TextView typeTextView = findViewById(R.id.typeTextView);
            CheckBox statusCheckBox = findViewById(R.id.statusCheckBox);

            titleTextView.setText(taskName);
            descriptionTextView.setText(taskDescription);
            typeTextView.setText(taskType);

            // Set the initial state of the CheckBox based on the task status
            statusCheckBox.setChecked(taskStatus == 1);

            // Add a listener to handle CheckBox state changes
            statusCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Handle CheckBox state changes here
                updateTaskStatus(isChecked);
            });
        }
    }

    private void updateTaskStatus(boolean isChecked) {
        int status = isChecked ? 1 : 0;
        db.updateStatus(taskId, status);

        // Notify MainActivity that data has changed
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);

        // Finish the activity
        finish();
    }
}
