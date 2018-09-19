package leo.me.la.w3d5

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.button


class MainActivity : AppCompatActivity() {
    private val shortByte = mutableListOf<Short>()
    private val recordingThread by lazy {
        Recorder {
            shortByte.addAll(it.toList())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                1
            )
        } else {
            setup()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setup()
        } else {
            finish()
        }
    }

    private fun setup() {
        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    recordingThread.startRecording()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    recordingThread.stopRecording()
                    Thread().run {
                        Thread.sleep(500)
                        val t = Player(
                            shortByte.toShortArray()
                        )
                        runOnUiThread {
                            t.startPlayback()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }
}
