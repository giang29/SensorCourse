package leo.me.la.w3d1

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TopicAdapter(
    private val onClickListener : (Long) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicVH>() {

    var topics: List<Topic> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TopicVH {
        return TopicVH(
            LayoutInflater.from(p0.context).inflate(R.layout.i_topic, p0, false)
        ).also {
            it.itemView.setOnClickListener { _ ->
                onClickListener(topics[it.adapterPosition].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(p0: TopicVH, p1: Int) {
        p0.bind(topics[p1])
    }


    class TopicVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(topic: Topic) {
            (itemView as TextView).text = topic.topic
        }
    }
}
