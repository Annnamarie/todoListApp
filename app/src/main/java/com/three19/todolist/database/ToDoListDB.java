package com.three19.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.three19.todolist.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoListDB extends DBConnection {

    private static final String TABLE_TODO = "todos";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_STATUS = "status";

    public ToDoListDB(Context context) {
        super(context);
    }

    public ToDo add(String name, String dueDate, String category, String priority, String status) {

        // CREATE - inserting a new To Do item
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_DUE_DATE, dueDate);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PRIORITY, priority);
        values.put(COLUMN_STATUS, status);

        long id = db.insert(TABLE_TODO, null, values);
        db.close();

        return new ToDo((int) id, name, dueDate, category, priority, status);
    }

    public boolean update(ToDo toDo) {

        // UPDATE - updating an existing To Do item
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, toDo.getName());
        values.put(COLUMN_DUE_DATE, toDo.getDueDate());
        values.put(COLUMN_CATEGORY, toDo.getCategory());
        values.put(COLUMN_PRIORITY, toDo.getPriority());
        values.put(COLUMN_STATUS, toDo.getStatus());


        int rowsAffected = db.update(TABLE_TODO, values, COLUMN_ID + " = ?", new String[]{String.valueOf(toDo.getId())});
        db.close();

        return rowsAffected > 0;
    }

    public void remove(int id) {

        // DELETE
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ToDo getDetails(int id) {
        // READ - Query the database for the specific To Do with the given id
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TODO,
                new String[]{
                        COLUMN_ID,
                        COLUMN_NAME,
                        COLUMN_DUE_DATE,
                        COLUMN_CATEGORY,
                        COLUMN_PRIORITY,
                        COLUMN_STATUS
                },
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        // Check if the cursor is not null and has at least one row
        if (cursor != null && cursor.moveToFirst()) {
            // Extract data from the cursor with null checks
            int idColumn = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

            // Check for null in each column and assign empty string if null
            String name = cursor.isNull(cursor.getColumnIndex(COLUMN_NAME)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String dueDate = cursor.isNull(cursor.getColumnIndex(COLUMN_DUE_DATE)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
            String category = cursor.isNull(cursor.getColumnIndex(COLUMN_CATEGORY)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
            String priority = cursor.isNull(cursor.getColumnIndex(COLUMN_PRIORITY)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY));
            String status = cursor.isNull(cursor.getColumnIndex(COLUMN_STATUS)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

            // Create a To Do object using the values from the cursor
            ToDo toDo = new ToDo(idColumn, name, dueDate, category, priority, status);

            // Close the cursor to free resources
            cursor.close();

            return toDo;
        }

        // Return null if no record is found
        if (cursor != null) {
            cursor.close();  // Always close the cursor to avoid leaks
        }
        return null;
    }

    public List<ToDo> getList() {
        List<ToDo> toDoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO, null);

        if (cursor.moveToFirst()) {
            do {
                int idColumn = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

                String name = cursor.isNull(cursor.getColumnIndex(COLUMN_NAME)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String dueDate = cursor.isNull(cursor.getColumnIndex(COLUMN_DUE_DATE)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));
                String category = cursor.isNull(cursor.getColumnIndex(COLUMN_CATEGORY)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                String priority = cursor.isNull(cursor.getColumnIndex(COLUMN_PRIORITY)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY));
                String status = cursor.isNull(cursor.getColumnIndex(COLUMN_STATUS)) ? "" : cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));

                ToDo toDo = new ToDo(idColumn, name, dueDate, category, priority, status);
                toDoList.add(toDo);

            } while (cursor.moveToNext());
        }

        cursor.close();

        java.util.Collections.sort(toDoList, new java.util.Comparator<ToDo>() {
            @Override
            public int compare(ToDo task1, ToDo task2) {
                int score1 = calculatePriorityScore(task1);
                int score2 = calculatePriorityScore(task2);
                return Integer.compare(score2, score1); // Descending order
            }
        });


        return toDoList;
    }

    private int calculatePriorityScore(ToDo task) {
        int score = 0;

        // Give points based on task's status
        if (task.getStatus().equalsIgnoreCase("Not Started")) {
            score += 20;
        } else if (task.getStatus().equalsIgnoreCase("In Progress")) {
            score += 10;
        }

        // Give points based on task's priority
        if (task.getPriority().equalsIgnoreCase("High")) {
            score += 30;
        } else if (task.getPriority().equalsIgnoreCase("Medium")) {
            score += 20;
        } else if (task.getPriority().equalsIgnoreCase("Low")) {
            score += 10;
        }

        // Bonus points for tasks that have earlier deadlines
        String dueDate = task.getDueDate();
        if (dueDate != null && !dueDate.isEmpty()) {
            score += 50;  // (Optional: Later we could parse the date and add more logic)
        }

        return score;
    }

    public List<String> getTaskCountByPriority() {
        List<String> report = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query: count how many tasks per priority
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_PRIORITY + ", COUNT(*) as count FROM " + TABLE_TODO + " GROUP BY " + COLUMN_PRIORITY,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                String priority = cursor.isNull(cursor.getColumnIndex(COLUMN_PRIORITY)) ? "Unknown" : cursor.getString(cursor.getColumnIndex(COLUMN_PRIORITY));
                int count = cursor.getInt(cursor.getColumnIndex("count"));

                report.add(priority + ": " + count + " task(s)");
            } while (cursor.moveToNext());
        }
        cursor.close();

        return report;
    }




}
