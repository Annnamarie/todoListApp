package com.three19.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6; // Increment version
    private static final String DATABASE_NAME = "data";

    // Table
    private static final String TABLE_TODO = "todos";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_STATUS = "status";

    // Table creation statement
    private static final String CREATE_TABLE_TODO =
            "CREATE TABLE " + TABLE_TODO + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DUE_DATE + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_PRIORITY + " TEXT, " +
                    COLUMN_STATUS + " TEXT);";

    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the tasks table
        db.execSQL(CREATE_TABLE_TODO);

        // Create indexes to optimize searching and filtering
        db.execSQL("CREATE INDEX idx_due_date ON " + TABLE_TODO + " (" + COLUMN_DUE_DATE + ");");
        db.execSQL("CREATE INDEX idx_priority ON " + TABLE_TODO + " (" + COLUMN_PRIORITY + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) { // Apply missing columns only if upgrading from an older version
            try {
                db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_DUE_DATE + " TEXT;");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT;");
            } catch (Exception ignored) {}
            try {
                db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_STATUS + " TEXT;");
            } catch (Exception ignored) {}
        }
    }
}
