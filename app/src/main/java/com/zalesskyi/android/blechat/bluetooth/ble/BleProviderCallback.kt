package com.zalesskyi.android.blechat.bluetooth.ble

interface BleProviderCallback {

    fun onConnectionFail(errorCode: Int)

    fun onConnectionEstablished()

    fun onMessageArrived(message: ByteArray)

    fun onConnectionLost(errorCode: Int)
}