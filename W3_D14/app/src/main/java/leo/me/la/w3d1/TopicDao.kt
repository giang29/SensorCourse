package leo.me.la.w3d1

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface TopicDao {
    @Insert
    fun insert(topic: Topic)

    @Query("SELECT * from topic ORDER BY topic ASC")
    fun getAllTopics(): LiveData<List<Topic>>

    @Query("SELECT topic from topic WHERE id = :id")
    fun getTopic(id: Long): String
}
