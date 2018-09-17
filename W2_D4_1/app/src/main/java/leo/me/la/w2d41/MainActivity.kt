package leo.me.la.w2d41

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.read
import kotlinx.android.synthetic.main.activity_main.write

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, WriteFragment())
            .commit()
        write.isClickable = false
        write.setOnClickListener {
            read.isClickable = true
            write.isClickable = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WriteFragment())
                .commit()
        }
        read.setOnClickListener {
            write.isClickable = true
            read.isClickable = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReadFragment())
                .commit()
        }
    }
}
