package com.zalesskyi.android.blechat.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cleveroad.bootstrap.kotlin_rx_bus.RxBus
import com.zalesskyi.android.blechat.bluetooth.ble.*
import com.zalesskyi.android.blechat.bluetooth.ble.central.CentralBleProvider
import com.zalesskyi.android.blechat.bluetooth.ble.peripheral.PeripheralBleProvider
import com.zalesskyi.android.blechat.bluetooth.events.*

interface BluetoothProvider {

    fun connecting()

    fun stopConnecting()
}

class BluetoothProviderImpl(mode: BleMode, context: Context) : BluetoothProvider {

    private val callback = object : BleProviderCallback {

        override fun onConnectionFail(errorCode: Int) {
            when (errorCode) {
                SCAN_FAILED, GATT_SERVICE_NOT_FOUND -> reconnect()
                ADVERTISING_NOT_SUPPORTED -> dispatchAdvertisingNotSupportedEvent()
            }
        }

        override fun onConnectionEstablished() {
            dispatchConnectedEvent()
        }

        override fun onMessageArrived(message: ByteArray) {
            dispatchReceivedEvent(String(message))
        }

        override fun onConnectionLost(errorCode: Int) {
            if (errorCode == GATT_ERROR) reconnect()
            dispatchDisconnectedEvent()
        }
    }

    private var bleProvider: BleProvider? = null

    init {
        bleProvider = getBleProvider(mode, context)
        handleSentEvent()
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

    private fun send(message: String) {
        bleProvider?.sendMessage(message.toByteArray())
    }

    @SuppressLint("CheckResult")
    private fun handleSentEvent() {
        RxBus.filter(MessageSentEvent::class.java)
            .map { it.message }
            .subscribe { send(it) }
    }

    private fun dispatchReceivedEvent(message: String) {
        RxBus.send(MessageReceivedEvent(message))
    }

    private fun dispatchAdvertisingNotSupportedEvent() {
        RxBus.send(AdvertisingNotSupportedEvent())
    }

    private fun dispatchConnectedEvent() {
        RxBus.send(ConnectedEvent())
    }

    private fun dispatchDisconnectedEvent() {
        RxBus.send(DisconnectedEvent())
    }

    private fun reconnect() {
        stopConnecting()
        connecting()
    }

    private fun getBleProvider(mode: BleMode, context: Context): BleProvider = when (mode) {
        BleMode.CENTRAL -> CentralBleProvider(context, callback)
        BleMode.PERIPHERAL -> PeripheralBleProvider(context, callback)
    }
}