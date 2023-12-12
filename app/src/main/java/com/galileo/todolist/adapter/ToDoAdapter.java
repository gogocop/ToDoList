package com.galileo.todolist.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galileo.todolist.AddNewTask;
import com.galileo.todolist.MainActivity;
import com.galileo.todolist.R;
import com.galileo.todolist.model.ToDoModel;
import com.galileo.todolist.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private MainActivity activity;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);

        // Use the bind method to set the text for task CheckBox
        holder.bind(item);

        // Set the checked state and define the onCheckedChangeListener
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int newStatus = isChecked ? 1 : 0;
                db.updateStatus(item.getId(), newStatus);
            }
        });
    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("description", item.getDescription());
        bundle.putString("type", item.getType());

        AddNewTask fragment = AddNewTask.newInstance();
        fragment.setArguments(bundle);

        // Check if the fragment is already added to avoid multiple instances
        if (!fragment.isAdded()) {
            fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }

        void bind(ToDoModel item) {
            // Format the text to display Task, Description, and Type
            String taskText = String.format("Tarea: %s\nDescripcion: %s\nTipo: %s",
                    item.getTask(), item.getDescription(), item.getType());

            // Set the formatted text to the task CheckBox
            task.setText(taskText);
        }
    }
}