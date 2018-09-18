package leo.me.la.w3d1

import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.d_topic.button
import kotlinx.android.synthetic.main.d_topic.button2
import kotlinx.android.synthetic.main.d_topic.editText


class NewTopicDialog(
    c: Activity,
    private val topicViewModel: ViewModel
) : Dialog(c), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.d_topic)
        button.setOnClickListener(this)
        button2.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                editText.text.toString().run {
                    if (isNotEmpty()) {
                        topicViewModel.insert(
                            Topic(topic = editText.text.toString())
                        )
                        this@NewTopicDialog.dismiss()
                    }
                }
            }
            R.id.button2 -> dismiss()
        }
        dismiss()
    }
}
