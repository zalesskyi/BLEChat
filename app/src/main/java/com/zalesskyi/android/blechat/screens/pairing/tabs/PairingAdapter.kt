package com.zalesskyi.android.blechat.screens.pairing.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import com.zalesskyi.android.blechat.screens.pairing.connecting.PairingFragment
import com.zalesskyi.android.blechat.utils.getStringApp

class PairingAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val PAIRING_COUNT = 2
        private const val ADVERTISING_POSITION = 0
        private const val SCANNING_POSITION = 1
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            ADVERTISING_POSITION -> PairingFragment.newInstance(BleMode.PERIPHERAL)
            SCANNING_POSITION -> PairingFragment.newInstance(BleMode.CENTRAL)
            else -> throw IllegalStateException("Unexpected case")
        }

    override fun getCount() = PAIRING_COUNT

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            ADVERTISING_POSITION -> getStringApp(R.string.peripheral_mode)
            SCANNING_POSITION -> getStringApp(R.string.central_mode)
            else -> throw IllegalStateException("Unexpected case")
        }
}