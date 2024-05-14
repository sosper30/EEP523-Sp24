package edu.uw.ee523.class8

import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NetworkInfoActivity: AppCompatActivity()  {


    // The BroadcastReceiver that tracks network connectivity changes.
    private var receiver: NetworkReceiver? = NetworkReceiver()

    private var userNetworkPref: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_info)

        // Registers BroadcastReceiver to track network connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = NetworkReceiver()
        this.registerReceiver(receiver, filter)
    }

    override fun onStart() {
        super.onStart()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val networkPref = sharedPrefs.getString("networkType", "wifi")
        val pref_tv = findViewById(R.id.user_pref_textview) as TextView
        pref_tv.text = networkPref
        userNetworkPref = networkPref
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
    }

    fun checkConnection(view: View?) {
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiTV = findViewById(R.id.wifi_connected_textview) as TextView
        val cellTV = findViewById(R.id.cell_connected_textview) as TextView

        // How to do this changed in Lollipop. Including both versions here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networks = connMgr.allNetworks
            var networkInfo: NetworkInfo?
            for (mNetwork in networks) {
                networkInfo = connMgr.getNetworkInfo(mNetwork)
                val info = getDescString(networkInfo)
                when (networkInfo!!.type) {
                    ConnectivityManager.TYPE_WIFI -> wifiTV.text = info
                    ConnectivityManager.TYPE_MOBILE -> cellTV.text = info
                }
            }
        } else {
            // To account for Android API < 21.
            var networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            wifiTV.text = getDescString(networkInfo)
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            cellTV.text = getDescString(networkInfo)
        }
    }


    private fun getDescString(networkInfo: NetworkInfo?): String {
        var info = java.lang.Boolean.toString(networkInfo!!.state == NetworkInfo.State.CONNECTED)
        info += "; "
        info += networkInfo.typeName
        info += "; "
        info += networkInfo.subtypeName
        return info
    }

    fun isOnline(): Boolean {
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun setNetworkPrefs(view: View?) {
        startActivity(Intent(this, NetworkSettingsPrefActivity::class.java))
    }

    inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val conn = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = conn.activeNetworkInfo

            // Checks the user prefs and the network connection.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if ("wi-fi only" == userNetworkPref && networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Can do data task on Wi-Fi", Toast.LENGTH_LONG).show()

                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile)
            } else if ("any" == userNetworkPref &&
                networkInfo != null
            ) {
                Toast.makeText(context, "Can do data task on mobile connection", Toast.LENGTH_LONG)
                    .show()

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
            } else {
                Toast.makeText(
                    context,
                    "Not connected, or can't do data task on mobile. ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}