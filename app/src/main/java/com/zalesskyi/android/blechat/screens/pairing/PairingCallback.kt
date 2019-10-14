package com.zalesskyi.android.blechat.screens.pairing

import com.zalesskyi.android.blechat.bluetooth.ble.BleMode

interface PairingCallback {

    fun connect(mode: BleMode)

    fun stopLookForConnection()

    fun openChatScreen()
}