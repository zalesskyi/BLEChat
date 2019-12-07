package com.zalesskyi.android.blechat.screens.chat

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.screens.base.BaseFragment
import com.zalesskyi.android.data.models.Message
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : BaseFragment<ChatVM>() {

    companion object {

        fun newInstance() = ChatFragment()
    }

    override val layoutId = R.layout.fragment_chat
    override val viewModelClass = ChatVM::class.java

    private val messageObserver = Observer<Message> {

    }

    private var adapter: MessagesAdapter? = null

    override fun observeLiveData() {
        viewModel.messageLD.observe(this@ChatFragment, messageObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivSend.setOnClickListener {
            etMessage.text.takeIf { it.isNotBlank() }?.toString()?.let { message ->
                viewModel.sendMessage(message)
            }
        }
    }
}