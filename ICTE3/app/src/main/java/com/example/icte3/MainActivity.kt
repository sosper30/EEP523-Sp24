package com.example.icte3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    // Lateinit is used for variables that will be initialized later.
    private lateinit var imageView: ImageView
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Sets the UI layout for this activity

        // Initialize the ImageView and Button from the layout
        imageView = findViewById(R.id.selected_image_view)
        deleteButton = findViewById(R.id.button_delete_image)

        // Set an onClickListener for the "Add Image" button to handle camera permission and opening the camera
        findViewById<Button>(R.id.button_add_image).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission if not granted
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                // Open the camera if permission is granted
                openCamera()
            }
        }

        // Set an onClickListener for the "Delete Image" button to clear and hide the image and button
        deleteButton.setOnClickListener {
            imageView.setImageBitmap(null) // Clears the image from ImageView
            imageView.visibility = ImageView.GONE // Hides the ImageView
            deleteButton.visibility = Button.GONE // Hides the Delete Button
        }
    }

    // Function to open the camera using an implicit intent
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_CODE)
            //The startActivityForResult() method is deprecated in favor of the Activity Result API, which provides a more modern and flexible approach for handling the result returned by an activity.
        }
    }

    // Callback for the result from requesting permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, open the camera
            openCamera()
        } else {
            // Permission was denied, handle the case
        }
    }

    // Callback for the result from capturing an image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            // Process and display the captured image
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            imageView.visibility = ImageView.VISIBLE // Show the ImageView
            deleteButton.visibility = Button.VISIBLE // Show the Delete Button
        }
    }

    companion object {
        // Request codes for camera and permissions
        private const val IMAGE_CAPTURE_CODE = 1002
        private const val CAMERA_REQUEST_CODE = 1003
    }
}
