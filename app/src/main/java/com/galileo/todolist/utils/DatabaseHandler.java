package com.galileo.todolist.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.galileo.todolist.model.ToDoModel;

import java.util.ArrayList;
import java.util.List;
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 3;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TASK + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            TYPE + " TEXT, " +
            STATUS + " INTEGER)";


    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(TYPE, task.getType());
        cv.put(STATUS, 0);

        // Log statements to check the values being inserted
        Log.d("DatabaseHandler Insert", "Task: " + task.getTask());
        Log.d("DatabaseHandler Insert", "Description: " + task.getDescription());
        Log.d("DatabaseHandler Insert", "Type: " + task.getType());

        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                int idIndex = cur.getColumnIndexOrThrow(ID);
                int taskIndex = cur.getColumnIndexOrThrow(TASK);
                int statusIndex = cur.getColumnIndexOrThrow(STATUS);
                int descriptionIndex = cur.getColumnIndexOrThrow(DESCRIPTION);
                int typeIndex = cur.getColumnIndexOrThrow(TYPE);

                do {
                    ToDoModel task = new ToDoModel();

                    // Check if the column index is valid (greater than or equal to 0)
                    if (idIndex >= 0) {
                        task.setId(cur.getInt(idIndex));
                    } else {
                        // Handle the case where the ID column is not found
                        // You may choose to skip this task or set a default value
                        continue;
                    }

                    if (taskIndex >= 0) {
                        task.setTask(cur.getString(taskIndex));
                    } else {
                        // Handle the case where the TASK column is not found
                        // You may choose to skip this task or set a default value
                        continue;
                    }

                    if (statusIndex >= 0) {
                        task.setStatus(cur.getInt(statusIndex));
                    } else {
                        // Handle the case where the STATUS column is not found
                        // You may choose to skip this task or set a default value
                        continue;
                    }
                    if (descriptionIndex >= 0) {
                        task.setDescription(cur.getString(descriptionIndex));
                    } else {
                        // Handle the case where the DESCRIPTION column is not found
                        // You may choose to skip this task or set a default value
                        task.setDescription("Default Description");
                    }

                    if (typeIndex >= 0) {
                        task.setType(cur.getString(typeIndex));
                    } else {
                        // Handle the case where the TYPE column is not found
                        // You may choose to skip this task or set a default value
                        task.setType("Default Type");
                    }
                    Log.d("DatabaseHandler Get", "Task: " + task.getTask());
                    Log.d("DatabaseHandler Get", "Description: " + task.getDescription());
                    Log.d("DatabaseHandler Get", "Type: " + task.getType());
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            db.endTransaction();
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }


    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateDescription(int id, String description) {
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, description);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void updateType(int id, String type) {
        ContentValues cv = new ContentValues();
        cv.put(TYPE, type);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }


    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}