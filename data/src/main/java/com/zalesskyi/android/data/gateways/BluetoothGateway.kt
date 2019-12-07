package com.zalesskyi.android.data.gateways

import com.cleveroad.bootstrap.kotlin_rx_bus.RxBus
import com.fasterxml.jackson.databind.ObjectMapper
import com.zalesskyi.android.data.beans.MessageBean
import com.zalesskyi.android.data.converters.MessageBeanConverter
import com.zalesskyi.android.data.converters.MessageBeanConverterImpl
import com.zalesskyi.android.data.models.Message
import com.zalesskyi.android.data.models.events.MessageReceivedEvent
import com.zalesskyi.android.data.models.events.MessageSentEvent
import io.reactivex.Flowable
import io.reactivex.Single

interface BluetoothGateway {

    fun sendMessage(message: Message): Single<Message>

    fun receiveMessage(): Flowable<Message>
}

object BluetoothGatewayImpl : BluetoothGateway {

    private val converter: MessageBeanConverter = MessageBeanConverterImpl()

    private val mapper = ObjectMapper()

    override fun sendMessage(message: Message): Single<Message> =
        Single.just(message)
            .compose(converter.singleOUTtoIN())
            .map { mapper.writeValueAsString(it) }
            .doOnSuccess { RxBus.send(MessageSentEvent(it)) }
            .map { message }

    override fun receiveMessage(): Flowable<Message> =
        RxBus.filter(MessageReceivedEvent::class.java)
            .map { it.message }
            .map { mapper.readValue(it, MessageBean::class.java) }
            .compose(converter.flowINtoOUT())
}