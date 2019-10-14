package com.zalesskyi.android.blechat.screens.pairing.tabs

import android.os.Bundle
import android.view.View
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_pairing_tabs.*

class PairingTabsFragment : BaseFragment<PairingTabsVM>() {

    companion object {

        fun newInstance() = PairingTabsFragment()
    }

    override val layoutId = R.layout.fragment_pairing_tabs

    override val viewModelClass = PairingTabsVM::class.java

    override fun observeLiveData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        vpPairing.adapter = PairingAdapter(childFragmentManager)
        tlPairing.setupWithViewPager(vpPairing)
    }
}