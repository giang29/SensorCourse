package leo.me.la.w3d1

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordVH>() {
    var wordList: List<Word> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WordVH {
        return WordVH(
            LayoutInflater.from(p0.context).inflate(R.layout.i_word, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    override fun onBindViewHolder(p0: WordVH, p1: Int) {
        p0.bind(wordList[p1])
    }

    class WordVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(word: Word) {
            (itemView as TextView).text = word.word
        }
    }
}
