package com.zalesskyi.android.blechat.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import com.cleveroad.bootstrap.kotlin_core.ui.*
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.bluetooth.BluetoothProvider
import com.zalesskyi.android.blechat.bluetooth.BluetoothProviderImpl
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import com.zalesskyi.android.blechat.screens.pairing.PairingCallback
import com.zalesskyi.android.blechat.screens.pairing.tabs.PairingTabsFragment

class MainActivity : BaseLifecycleActivity<MainVM>(),
    BlockedCallback,
    BackPressedCallback,
    BottomNavigationCallback,
    PairingCallback {

    override val containerId = R.id.flContainer

    override val layoutId = R.layout.activity_main

    override val viewModelClass = MainVM::class.java

    override fun getProgressBarId() = NO_ID

    override fun getSnackBarDuration() = Snackbar.LENGTH_SHORT

    private var bluetoothProvider: BluetoothProvider? = null

    override fun observeLiveData() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceFragment(PairingTabsFragment.newInstance(), false)
    }

    override fun onStop() {
        bluetoothProvider?.stopConnecting()
        super.onStop()
    }

    override fun connect(mode: BleMode) {
        when (mode) {
            BleMode.CENTRAL -> startScanning()
            BleMode.PERIPHERAL -> startAdvertising()
        }
    }

    override fun stopLookForConnection() {
        bluetoothProvider?.stopLookForConnection()
    }

    override fun openChatScreen() {
        // todo
    }

    override fun onBlocked() = Unit

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