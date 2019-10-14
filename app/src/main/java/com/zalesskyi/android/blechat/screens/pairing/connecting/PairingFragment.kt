package com.zalesskyi.android.blechat.screens.pairing.connecting

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_core.utils.misc.bindInterfaceOrThrow
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.show
import com.zalesskyi.android.blechat.R
import com.zalesskyi.android.blechat.bluetooth.ble.BleMode
import com.zalesskyi.android.blechat.screens.base.BaseFragment
import com.zalesskyi.android.blechat.screens.pairing.PairingCallback
import com.zalesskyi.android.blechat.utils.FragmentArgumentDelegate
import kotlinx.android.synthetic.main.fragment_pairing.*
import org.jetbrains.anko.textResource

class PairingFragment : BaseFragment<PairingVM>() {

    companion object {

        fun newInstance(mode: BleMode) = PairingFragment().apply {
            connectMode = mode
        }
    }

    override val layoutId = R.layout.fragment_pairing

    override val viewModelClass = PairingVM::class.java

    private var connectMode by FragmentArgumentDelegate<BleMode>()

    private var callback: PairingCallback? = null

    private var connectionAnimator: Animator? = null

    private var connectionShineAnimator: Animator? = null

    private var isConnecting = false

    private val advertisingNotSupportedObserver = Observer<Unit> {
        tvDesc.textResource = R.string.advertising_not_supported
        stopConnecting()
    }

    private val connectedObserver = Observer<Unit> {
        stopConnecting()
        tvDesc.textResource = R.string.connected
        callback?.openChatScreen()
    }

    override fun observeLiveData() = viewModel.run {
        advertisingNotSupportedLD.observe(this@PairingFragment, advertisingNotSupportedObserver)
        connectedLD.observe(this@PairingFragment, connectedObserver)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = bindInterfaceOrThrow(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.handleBluetoothEvents()
        setupUi()
    }

    override fun onResume() {
        super.onResume()
        tvDesc.textResource = when (connectMode) {
            BleMode.PERIPHERAL -> R.string.start_advertising
            else -> R.string.start_scanning
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    private fun setupUi() {
        ivConnect.setOnClickListener {
            if (isConnecting) stopConnecting() else startConnecting()
        }
    }

    private fun startConnecting() {
        startConnectionAnim()
        tvDesc.textResource = when (connectMode) {
            BleMode.PERIPHERAL -> R.string.advertising_in_process
            else -> R.string.scanning_in_process
        }
        connectMode?.let { callback?.connect(it) }
        isConnecting = true
    }

    private fun stopConnecting() {
        stopConnectionAnim()
        callback?.stopLookForConnection()
        isConnecting = false
    }

    private fun startConnectionAnim() {
        vConnectShine?.show()
        connectionAnimator = AnimatorInflater.loadAnimator(context, R.animator.increase)?.apply {
            setTarget(ivConnect)
            start()
        }
        connectionShineAnimator = AnimatorInflater.loadAnimator(context, R.animator.shine)?.apply {
            setTarget(vConnectShine)
            start()
        }
    }

    private fun stopConnectionAnim() {
        connectionAnimator?.end()
        connectionShineAnimator?.end()
        vConnectShine?.hide()

        connectionAnimator = null
        connectionShineAnimator = null
    }
}