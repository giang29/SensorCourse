package leo.me.la.w3d1

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.Menu
import kotlinx.android.synthetic.main.activity_main.fab
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MenuItem


class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.allTopics
            .observe(
                this,
                Observer {
                    it?.let { topics ->
                        (supportFragmentManager.findFragmentById(R.id.topics) as TopicFragment)
                            .update(topics)
                    }
                }
            )

        fab.setOnClickListener {
            NewTopicDialog(
                this,
                viewModel
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getString(applicationContext.getString(R.string.preference_color), "#FF4081")
            ?.let {
                ColorStateList.valueOf(Color.parseColor(it))
            }
            ?.run(fab::setBackgroundTintList)

    }

    fun showWord(id: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.words, WordFragment.newInstance(id))
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(
                this, SettingsActivity::class.java
            ))
        }
        return super.onOptionsItemSelected(item)
    }
}
