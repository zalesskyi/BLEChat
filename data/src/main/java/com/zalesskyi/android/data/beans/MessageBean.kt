package com.zalesskyi.android.data.beans

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageBean(@JsonProperty("id") val id: String?,
                       @JsonProperty("text") val text: String?,
                       @JsonProperty("date") val date: String?,
                       @JsonProperty("status") val status: String?)