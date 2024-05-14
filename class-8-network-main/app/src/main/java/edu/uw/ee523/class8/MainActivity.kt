package edu.uw.ee523.class8

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startLaunchWebActivity(view: View?) {
        startActivity(Intent(this@MainActivity, LaunchWebActivity::class.java))
    }

    fun startWebViewActivity(view: View?) {
        startActivity(Intent(this@MainActivity, WebViewActivity::class.java))
    }

    fun startWebServiceActivity(view: View?) {
        startActivity(Intent(this@MainActivity, WebServiceActivity::class.java))
    }

    fun startNetworkInfoActivity(view: View?) {
        startActivity(Intent(this@MainActivity, NetworkInfoActivity::class.java))
    }
}