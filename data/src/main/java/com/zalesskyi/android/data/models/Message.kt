package com.zalesskyi.android.data.models

import com.zalesskyi.android.domain.models.MessageStatus
import org.joda.time.DateTime

interface Message {
    val id: String?
    val text: String?
    val date: DateTime?
    val status: MessageStatus
}

data class MessageModel(override val id: String?,
                        override val text: String?,
                        override val date: DateTime?,
                        override val status: MessageStatus
) : Message