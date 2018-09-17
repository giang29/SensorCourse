package leo.me.la.lab3

import android.os.Bundle
import android.support.v4.app.FragmentActivity


internal class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun updateTextView(text: String, hit: Int) {
        (supportFragmentManager.findFragmentById(R.id.fragment_text) as? InfoFragment)
            ?.updateText(text, hit)
    }
}
