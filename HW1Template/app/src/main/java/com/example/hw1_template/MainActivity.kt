package com.example.hw1_template

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// MainActivity: The heart of your application's UI.
// This class should coordinate the main user interactions and screen transitions.
class MainActivity : AppCompatActivity() {

    // onCreate: Critical for initializing the activity and setting up the UI components.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView setup: Essential for displaying a list of items.
        // You MUST properly initialize and configure your RecyclerView and its adapter.
        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        // TaskItemAdapter is your custom adapter for the RecyclerView. You need to create this file.
        recyclerView.adapter = // TODO: Initialize your TaskItemAdapter with data

        // FloatingActionButton: Triggers the creation of new tasks.
        // The onClickListener here is vital for handling user actions to add new tasks.
        val fabNewTask = findViewById<FloatingActionButton>(R.id.fabNewTask)
        fabNewTask.setOnClickListener {
            // TODO: Implement the logic to show NewTaskSheet (you need to create this Activity/Fragment)
        }
    }

    // Instructions:
    // 1. Create NewTaskSheet.kt for handling the creation of new tasks.
    // 2. Implement TaskItemAdapter.kt to manage how task data is bound to the RecyclerView.
    // 3. Understand how TaskItem.kt represents individual task data.
    // 4. TaskViewModel.kt should handle all your data logic, like adding and retrieving tasks.
    // 5. TaskItemClickListener.kt and TaskItemViewHolder.kt are crucial for handling item interactions in your RecyclerView.

    // To add new Kotlin files for your classes, like NewTaskSheet or TaskItemAdapter,
// right-click on the package directory in the 'src/main/java' (or 'src/main/kotlin') folder in the Project view,
// then choose 'New' > 'Kotlin File/Class', name your file/class


