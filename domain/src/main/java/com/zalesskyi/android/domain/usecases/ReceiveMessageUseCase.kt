package com.zalesskyi.android.domain.usecases

import com.zalesskyi.android.data.gateways.BluetoothGateway
import com.zalesskyi.android.data.gateways.BluetoothGatewayImpl
import com.zalesskyi.android.data.models.Message
import io.reactivex.Flowable

interface ReceiveMessageUseCase {

    fun onMessageReceived(): Flowable<Message>
}

class ReceiveMessageUseCaseImpl : ReceiveMessageUseCase {

    private val gateway: BluetoothGateway = BluetoothGatewayImpl

    override fun onMessageReceived(): Flowable<Message> = gateway.receiveMessage()
}