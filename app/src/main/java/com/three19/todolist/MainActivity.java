package com.three19.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.three19.todolist.database.ToDoListDB;
import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ToDoListDB toDoListDB;
    List<ToDo> arrayList;
    ToDoListAdapter adapter;
    Integer lastKeyID = 0;

    // UI elements
    ProgressBar progressBar;
    TextView notStartedPercentage, inProgressPercentage, completedPercentage;
    EditText txtName;
    Button addBtn;
    ToDo selectedToDo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar);
        notStartedPercentage = findViewById(R.id.notStartedPercentage);
        inProgressPercentage = findViewById(R.id.inProgressPercentage);
        completedPercentage = findViewById(R.id.completedPercentage);
        txtName = findViewById(R.id.txtName);
        addBtn = findViewById(R.id.btnAdd);
        //Filter Buttons
        Button btnCategoryFilter = findViewById(R.id.btnCategoryFilter);
        Button btnPriorityFilter = findViewById(R.id.btnPriorityFilter);
        Button btnResetFilter = findViewById(R.id.btnResetFilter);


        toDoListDB = new ToDoListDB(this);
        arrayList = toDoListDB.getList();
        adapter = new ToDoListAdapter(this, (ArrayList<ToDo>) arrayList);

        ListView listView = findViewById(R.id.lstView);
        listView.setAdapter(adapter);

        updateProgressBar();

        // Click listener for selecting a task
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedToDo = arrayList.get(position);
                txtName.setText(selectedToDo.getName());
                addBtn.setText("Update");
            }
        });

        // Long-click listener for deleting a task
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeItemFromList(position);
                return true;
            }
        });

        // Add/Update button functionality
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString().trim();
                if (!name.isEmpty()) {
                    if (selectedToDo == null) {
                        openTaskDetailsDialog(null, name);
                    } else {
                        openTaskDetailsDialog(selectedToDo, name);
                    }
                    txtName.setText("");
                    addBtn.setText("Add");
                    selectedToDo = null;
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clear button
        Button clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtName.setText("");
                addBtn.setText("Add");
                selectedToDo = null;
            }
        });

        // Show all tasks button
        Button showAllBtn = findViewById(R.id.btnShowAllTasks);
        showAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllTasksActivity.class);
                startActivity(intent);
            }
        });
        //Filter implementation
        btnCategoryFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryFilterDialog();
            }
        });

        btnPriorityFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriorityFilterDialog();
            }
        });

        btnResetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilters();
            }
        });

        Button btnTaskCount = findViewById(R.id.btnTaskCount);

        btnTaskCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> report = toDoListDB.getTaskCountByPriority();

                if (report.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No tasks found.", Toast.LENGTH_SHORT).show();
                } else {
                    // Combine all lines into one message
                    StringBuilder message = new StringBuilder();
                    for (String line : report) {
                        message.append(line).append("\n");
                    }
                    // Show report in a dialog
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Task Count by Priority")
                            .setMessage(message.toString())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });



    }

    private void openTaskDetailsDialog(final ToDo toDo, final String taskName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_task_details, null);

        EditText editDueDate = view.findViewById(R.id.editDueDate);
        EditText editCategory = view.findViewById(R.id.editCategory);
        EditText editPriority = view.findViewById(R.id.editPriority);
        Spinner statusSpinner = view.findViewById(R.id.spinnerStatus);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.task_status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        if (toDo != null) {
            editDueDate.setText(toDo.getDueDate());
            editCategory.setText(toDo.getCategory());
            editPriority.setText(toDo.getPriority());
            statusSpinner.setSelection(((ArrayAdapter<String>) statusSpinner.getAdapter()).getPosition(toDo.getStatus()));
        }

        // Now STOP chaining here and set buttons separately:
        builder.setView(view);
        builder.setTitle(toDo == null ? "Add Task" : "Edit Task");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton(toDo == null ? "Add" : "Save", null); // temp null

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Override PositiveButton manually
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dueDate = editDueDate.getText().toString().trim();
                String category = editCategory.getText().toString().trim();
                String priority = editPriority.getText().toString().trim();
                String status = statusSpinner.getSelectedItem().toString();

                if (dueDate.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a Due Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (category.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a Category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (priority.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a Priority", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (status.equals("Select Status")) {
                    Toast.makeText(MainActivity.this, "Please select a Status", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (toDo == null) {
                    ToDo newToDo = toDoListDB.add(taskName, dueDate, category, priority, status);
                    arrayList.add(newToDo);
                } else {
                    toDo.setDueDate(dueDate);
                    toDo.setCategory(category);
                    toDo.setPriority(priority);
                    toDo.setStatus(status);
                    boolean success = toDoListDB.update(toDo);
                    if (success) {
                        Toast.makeText(MainActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                    }
                }

                adapter.notifyDataSetChanged();
                saveToSharedPreferences(toDo == null ? arrayList.get(arrayList.size() - 1) : toDo);
                updateProgressBar();

                NotificationHelper.sendNotification(MainActivity.this, "Task Reminder!", "You have tasks to complete today.");
                alertDialog.dismiss(); // Only close dialog if everything is OK
            }

        });
    }


    private void saveToSharedPreferences(ToDo toDo) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        String taskDetails = toDo.getName() + "|" + toDo.getDueDate() + "|" + toDo.getCategory() + "|"
                + toDo.getPriority() + "|" + toDo.getStatus();
        editor.putString(String.valueOf(toDo.getId()), taskDetails);
        editor.apply();
    }

    private void removeItemFromList(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this task?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToDo toDo = arrayList.get(position);
                toDoListDB.remove(toDo.getId());
                arrayList.remove(position);
                adapter.notifyDataSetChanged();
                updateProgressBar();
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void updateProgressBar() {
        int totalTasks = arrayList.size();
        int notStarted = 0, inProgress = 0, completed = 0;

        // Count how many tasks are in each status
        for (ToDo task : arrayList) {
            switch (task.getStatus()) {
                case "Not Started":
                    notStarted++;
                    break;
                case "In Progress":
                    inProgress++;
                    break;
                case "Completed":
                    completed++;
                    break;
            }
        }

        // Calculate the percentage of tasks in each status
        float notStartedPercentageVal = totalTasks > 0 ? (float) notStarted / totalTasks * 100 : 0;
        float inProgressPercentageVal = totalTasks > 0 ? (float) inProgress / totalTasks * 100 : 0;
        float completedPercentageVal = totalTasks > 0 ? (float) completed / totalTasks * 100 : 0;

        // Set progress for the progress bar
        progressBar.setMax(100);  // The progress bar's max value
        progressBar.setProgress((int) completedPercentageVal);  // Set progress for Completed tasks
        progressBar.setSecondaryProgress((int) (completedPercentageVal + inProgressPercentageVal));  // Set for In Progress tasks

        // Update TextViews with the percentage of each status
        completedPercentage.setText("Completed: " + Math.round(completedPercentageVal) + "%");
        inProgressPercentage.setText("In Progress: " + Math.round(inProgressPercentageVal) + "%");
        notStartedPercentage.setText("Not Started: " + Math.round(notStartedPercentageVal) + "%");


    }

    private void showCategoryFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");

        // Get unique categories
        List<String> categories = new ArrayList<>();
        for (ToDo task : arrayList) {
            if (!categories.contains(task.getCategory())) {
                categories.add(task.getCategory());
            }
        }
        final String[] categoryArray = categories.toArray(new String[0]);

        builder.setItems(categoryArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterTasksByCategory(categoryArray[which]);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void filterTasksByCategory(String category) {
        List<ToDo> filteredList = new ArrayList<>();
        for (ToDo task : arrayList) {
            if (task.getCategory().equals(category)) {
                filteredList.add(task);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void showPriorityFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Priority");

        // Get unique priorities
        List<String> priorities = new ArrayList<>();
        for (ToDo task : arrayList) {
            if (!priorities.contains(task.getPriority())) {
                priorities.add(task.getPriority());
            }
        }
        final String[] priorityArray = priorities.toArray(new String[0]);

        builder.setItems(priorityArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterTasksByPriority(priorityArray[which]);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void filterTasksByPriority(String priority) {
        List<ToDo> filteredList = new ArrayList<>();
        for (ToDo task : arrayList) {
            if (task.getPriority().equals(priority)) {
                filteredList.add(task);
            }
        }
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void resetFilters() {
        adapter.clear();
        adapter.addAll(toDoListDB.getList());
        adapter.notifyDataSetChanged();
    }



    class ToDoListAdapter extends ArrayAdapter<ToDo> {
    public ToDoListAdapter(Context context, ArrayList<ToDo> toDoList) {
        super(context, 0, toDoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToDo toDo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView name = convertView.findViewById(android.R.id.text1);
        name.setText(toDo.getName() + " - " + toDo.getStatus() + " - " + toDo.getPriority());

        return convertView;
    }


}}