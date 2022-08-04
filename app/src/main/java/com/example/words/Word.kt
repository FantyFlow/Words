package com.example.words

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Word(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "english_word")
    val word: String,
    @ColumnInfo(name = "chinese_meaning")
    val chineseMeaning: String,
    @ColumnInfo(name = "chinese_invisible")
    var chineseInvisible: Boolean = false
) : Parcelable
