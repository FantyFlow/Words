package com.example.words

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.words.databinding.CellCardBinding
import com.example.words.databinding.CellNormalBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class MyAdapter(
    private val useCardView: Boolean = false,
    private val wordViewModel: WordViewModel
) : ListAdapter<Word, MyAdapter.MyViewHolder>(
    object : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = if (useCardView) {
            MyViewHolder(CellCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            MyViewHolder(CellNormalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://dict.youdao.com/m/result?word=${holder.mTextViewEnglish.text}&lang=en")
            holder.itemView.context.startActivity(intent)
        }
        holder.mSwitchChineseInvisible.setOnCheckedChangeListener { _, b ->
            val word = getItem(holder.adapterPosition)
            holder.mTextViewChinese.isVisible = !b
            word.chineseInvisible = b
            wordViewModel.updateWords(word)
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val word = getItem(position)
        with(holder) {
            mTextViewNumber.text = itemView.context.getString(R.string.text_number, position + 1)
            mTextViewEnglish.text = word.word
            mTextViewChinese.text = word.chineseMeaning
            mTextViewChinese.isVisible = !word.chineseInvisible
            mSwitchChineseInvisible.isChecked = word.chineseInvisible
        }
    }

    inner class MyViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        val mTextViewNumber: TextView
        val mTextViewEnglish: TextView
        val mTextViewChinese: TextView
        val mSwitchChineseInvisible: SwitchMaterial

        init {
            when (viewBinding) {
                is CellNormalBinding -> with(viewBinding) {
                    mTextViewNumber = textViewNumber
                    mTextViewEnglish = textViewEnglish
                    mTextViewChinese = textViewChinese
                    mSwitchChineseInvisible = switchChineseInvisible
                }

                is CellCardBinding -> with(viewBinding) {
                    mTextViewNumber = textViewNumber
                    mTextViewEnglish = textViewEnglish
                    mTextViewChinese = textViewChinese
                    mSwitchChineseInvisible = switchChineseInvisible
                }

                else -> throw IllegalArgumentException()
            }
        }
    }
}