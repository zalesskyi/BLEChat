package com.zalesskyi.android.blechat.bluetooth

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import com.zalesskyi.android.blechat.bluetooth.ble.BleProvider
import com.zalesskyi.android.blechat.bluetooth.ble.BleProviderCallback
import com.zalesskyi.android.blechat.bluetooth.ble.central.CentralBleProvider
import com.zalesskyi.android.blechat.bluetooth.ble.peripheral.PeripheralBleProvider

interface TempCallback {

    fun onMessage(message: String)
}

interface BluetoothProvider {

    fun send(message: String)   // todo temp

    fun connecting()

    fun stopConnecting()
}

class BluetoothProviderImpl(mode: BleMode, context: Context, callback: TempCallback) : BluetoothProvider {

    companion object {
        private val TAG = BluetoothProviderImpl::class.java.simpleName
    }

    private val callback = object : BleProviderCallback {

        override fun onConnectionFail(errorCode: Int) {
            Log.i(TAG, "onConnectionFail")
        }

        override fun onConnectionEstablished() {
            Log.i(TAG, "onConnectionEstablished")
        }

        override fun onMessageArrived(message: ByteArray) {
            Log.i(TAG, "onMessageArrived: ${String(message)}")
            Handler(Looper.getMainLooper()).post { callback.onMessage(String(message)) }
        }

        override fun onConnectionLost(errorCode: Int) {
            Log.i(TAG, "onConnectionLost")
        }
    }

    private var bleProvider: BleProvider? = null

    init {
        bleProvider = getBleProvider(mode, context)
    }

    override fun connecting() {
        bleProvider?.lookForConnection()
    }

    override fun stopConnecting() {
        bleProvider?.run {
            stopLookForConnection()
            disconnect()
        }
    }

    override fun send(message: String) {
        bleProvider?.sendMessage(message.toByteArray())
    }

    private fun getBleProvider(mode: BleMode, context: Context): BleProvider = when (mode) {
        BleMode.CENTRAL -> CentralBleProvider(context, callback)
        BleMode.PERIPHERAL -> PeripheralBleProvider(context, callback)
    }
}