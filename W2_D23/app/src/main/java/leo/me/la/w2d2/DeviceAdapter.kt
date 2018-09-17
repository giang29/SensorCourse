package leo.me.la.w2d2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DeviceAdapter(
    private val devices: Map<String, Triple<BluetoothDevice, Int, Boolean>>,
    private val onClickListener: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVH {
        return DeviceVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.i_device, parent, false)
        ).also {
            it.itemView.setOnClickListener { _ ->
                onClickListener(devices.toList()[it.adapterPosition].second.first)
            }
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceVH, position: Int) {
        holder.bind(devices.toList()[position].second)
    }

    class DeviceVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val mac = itemView.findViewById<TextView>(R.id.mac)
        private val rssi = itemView.findViewById<TextView>(R.id.rssi)
        @SuppressLint("SetTextI18n")
        fun bind(bluetoothDevice: Triple<BluetoothDevice, Int, Boolean>) {
            name.text = bluetoothDevice.first.name
            mac.text = bluetoothDevice.first.address
            rssi.text = "${bluetoothDevice.second} dBm"
            itemView.alpha = if (bluetoothDevice.third) 1.0f else 0.4f
        }
    }
}
