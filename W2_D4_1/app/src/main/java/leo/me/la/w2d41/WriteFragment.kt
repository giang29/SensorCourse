package leo.me.la.w2d41

import android.content.Context.MODE_APPEND
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_write.input
import kotlinx.android.synthetic.main.fragment_write.save

const val SHAREFILE = "demo.txt"

class WriteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        save.setOnClickListener {
            activity!!.openFileOutput(SHAREFILE, MODE_APPEND)
                .apply {
                    write("\n${input.text}".toByteArray())
                    input.text.clear()
                }
        }
    }
}
