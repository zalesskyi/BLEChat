package com.zalesskyi.android.blechat.screens.chat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.zalesskyi.android.data.models.Message
import com.zalesskyi.android.data.models.MessageModel
import com.zalesskyi.android.domain.models.MessageStatus
import com.zalesskyi.android.domain.usecases.SendMessageUseCaseImpl
import io.reactivex.Single
import org.joda.time.DateTime
import java.util.*

class ChatVM(application: Application) : BaseLifecycleViewModel(application) {

    val messageLD = MutableLiveData<Message>()

    private val sendMessageUseCase = SendMessageUseCaseImpl()

    fun sendMessage(text: String) {
        Single.just(MessageModel(UUID.randomUUID().toString(), text, DateTime.now(), MessageStatus.SENT))
            .flatMap { sendMessageUseCase.sendMessage(it) }
            .doAsync(messageLD)
    }
}