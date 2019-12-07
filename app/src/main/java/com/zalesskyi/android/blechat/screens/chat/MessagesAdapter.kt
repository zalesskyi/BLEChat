package com.zalesskyi.android.blechat.screens.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleveroad.bootstrap.kotlin_core.ui.adapter.BaseRecyclerViewAdapter
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.data.models.Message
import org.jetbrains.anko.find

class MessagesAdapter(context: Context)
    : BaseRecyclerViewAdapter<Message, MessagesAdapter.MessageHolder>(context, listOf()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageHolder.newInstance(inflater, parent)

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(data[position])
    }

    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun newInstance(inflater: LayoutInflater, parent: ViewGroup) =
                MessageHolder(inflater.inflate(R.layout.item_message, parent, false))
        }

        private val tvMessage: TextView = itemView.find(R.id.tvMessage)

        fun bind(message: Message) {
            tvMessage.text = message.text
        }
    }
}