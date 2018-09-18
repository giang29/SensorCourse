package leo.me.la.w3d1

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.room.Room


@Database(entities = [Topic::class, Word::class], version = 2)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao

    abstract fun wordDao(): WordDao

    companion object {
        private var instance: MyRoomDatabase? = null
        fun getDatabase(context: Context): MyRoomDatabase {
            synchronized(MyRoomDatabase::class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyRoomDatabase::class.java,
                        "lab_database"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance!!
        }
    }
}
