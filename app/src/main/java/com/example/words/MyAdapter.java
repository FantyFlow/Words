package com.example.words;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends ListAdapter<Word, MyAdapter.MyViewHolder> {
    boolean useCardView;
    private final WordViewModel wordViewModel;

    MyAdapter(boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal, parent, false);
        }
        MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://m.youdao.com/dict?le=eng&q=" + holder.textViewEnglish.getText());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            holder.itemView.getContext().startActivity(intent);
        });
        holder.aSwitchChineseInvisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
            if (isChecked) {
                holder.textViewChinese.setVisibility(View.GONE);
                word.setChineseInvisible(true);
            } else {
                holder.textViewChinese.setVisibility(View.VISIBLE);
                word.setChineseInvisible(false);
            }
            wordViewModel.updateWords(word);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder, word);
        holder.textViewNumber.setText(String.valueOf(position + 1));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());
        if (word.isChineseInvisible()) {
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisible.setChecked(true);
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyAdapter.MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewEnglish, textViewChinese;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch aSwitchChineseInvisible;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseInvisible = itemView.findViewById(R.id.switchChineseInvisible);
        }
    }
}
