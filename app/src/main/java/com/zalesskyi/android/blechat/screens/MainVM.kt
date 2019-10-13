package com.zalesskyi.android.blechat.screens

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_rx_bus.RxBus
import com.zalesskyi.android.blechat.bluetooth.events.*

class MainVM(application: Application) : BaseLifecycleViewModel(application) {

    val messageReceivedLD = MutableLiveData<String>()

    val advertisingNotSupportedLD = MutableLiveData<Unit>()

    val connectedLD = MutableLiveData<Unit>()

    val disconnectedLD = MutableLiveData<Unit>()

    fun handleBluetoothEvents() {
        handleMessageReceivedEvent()
        handleAdvertisingNotSupportedEvent()
        handleConnectedEvent()
        handleDisconnectedEvent()
    }

    fun sendMessage(message: String) {
        RxBus.send(MessageSentEvent(message))
    }

    private fun handleMessageReceivedEvent() {
        RxBus.filter(MessageReceivedEvent::class.java)
            .map { it.message }
            .doAsync(messageReceivedLD)
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