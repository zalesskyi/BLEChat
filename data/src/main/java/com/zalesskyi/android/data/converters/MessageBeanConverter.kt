package com.zalesskyi.android.data.converters

import com.zalesskyi.android.data.beans.MessageBean
import com.zalesskyi.android.data.converters.base.BaseConverter
import com.zalesskyi.android.data.converters.base.Converter
import com.zalesskyi.android.data.models.Message
import com.zalesskyi.android.data.models.MessageModel
import com.zalesskyi.android.domain.models.MessageStatus
import org.joda.time.DateTime

interface MessageBeanConverter : Converter<MessageBean, Message>

class MessageBeanConverterImpl: BaseConverter<MessageBean, Message>(), MessageBeanConverter {

    override fun processConvertInToOut(inObject: MessageBean): Message? = inObject.run {
        MessageModel(id,
            text,
            DateTime.parse(date),
            MessageStatus.byValue(status))
    }

    override fun processConvertOutToIn(outObject: Message): MessageBean? = outObject.run {
        MessageBean(id, text, date?.toString(), status())
    }
}