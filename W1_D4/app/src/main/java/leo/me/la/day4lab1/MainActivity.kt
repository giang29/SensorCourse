package leo.me.la.day4lab1

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import kotlinx.android.synthetic.main.activity_main.text
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isOnline())
            Thread(
                Client(
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            if (msg.what == 0) {
                                text.text = msg.obj.toString()
                            }
                        }
                    }
                )
            ).start()
    }

    private fun isOnline(): Boolean {
        return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo != null
    }

    private class Client(private val handler: Handler) : Runnable {
        override fun run() {
            try {
                (URL("http://www.greens.org/about/software/editor.txt").openConnection() as? HttpURLConnection)
                    ?.apply {
                        requestMethod = "GET"
                    }
                    ?.inputStream
                    ?.bufferedReader()
                    ?.use {
                        it.readText()
                    }?.let {
                        handler.obtainMessage()
                            .apply {
                                what = 0
                                obj = it
                            }
                            .also { mes ->
                                handler.sendMessage(mes)
                            }
                    }
            } catch (ignored: Exception) {
            }
        }
    }
}
