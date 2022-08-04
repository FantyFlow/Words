package com.example.words

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class WordRepository(context: Context) {
    private val wordDatabase by lazy { WordDatabase.getDatabase(context.applicationContext) }
    private val wordDao by lazy { wordDatabase.getWordDao() }

    suspend fun insertWords(vararg words: Word) = withContext(Dispatchers.IO) { wordDao.insertWords(*words) }

    suspend fun updateWords(vararg words: Word) = withContext(Dispatchers.IO) { wordDao.updateWords(*words) }

    suspend fun deleteWords(vararg words: Word) = withContext(Dispatchers.IO) { wordDao.deleteWords(*words) }

    suspend fun deleteAllWords() = withContext(Dispatchers.IO) { wordDao.deleteAllWords() }

    fun getAllWordsLive() = wordDao.getAllWordsLive().flowOn(Dispatchers.IO)

    fun findWordsWithPattern(pattern: String) = wordDao.findWordsWithPattern("%$pattern%").flowOn(Dispatchers.IO)
}