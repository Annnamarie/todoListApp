package com.three19.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.three19.todolist.database.ToDoListDB;
import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllTasksActivity extends AppCompatActivity {

    private ToDoListDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        // Initialize the database
        db = new ToDoListDB(this);


        // Retrieve all tasks from the database
        List<ToDo> tasksFromDB = db.getList();  // Assuming getList() is a method to get all tasks

        // Prepare a list to display
        List<String> taskDetails = new ArrayList<>();
        for (ToDo task : tasksFromDB) {
            taskDetails.add("Task: " + task.getName() +
                    "\nDue Date: " + task.getDueDate() +
                    "\nCategory: " + task.getCategory() +
                    "\nPriority: " + task.getPriority() +
                    "\nStatus: " + task.getStatus());
        }

        // Display tasks in a ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskDetails);
        ListView listView = findViewById(R.id.lstViewAllTasks);
        listView.setAdapter(adapter);

        // Back button to return to the main screen
        Button backBtn = findViewById(R.id.btnBackToMain);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main screen
                Intent intent = new Intent(AllTasksActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}


