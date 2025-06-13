package com.example.team.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DiaryEntity::class, VocabEntity::class, ChatEntity::class], version = 4)
abstract class AikuDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun vocabDao(): VocabDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AikuDatabase? = null

        fun getDatabase(context: Context): AikuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AikuDatabase::class.java,
                    "aiku_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
