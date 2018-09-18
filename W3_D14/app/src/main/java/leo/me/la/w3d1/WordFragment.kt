package leo.me.la.w3d1

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_word.buttonAdd
import kotlinx.android.synthetic.main.fragment_word.rcvWord

private const val ARG_TOPIC_ID = "topic"

class WordFragment : Fragment() {
    private val topicId by lazy {
        arguments!!.getLong(ARG_TOPIC_ID)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ViewModel::class.java)
    }

    private val wordAdapter = WordAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.allTopicWords(topicId).observe(
            this,
            Observer {
                it?.run {
                    wordAdapter.wordList = this
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonAdd.apply {
            text = viewModel.topic(topicId)
            setOnClickListener {
                NewWordDialog(topicId, activity!!, viewModel).show()
            }
        }
        rcvWord.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(context!!, VERTICAL, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_word, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(topicId: Long) =
            WordFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TOPIC_ID, topicId)
                }
            }
    }
}
