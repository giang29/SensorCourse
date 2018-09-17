package leo.me.la.w2d2

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.buttonStart
import kotlinx.android.synthetic.main.activity_main.buttonStop
import kotlinx.android.synthetic.main.activity_main.heartrate
import kotlinx.android.synthetic.main.activity_main.rcv
import leo.me.la.w2d2.R.id.buttonStart
import leo.me.la.w2d2.R.id.buttonStop
import leo.me.la.w2d2.R.id.heartrate
import leo.me.la.w2d2.R.id.rcv
import java.util.UUID

private val HEART_RATE_SERVICE_UUID = 0x180D.toUUID()
private val HEART_RATE_MEASUREMENT_CHAR_UUID = 0x2A37.toUUID()
private val CLIENT_CHARACTERISTIC_CONFIG_UUID = 0x2902.toUUID()

class MainActivity : AppCompatActivity() {

    private val foundDevices = mutableMapOf<String, Triple<BluetoothDevice, Int, Boolean>>()
    private val adapter: DeviceAdapter = DeviceAdapter(foundDevices) {
        it.connectGatt(this, false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED)
                    gatt.discoverServices();
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)
                runOnUiThread {
                    val format = if (characteristic.properties and 0x01 != 0) {
                        BluetoothGattCharacteristic.FORMAT_UINT16
                    } else {
                        BluetoothGattCharacteristic.FORMAT_UINT8
                    }
                    val heartRate = characteristic.getIntValue(format, 1)
                    heartrate.text = "$heartRate bpm"
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
                gatt.getService(HEART_RATE_SERVICE_UUID)
                    ?.getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
                    ?.let { characteristics ->
                        gatt.apply {
                            writeDescriptor(
                                characteristics.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                                    .also { bdg ->
                                        bdg.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    }
                            )
                            setCharacteristicNotification(characteristics, true)
                        }
                    }
            }
        })
    }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val bluetoothCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (callbackType == CALLBACK_TYPE_ALL_MATCHES) {
                if (foundDevices[result.device.address] == null) {
                    foundDevices[result.device.address] = Triple(result.device, result.rssi, result.isConnectable)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { result ->
                if (foundDevices[result.device.address] == null) {
                    foundDevices[result.device.address] = Triple(result.device, result.rssi, result.isConnectable)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        rcv.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
        buttonStart.setOnClickListener { _ ->
            mBluetoothAdapter?.also {
                if (!it.isEnabled) {
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 291)
                } else {
                    scanBluetoothDevices()
                }
            }
        }
        buttonStop.setOnClickListener {
            mBluetoothAdapter?.bluetoothLeScanner?.stopScan(bluetoothCallback)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            291 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> scanBluetoothDevices()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBluetoothAdapter?.bluetoothLeScanner?.stopScan(bluetoothCallback)
    }

    private fun scanBluetoothDevices() {
        if (isLocationPermissionGranted())
            mBluetoothAdapter?.bluetoothLeScanner?.startScan(
                listOf(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(HEART_RATE_SERVICE_UUID))
                        .build()
                ),
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build(),
                bluetoothCallback
            )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1111 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkLocationPermission()) {
                        scanBluetoothDevices()
                    }
                }
                return
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        if (!checkLocationPermission()) {
            // Should we show an explanation?
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This app requires location permissions")
                    .setPositiveButton("ok") { dialog, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1111
                        )
                        dialog.dismiss()
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1111
                )
            }
            return false
        } else {
            return true
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}

private fun Int.toUUID(): UUID {
    val MSB = 0x0000000000001000L
    val LSB = -0x7fffff7fa064cb05L
    val value = (this and -0x1).toLong()
    return UUID(MSB or (value shl 32), LSB)
}
