package com.ameerapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton gitbtn_add;
    private DatabaseReference databaseReference;
    private List<TaskModel> taskList;
    private TaskAdapter taskAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        View btn_add = findViewById(R.id.add_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");

        btn_add.setOnClickListener(v -> showAddTaskDialog());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TaskModel task = snapshot.getValue(TaskModel.class);
                    taskList.add(task);
                }
                if (taskAdapter == null) {
                    taskAdapter = new TaskAdapter(MainActivity.this, taskList, new TaskAdapter.OnTaskClickListener(){
                        @Override
                        public void onUpdateClick(int position) {
                            TaskModel updatedTask = taskList.get(position);
                            updateTask(updatedTask);
                        }

                        @Override
                        public void onDeleteClick(int position) {
                            TaskModel deletedTask = taskList.get(position);
                            deleteTask(deletedTask);
                        }

                        @Override
                        public void onTaskClick(TaskModel task) {
                            // Handle task click if needed
                        }
                    });
                    recyclerView.setAdapter(taskAdapter);
                } else {
                    taskAdapter.notifyDataSetChanged(); // Notify adapter of dataset changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        final EditText inputField = new EditText(this);
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputField);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = inputField.getText().toString().trim();
            if (!TextUtils.isEmpty(taskName)) {
                String id = databaseReference.push().getKey();
                TaskModel task = new TaskModel(id, taskName);
                assert id != null;
                databaseReference.child (id).setValue(task);
                Toast.makeText(MainActivity.this, "Task added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateTask(final TaskModel task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Task");

        final EditText inputField = new EditText(this);
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputField.setText(task.getName());
        builder.setView(inputField);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedTaskName = inputField.getText().toString().trim();
            if (!TextUtils.isEmpty(updatedTaskName)) {
                task.setName(updatedTaskName);
                databaseReference.child(task.getId()).setValue(task);
                Toast.makeText(MainActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteTask(final TaskModel task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            databaseReference.child(task.getId()).removeValue();
            Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
