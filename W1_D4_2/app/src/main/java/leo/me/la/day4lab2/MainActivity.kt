package leo.me.la.day4lab2

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.net.URL
import android.graphics.BitmapFactory
import kotlinx.android.synthetic.main.activity_main.image
import java.net.HttpURLConnection


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DownloadImagesTask{
            image.setImageBitmap(it)
        }.execute(URL("https://www.freepngimg.com/download/android/31165-8-android-hd.png"))
    }

    class DownloadImagesTask(private val callback: (Bitmap) -> Unit) : AsyncTask<URL, Void, Bitmap>() {
        override fun onPostExecute(result: Bitmap?) {
            result?.let {
                callback(it)
            }
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: URL): Bitmap? {
            return downloadImage(params[0])
        }

        private fun downloadImage(url: URL): Bitmap? {
            return try {
                (url.openConnection() as? HttpURLConnection)
                    ?.inputStream
                    ?.let {
                        BitmapFactory.decodeStream(it)
                    }

            } catch (e: Throwable) {
                null
            }
        }
    }
}
