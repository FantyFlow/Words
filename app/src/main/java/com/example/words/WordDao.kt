package com.example.words

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insertWords(vararg words: Word)

    @Update
    suspend fun updateWords(vararg words: Word)

    @Delete
    suspend fun deleteWords(vararg words: Word)

    @Query("DELETE FROM Word")
    suspend fun deleteAllWords()

    @Query("SELECT * FROM Word ORDER BY id DESC")
    fun getAllWordsLive(): Flow<List<Word>>

    @Query("SELECT * FROM Word WHERE english_word LIKE :pattern ORDER BY id DESC")
    fun findWordsWithPattern(pattern: String): Flow<List<Word>>
}