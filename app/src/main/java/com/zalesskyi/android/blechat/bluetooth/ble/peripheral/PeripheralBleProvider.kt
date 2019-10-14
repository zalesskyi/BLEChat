package com.zalesskyi.android.blechat.bluetooth.ble.peripheral

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.zalesskyi.android.blechat.ADVERTISE_TIMEOUT_MILLIS
import com.zalesskyi.android.blechat.CHARACTERISTIC_MESSAGE_UUID
import com.zalesskyi.android.blechat.CHAT_SERVICE_UUID
import com.zalesskyi.android.blechat.bluetooth.ble.ADVERTISING_FAILED
import com.zalesskyi.android.blechat.bluetooth.ble.ADVERTISING_NOT_SUPPORTED
import com.zalesskyi.android.blechat.bluetooth.ble.BleProvider
import com.zalesskyi.android.blechat.bluetooth.ble.BleProviderCallback
import java.lang.ref.WeakReference

class PeripheralBleProvider(context: Context,
                            callback: BleProviderCallback) : BleProvider {

    companion object {

        private val TAG = PeripheralBleProvider::class.java.simpleName
    }

    private val callbackRef = WeakReference(callback)

    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var bluetoothManager = context
          .getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private var gattServer: BluetoothGattServer? = null

    private var chatGattService: BluetoothGattService? = null

    private var messageCharacteristic: BluetoothGattCharacteristic? = null

    private var connectedDevice: BluetoothDevice? = null

    private val advertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.i(TAG, "advertising is started")
        }

        override fun onStartFailure(errorCode: Int) {
            callbackRef.get()?.onConnectionFail(ADVERTISING_FAILED)
        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectedDevice = null
                    callbackRef.get()?.onConnectionLost(0)
                }
                BluetoothProfile.STATE_CONNECTED -> {
                    connectedDevice = device
                    callbackRef.get()?.onConnectionEstablished()
                }
            }
        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice?,
                                                  requestId: Int,
                                                  characteristic: BluetoothGattCharacteristic?,
                                                  preparedWrite: Boolean,
                                                  responseNeeded: Boolean,
                                                  offset: Int,
                                                  value: ByteArray?) {
            value?.takeIf { characteristic?.uuid == CHARACTERISTIC_MESSAGE_UUID }?.let {
                callbackRef.get()?.onMessageArrived(it)
                if (responseNeeded) {
                    gattServer?.sendResponse(device, requestId, GATT_SUCCESS, 0, null)
                }
            }
        }
    }

    init {
        chatGattService = createGattService()

        bluetoothManager?.openGattServer(context, gattServerCallback)?.also {
            gattServer = it
        }?.addService(chatGattService)
    }

    override fun lookForConnection() {
        if (bluetoothAdapter?.isMultipleAdvertisementSupported == false) {
            callbackRef.get()?.onConnectionFail(ADVERTISING_NOT_SUPPORTED)
            return
        }
        val settings = AdvertiseSettings.Builder()
              .setTimeout(ADVERTISE_TIMEOUT_MILLIS)
              .setConnectable(true)
              .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
              .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
              .build()

        val data = AdvertiseData.Builder()
              .setIncludeDeviceName(true)
              .addServiceUuid(ParcelUuid(CHAT_SERVICE_UUID))
              .build()

        bluetoothAdapter?.bluetoothLeAdvertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    override fun stopLookForConnection() {
        bluetoothAdapter?.bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }

    override fun disconnect() {
        gattServer?.run {
            removeService(chatGattService)
            close()
        }
    }

    override fun sendMessage(message: ByteArray) {
        connectedDevice?.let { device ->
            messageCharacteristic?.value = message
            gattServer?.notifyCharacteristicChanged(device, messageCharacteristic, false)
        }
    }

    private fun createGattService(): BluetoothGattService {
        val service = BluetoothGattService(CHAT_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        messageCharacteristic = BluetoothGattCharacteristic(
              CHARACTERISTIC_MESSAGE_UUID,
              BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
              BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        return service
    }
}