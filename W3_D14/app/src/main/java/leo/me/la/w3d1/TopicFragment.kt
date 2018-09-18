package leo.me.la.w3d1

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
class TopicFragment : Fragment() {
    private var activity : MainActivity? = null
    private val adapter = TopicAdapter {
        activity?.showWord(it)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as RecyclerView).apply {
            adapter = this@TopicFragment.adapter
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
        }
    }

    fun update(topics: List<Topic>) {
        adapter.topics = topics
    }
}
