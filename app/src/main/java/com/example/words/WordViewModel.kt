package com.example.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {
    private val wordRepository by lazy { WordRepository(application) }

    val allWordsLive by lazy { wordRepository.getAllWordsLive().asLiveData(Dispatchers.IO) }

    fun insertWords(vararg words: Word) {
        viewModelScope.launch {
            wordRepository.insertWords(*words)
        }
    }

    fun updateWords(vararg words: Word) {
        viewModelScope.launch {
            wordRepository.updateWords(*words)
        }
    }

    fun deleteWords(vararg words: Word) {
        viewModelScope.launch {
            wordRepository.deleteWords(*words)
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            wordRepository.deleteAllWords()
        }
    }

    fun findWordsWithPattern(pattern: String) = wordRepository.findWordsWithPattern(pattern).asLiveData(Dispatchers.IO)
}