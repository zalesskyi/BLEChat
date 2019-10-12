package com.zalesskyi.android.blechat.bluetooth.ble

interface BleProvider {

    fun lookForConnection()

    fun stopLookForConnection()

    fun disconnect()

    fun sendMessage(message: ByteArray)
}