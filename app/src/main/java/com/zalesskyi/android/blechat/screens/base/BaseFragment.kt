package com.zalesskyi.android.blechat.screens.base

import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleFragment
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_core.ui.NO_ID
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TITLE
import com.zalesskyi.android.blechat.utils.EMPTY_STRING

abstract class BaseFragment<VM : BaseLifecycleViewModel> : BaseLifecycleFragment<VM>() {

    override var endpoint = EMPTY_STRING

    override var versionName = EMPTY_STRING

    override fun getEndPointTextViewId() = NO_ID

    override fun getScreenTitle() = NO_TITLE

    override fun getToolbarId() = NO_ID

    override fun getVersionsLayoutId() = NO_ID

    override fun getVersionsTextViewId() = NO_ID

    override fun hasToolbar() = false

    override fun isDebug() = false

    override fun showBlockBackAlert() = Unit
}