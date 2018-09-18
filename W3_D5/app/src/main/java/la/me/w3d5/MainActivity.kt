package la.me.w3d5

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.push_to_talk_btn
import kotlinx.android.synthetic.main.activity_main.push_to_talk_img

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    private var FILE_DIR: String? = null
    private var recorder: MiniRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
    }

    private fun checkPermissions() {
        var permissionCheck: Int = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_REQUESTS)
            return
        }

        permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUESTS)
            return
        }

        setup()
    }

    private fun setup() {
        FILE_DIR = externalCacheDir!!.path
        val file = File(FILE_DIR!!)
        if (!file.exists()) file.mkdir()

        recorder = MiniRecorder()

        push_to_talk_btn.setOnTouchListener(View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    recorder!!.isEnable = true
                    push_to_talk_img.setImageResource(R.drawable.ic_mic)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (recorder!!.isEnable) {
                        recorder!!.isEnable = false
                        saveFile()
                    }
                    push_to_talk_img.setImageResource(R.drawable.ic_mic_off)
                    return@OnTouchListener true
                }
            }
            false
        })

        recorder!!.resume()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUESTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (recorder != null)
            recorder!!.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recorder != null)
            recorder!!.release()
    }

    private fun saveFile() {
        val rawBuffer = ByteBuffer.allocate(recorder!!.bufferSize)
        recorder!!.getBuffer(rawBuffer)

        try {
            var file = File(FILE_DIR, "example.pcm")
            var outputStream: DataOutputStream

            /* save raw audio data (pcm) */
            outputStream = DataOutputStream(FileOutputStream(file))
            outputStream.write(rawBuffer.array(), 0, rawBuffer.position())
            outputStream.close()

            /* save wave file (wave header + pcm) */
            file = File(FILE_DIR, "example.wav")
            outputStream = DataOutputStream(FileOutputStream(file))
            WaveHeaderWriter.rawToWave(1, recorder!!.currentRate, 16, rawBuffer, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        recorder!!.clearBuffer()
    }

    companion object {

        private val PERMISSIONS_REQUESTS = 1
    }
}
