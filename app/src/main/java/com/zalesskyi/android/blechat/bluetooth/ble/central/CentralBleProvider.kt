package com.zalesskyi.android.blechat.bluetooth.ble.central

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.*
import android.util.Log
import com.zalesskyi.android.blechat.CHARACTERISTIC_MESSAGE_UUID
import com.zalesskyi.android.blechat.CHAT_SERVICE_UUID
import com.zalesskyi.android.blechat.PAIRING_DELAY_MS
import com.zalesskyi.android.blechat.SCAN_REPORT_DELAY
import com.zalesskyi.android.blechat.bluetooth.ble.*
import java.lang.ref.WeakReference

class CentralBleProvider(private val context: Context,
                         callback: BleProviderCallback) : BleProvider {

    companion object {

        private val TAG = CentralBleProvider::class.java.simpleName
    }

    private val callbackRef = WeakReference(callback)

    private var currentGatt: BluetoothGatt? = null

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var isDisconnectApproved = false

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            stopLookForConnection()
            gatt?.run {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        if (status == GATT_SUCCESS) {
                            currentGatt = this
                            discoverServices()
                        }
                        Log.i(TAG, "state connected $status")
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.i(TAG, "state disconnected $status")
                        currentGatt = null
                        close()
                        callbackRef.get()?.onConnectionLost(
                              NO_ERROR.takeIf { isDisconnectApproved } ?: GATT_ERROR)
                    }
                    else -> Unit
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status != GATT_SUCCESS) {
                Log.i(TAG, "services not discovered")
                return
            }
            Log.i(TAG, "services: ${gatt?.services?.map { it.uuid }}")
            gatt?.getService(CHAT_SERVICE_UUID)?.let {
                it.getCharacteristic(CHARACTERISTIC_MESSAGE_UUID)?.let { messageCharacteristic ->
                    currentGatt?.setCharacteristicNotification(messageCharacteristic, true)
                }
                Log.i(TAG, "Chat service found")
            } ?: callbackRef.get()?.onConnectionFail(GATT_SERVICE_NOT_FOUND)
            callbackRef.get()?.onConnectionEstablished()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?,
                                             characteristic: BluetoothGattCharacteristic?) {
            Log.i(TAG, "characteristic changed")
            characteristic?.value?.let { message ->
                callbackRef.get()?.onMessageArrived(message)
            }
        }
    }

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                Log.i(TAG, "new scan result: ${device.name}")
                device.onBond {
                    device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            callbackRef.get()?.onConnectionFail(SCAN_FAILED)
        }
    }

    override fun lookForConnection() {
        isDisconnectApproved = false
        val scanSettings = ScanSettings.Builder()
              .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
              .apply {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                      setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
              }
              .setReportDelay(SCAN_REPORT_DELAY)
              .build()
        val scanFilter = ScanFilter.Builder()
              .setServiceUuid(ParcelUuid(CHAT_SERVICE_UUID))
              .build()
        bluetoothAdapter.run {
            bluetoothLeScanner?.takeIf { isEnabled }?.startScan(listOf(scanFilter), scanSettings, scanCallback)
        }
    }

    override fun stopLookForConnection() {
        bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
    }

    override fun disconnect() {
        isDisconnectApproved = true
        currentGatt?.run {
            clearServicesCache()
            disconnect()
        }
    }

    override fun sendMessage(message: ByteArray) {
        val characteristic = currentGatt?.getService(CHAT_SERVICE_UUID)
              ?.getCharacteristic(CHARACTERISTIC_MESSAGE_UUID)

        currentGatt?.setCharacteristicNotification(characteristic, true)
        characteristic?.value = message
        currentGatt?.writeCharacteristic(characteristic)
    }

    private fun BluetoothDevice.onBond(action: () -> Unit) {
        Log.i(TAG, "device bonded: $bondState")
        when {
            bondState == BluetoothDevice.BOND_NONE -> action()
            bondState == BluetoothDevice.BOND_BONDED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> action()
            else -> Handler().postDelayed({ onBond(action) }, PAIRING_DELAY_MS)
        }
    }

    private fun BluetoothGatt.clearServicesCache() {
        try {
            javaClass.getMethod("refresh").invoke(this)
        } catch (exc: NoSuchMethodException) {
            Log.i(TAG, "error while clearing cache")
        }
    }
}