package leo.me.la.w2d2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_graph.graph
import kotlinx.android.synthetic.main.activity_graph.heartrate
import java.util.Date
import leo.me.la.w2d2.R.id.graph
import com.jjoe64.graphview.Viewport



private val HEART_RATE_MEASUREMENT_CHAR_UUID = 0x2A37.toUUID()
private val CLIENT_CHARACTERISTIC_CONFIG_UUID = 0x2902.toUUID()

class GraphActivity : AppCompatActivity() {

    private val series = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        graph.addSeries(series)

        intent.extras?.getParcelable<BluetoothDevice>("bd")
            ?.apply {
                connectGatt(this@GraphActivity, false, object : BluetoothGattCallback() {
                    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                        super.onConnectionStateChange(gatt, status, newState)
                        if (newState == BluetoothProfile.STATE_CONNECTED)
                            gatt.discoverServices();
                    }

                    @SuppressLint("SetTextI18n")
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
                            series.appendData(DataPoint(Date(), heartRate.toDouble()), false, 500)
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
    }
}
