package com.wielabs.thewitness.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wielabs.thewitness.databinding.NewsItemSuggestionBinding
import com.wielabs.thewitness.model.News

class NewsSuggestionAdapter(private val listener: NewsSuggestionAdapterListener) :
    ListAdapter<News, NewsSuggestionAdapter.NewsSuggestionViewHolder>(NewsAdapter.NewsDiffUtil) {

    interface NewsSuggestionAdapterListener {
        fun onNewsSuggestionClick(view: View, news: News)
    }

    class NewsSuggestionViewHolder(private val binding: NewsItemSuggestionBinding, private val listener: NewsSuggestionAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.run {
                this.news = news
                this.listener = this@NewsSuggestionViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsSuggestionViewHolder =
        NewsSuggestionViewHolder(
            NewsItemSuggestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: NewsSuggestionViewHolder, position: Int) =
        holder.bind(getItem(position))
}