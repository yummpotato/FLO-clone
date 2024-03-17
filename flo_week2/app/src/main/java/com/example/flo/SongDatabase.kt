package com.example.flo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.week1.Song

@Database(entities = [Song::class, Album::class, User::class, Like::class], version = 1)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun albumDao(): AlbumDao

    abstract fun userDao(): UserDao

    companion object {
        private var instance: SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SongDatabase? {
            if(instance == null)
            {
                synchronized(SongDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }
            }
            return instance
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. SongTable 테이블을 변경하여 albumIdx 컬럼을 추가합니다.
                database.execSQL("ALTER TABLE SongTable ADD COLUMN albumIdx INTEGER DEFAULT 0")

                // 2. 기존 데이터를 AlbumTable에서 가져와서 albumIdx 값을 설정합니다.
                database.execSQL("UPDATE SongTable SET albumIdx = (SELECT id FROM AlbumTable WHERE AlbumTable.id = SongTable.albumIdx)")

            }
        }
    }
}