package com.zalesskyi.android.domain.models

enum class MessageStatus(val status: String) {

    SENT("SENT"),
    RECEIVED("RECEIVED"),
    UNKNOWN("UNKNOWN");

    operator fun invoke() = status

    companion object {
        fun byValue(value: String?) = values().firstOrNull { value == it.status } ?: UNKNOWN
    }
}