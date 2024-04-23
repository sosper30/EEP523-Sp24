package com.example.mybluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.nearby.connection.ConnectionsClient

class MainActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    // ArrayAdapter to manage the display of discovered devices in the ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var buttonDiscover: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        buttonDiscover = findViewById(R.id.button_Discover)
        // Setup the ListView and ArrayAdapter to display discovered devices
        val listView: ListView = findViewById(R.id.listViewScannedDevices)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = arrayAdapter

        // IntentFilter to listen for Bluetooth devices found during discovery
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)


        buttonDiscover.setOnClickListener {
            startDiscovery()
        }

    }

    // Method to start the Bluetooth discovery process
    private fun startDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter?.startDiscovery()
        } else {
            // Show a toast message if BLUETOOTH_SCAN permission is not granted
            Toast.makeText(this, "BLUETOOTH_SCAN permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { !it.value }) {
            Toast.makeText(this, "Required permissions needed", Toast.LENGTH_LONG).show()
            finish()
        } else {
            recreate()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Retrieve the BluetoothDevice from the intent
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        // Check for BLUETOOTH_CONNECT permission before accessing device name and address
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
                            // Add the device name and address to the ArrayAdapter for display in the ListView
                            Toast.makeText(context, "Found device: ${device.name}", Toast.LENGTH_SHORT).show()

                            arrayAdapter.add("${it.name ?: "Unknown"} | ${it.address}")
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestMultiplePermissions.launch(
                REQUIRED_PERMISSIONS
            )
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.isEmpty() || permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private companion object {
        val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
    }
}
