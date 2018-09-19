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
                BleWrapper(this@GraphActivity, this.address)
                    .apply {
                        connect(false)
                        addListener(object : BleWrapper.BleCallback {
                            override fun onDeviceReady(gatt: BluetoothGatt) {
                                getNotifications(gatt, HEART_RATE_SERVICE_UUID, HEART_RATE_MEASUREMENT_CHAR_UUID)
                            }

                            override fun onDeviceDisconnected() {}

                            @SuppressLint("SetTextI18n")
                            override fun onNotify(characteristic: BluetoothGattCharacteristic) {
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
                        })
                    }
            }
    }
}
