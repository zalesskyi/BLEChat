package com.zalesskyi.android.blechat.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleActivity
import com.cleveroad.bootstrap.kotlin_core.ui.NO_ID
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.bluetooth.BluetoothProvider
import com.zalesskyi.android.blechat.bluetooth.BluetoothProviderImpl
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseLifecycleActivity<MainVM>() {
    override val containerId = NO_ID

    override val layoutId = R.layout.activity_main

    override val viewModelClass = MainVM::class.java

    override fun getProgressBarId() = NO_ID

    override fun getSnackBarDuration() = Snackbar.LENGTH_SHORT

    private var bluetoothProvider: BluetoothProvider? = null

    private val messageObserver = Observer<String> {
        Toast.makeText(this@MainActivity, "New message: $it", Toast.LENGTH_SHORT).show()
    }

    private val advertisingNotSupportedObserver = Observer<Unit> {
        Toast.makeText(this@MainActivity, "Advertising not supported", Toast.LENGTH_SHORT).show()
    }

    private val connectedObserver = Observer<Unit> {
        Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
    }

    private val disconnectedObserver = Observer<Unit> {
        Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show()
    }

    override fun observeLiveData(viewModel: MainVM) = viewModel.run {
        messageReceivedLD.observe(this@MainActivity, messageObserver)
        advertisingNotSupportedLD.observe(this@MainActivity, advertisingNotSupportedObserver)
        connectedLD.observe(this@MainActivity, connectedObserver)
        disconnectedLD.observe(this@MainActivity, disconnectedObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
        viewModel.handleBluetoothEvents()
    }

    override fun onStop() {
        bluetoothProvider?.stopConnecting()
        super.onStop()
    }

    private fun initUi() {
        bScan.setOnClickListener { startScanning() }
        bAdvertise.setOnClickListener { startAdvertising() }
        bSend.setOnClickListener { viewModel.sendMessage("Hello") }
    }

    @SuppressLint("CheckResult")
    private fun startScanning() {
        RxPermissions(this)
              .request(Manifest.permission.ACCESS_COARSE_LOCATION)
              .subscribe { isAllowed ->
                  if (isAllowed) {
                      bluetoothProvider = BluetoothProviderImpl(BleMode.CENTRAL, this)
                      bluetoothProvider?.connecting()
                  }
              }
    }

    private fun startAdvertising() {
        bluetoothProvider = BluetoothProviderImpl(BleMode.PERIPHERAL, this)
        bluetoothProvider?.connecting()
    }
}