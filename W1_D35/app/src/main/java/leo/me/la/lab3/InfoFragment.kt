package leo.me.la.lab3


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_info.textView
import kotlinx.android.synthetic.main.fragment_info.textView2

class InfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    @SuppressLint("SetTextI18n")
    fun updateText(text: String, hit: Int) {
        textView.text = text
        textView2.text = "Hits: $hit"
    }
}
