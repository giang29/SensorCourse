package leo.me.la.lab3

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal class PresidentAdapter(
    private val onClickListener: (President) -> Unit,
    private val onLongClickListener: (String) -> Unit
) : RecyclerView.Adapter<PresidentAdapter.PresidentVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresidentVH {
        return PresidentVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.i_president,
                parent,
                false
            )
        ).also { vh ->
            vh.itemView.apply {
                setOnClickListener {
                    onClickListener(Data.presidents[vh.adapterPosition])
                }
                setOnLongClickListener {
                    onLongClickListener(Data.presidents[vh.adapterPosition].name)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Data.presidents.size
    }

    override fun onBindViewHolder(vh: PresidentVH, position: Int) {
        vh.bind(Data.presidents[position])
    }

    class PresidentVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val name : TextView = itemView.findViewById(R.id.tv_name)
        private val start : TextView = itemView.findViewById(R.id.tv_start)
        private val end : TextView = itemView.findViewById(R.id.tv_end)

        fun bind(p: President) {
            name.text = p.name
            start.text = p.start.toString()
            end.text = p.end.toString()
        }
    }
}
