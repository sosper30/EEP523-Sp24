package com.example.hw1_template

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// NewTaskSheet: This Activity/Fragment is responsible for adding new tasks.
// Neglecting the input handling here will break the core functionality of task addition.
class NewTaskSheet : AppCompatActivity() {

    // onCreate: Again, crucial for setting up the layout and functionality.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_new_task_sheet)

        // Set up the UI components like EditText and Button, and handle their events.
        // TODO: Initialize your EditText and Button for task input and saving
    }
}
