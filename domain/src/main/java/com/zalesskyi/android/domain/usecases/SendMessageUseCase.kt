package com.zalesskyi.android.domain.usecases

import com.zalesskyi.android.data.gateways.BluetoothGateway
import com.zalesskyi.android.data.gateways.BluetoothGatewayImpl
import com.zalesskyi.android.data.models.Message
import io.reactivex.Single

interface SendMessageUseCase {

    fun sendMessage(message: Message): Single<Message>
}

class SendMessageUseCaseImpl : SendMessageUseCase {

    private val gateway: BluetoothGateway = BluetoothGatewayImpl

    override fun sendMessage(message: Message): Single<Message> =
        Single.just(message)
            .flatMap { gateway.sendMessage(it) }
}