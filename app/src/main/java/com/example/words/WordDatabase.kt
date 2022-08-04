package com.example.words

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun getDatabase(context: Context) =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    WordDatabase::class.java,
                    "word_database"
                ).build().also {
                    INSTANCE = it
                }
            }
    }

    abstract fun getWordDao(): WordDao
}