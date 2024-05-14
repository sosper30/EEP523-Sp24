package edu.uw.ee523.class8

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LaunchWebActivity : AppCompatActivity(){

    private var mUrlEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_web)
        mUrlEditText = findViewById(R.id.web_dest_edit_text) as EditText?
    }

    fun launchWeb(view: View?) {
        // Get the URL from the EditText
        val url = mUrlEditText!!.text.toString()

        // Create a URI from the URL
        val uri = Uri.parse(url)

        // Build the intent
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // Start the activity
        startActivity(intent)
    }
}