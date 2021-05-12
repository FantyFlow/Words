package com.example.words;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private final LiveData<List<Word>> allWordsLive;
    private final WordDao wordDao;

    public WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase.getWordDao();
        allWordsLive = wordDao.getAllWordsLive();
    }

    void insertWords(Word... words) {
        wordDao.insertWords(words);
    }

    void updateWords(Word... words) {
        wordDao.updateWords(words);
    }

    void deleteWords(Word... words) {
        wordDao.deleteWords(words);
    }

    void deleteAllWords() {
        wordDao.deleteAllWords();
    }

    LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }

    LiveData<List<Word>> findWordsWithPatten(String patten) {
        return wordDao.findWordsWithPatten("%" + patten + "%");
    }
}
