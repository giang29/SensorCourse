package la.me.leo.lab1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.textView
import kotlinx.android.synthetic.main.activity_main.textView2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.getString("tv2")?.let {
            textView2.text = it
        }
        button.setOnClickListener {
            textView2.text = getString(
                if (textView2.text == getString(R.string.hello_world))
                    R.string.goodbye_summer
                else
                    R.string.hello_world
            )
        }
        fab.setOnClickListener {
            textView.text = (textView.text.toString().toInt() + 1).toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.also {
            it.putString("tv2", textView2.text.toString())
        })
    }
}
