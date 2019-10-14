package com.zalesskyi.android.blechat.screens.pairing.connecting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_rx_bus.RxBus
import com.zalesskyi.android.blechat.bluetooth.events.AdvertisingNotSupportedEvent
import com.zalesskyi.android.blechat.bluetooth.events.ConnectedEvent
import com.zalesskyi.android.blechat.bluetooth.events.DisconnectedEvent

class PairingVM(application: Application) : BaseLifecycleViewModel(application) {

    val advertisingNotSupportedLD = MutableLiveData<Unit>()

    val connectedLD = MutableLiveData<Unit>()

    val disconnectedLD = MutableLiveData<Unit>()

    fun handleBluetoothEvents() {
        handleAdvertisingNotSupportedEvent()
        handleConnectedEvent()
        handleDisconnectedEvent()
    }

    private fun handleAdvertisingNotSupportedEvent() {
        RxBus.filter(AdvertisingNotSupportedEvent::class.java)
            .map { Unit }
            .doAsync(advertisingNotSupportedLD)
    }

    private fun handleConnectedEvent() {
        RxBus.filter(ConnectedEvent::class.java)
            .map { Unit }
            .doAsync(connectedLD)
    }

    private fun handleDisconnectedEvent() {
        RxBus.filter(DisconnectedEvent::class.java)
            .map { Unit }
            .doAsync(disconnectedLD)
    }
}