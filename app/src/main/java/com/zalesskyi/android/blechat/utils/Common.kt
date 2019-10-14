package com.zalesskyi.android.blechat.utils

import androidx.annotation.StringRes
import com.zalesskyi.android.blechat.BleChatApp

fun getStringApp(@StringRes res: Int) = BleChatApp.instance.getString(res)