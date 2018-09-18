package leo.me.la.w3d1

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WordDao {
    @Insert
    fun insert(word: Word)

    @Query("SELECT * from word WHERE topicId = :id")
    fun getAllTopicWords(id: Long) : LiveData<List<Word>>
}
