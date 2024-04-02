package com.ameerapp;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private final List<TaskModel> taskList;
    private final OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onUpdateClick(int position);

        void onDeleteClick(int position);

        void onTaskClick(TaskModel task);
    }

    public TaskAdapter(Context context, List<TaskModel> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskModel task = taskList.get(position);
        holder.taskNameTextView.setText(task.getName());

        holder.itemView.setOnClickListener(v -> {
            // Implement click listener to update or delete task
            listener.onUpdateClick(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onDeleteClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
        }
    }


}