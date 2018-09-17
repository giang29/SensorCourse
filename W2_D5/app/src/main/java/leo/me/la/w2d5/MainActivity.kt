package leo.me.la.w2d5

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.button
import kotlinx.android.synthetic.main.activity_main.image
import java.io.File
import java.io.IOException

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_WRITE_FILE = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_FILE
                )
            } else {
                openCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_FILE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, recIntent: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            image.setImageBitmap(BitmapFactory.decodeFile(file!!.absolutePath))
            startActivity(
                Intent()
                    .apply {
                        action = Intent.ACTION_VIEW
                        flags = FLAG_GRANT_READ_URI_PERMISSION
                        setDataAndType(
                            FileProvider.getUriForFile(
                                this@MainActivity,
                                "la.me.leo.fileprovider",
                                file!!
                            ),
                            "image/*"
                        )
                    }
            )
        }
    }

    private var file: File? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = System.currentTimeMillis()
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName, // prefix
            ".jpg", // suffix
            storageDir    // directory
        ).also {
            file = it
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            try {
                createImageFile()
                    .also {
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(this, "la.me.leo.fileprovider", it)
                        )
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
                    }
            } catch (ex: IOException) {
            }
        }
    }
}
