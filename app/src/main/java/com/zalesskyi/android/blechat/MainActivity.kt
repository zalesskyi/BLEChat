package com.zalesskyi.android.blechat

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zalesskyi.android.blechat.bluetooth.BluetoothProvider
import com.zalesskyi.android.blechat.bluetooth.BluetoothProviderImpl
import com.zalesskyi.android.blechat.bluetooth.TempCallback
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tempCallback = object : TempCallback {

        override fun onMessage(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private var bluetoothProvider: BluetoothProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
    }

    override fun onStop() {
        bluetoothProvider?.stopConnecting()
        super.onStop()
    }

    private fun initUi() {
        bScan.setOnClickListener { startScanning() }
        bAdvertise.setOnClickListener { startAdvertising() }
        bSend.setOnClickListener { bluetoothProvider?.send("Hello") }
    }

    @SuppressLint("CheckResult")
    private fun startScanning() {
        RxPermissions(this)
              .request(Manifest.permission.ACCESS_COARSE_LOCATION)
              .subscribe { isAllowed ->
                  if (isAllowed) {
                      bluetoothProvider = BluetoothProviderImpl(BleMode.CENTRAL, this, tempCallback)
                      bluetoothProvider?.connecting()
                  }
              }
    }

    private fun startAdvertising() {
        bluetoothProvider = BluetoothProviderImpl(BleMode.PERIPHERAL, this, tempCallback)
        bluetoothProvider?.connecting()
    }
}