package leo.me.la.w3d1

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val topicRoomDatabase: MyRoomDatabase = MyRoomDatabase.getDatabase(application)

    fun insert(topic: Topic) {
        topicRoomDatabase.topicDao().insert(topic)
    }

    fun insert(word: Word) {
        topicRoomDatabase.wordDao().insert(word)
    }

    val allTopics = topicRoomDatabase.topicDao().getAllTopics()

    fun allTopicWords(id: Long) = topicRoomDatabase.wordDao().getAllTopicWords(id)

    fun topic(id: Long) = topicRoomDatabase.topicDao().getTopic(id)
}
